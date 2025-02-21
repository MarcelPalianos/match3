package com.myapplication.andreea


import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.*
import kotlin.random.Random

class Match3Game(private val gridSize: Int = 6) {
    private val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta, Color.Cyan, Color.Gray, Color.White)
    private var nextId = 0
    private var grid = mutableListOf<GridItem>()

    init {
        initializeGrid()
    }

    fun getGrid(): List<GridItem> = grid

    private fun initializeGrid() {
        grid.clear()
        repeat(gridSize * gridSize) {
            grid.add(GridItem(nextId++, getRandomColor()))
        }
    }

    fun onSwipe(index: Int, direction: SwipeDirection, onGridUpdated: () -> Unit) {
        val targetIndex = getTargetIndex(index, direction)
        if (targetIndex != -1) {
            swapTiles(index, targetIndex, onGridUpdated)
        }
    }

    private fun getTargetIndex(index: Int, direction: SwipeDirection): Int {
        return when (direction) {
            SwipeDirection.LEFT -> if (index % gridSize > 0) index - 1 else -1
            SwipeDirection.RIGHT -> if (index % gridSize < gridSize - 1) index + 1 else -1
            SwipeDirection.UP -> if (index >= gridSize) index - gridSize else -1
            SwipeDirection.DOWN -> if (index < gridSize * (gridSize - 1)) index + gridSize else -1
            SwipeDirection.NONE -> -1
        }
    }

    private fun swapTiles(index1: Int, index2: Int, onGridUpdated: () -> Unit) {
        val temp = grid[index1]
        grid[index1] = grid[index2]
        grid[index2] = temp

        CoroutineScope(Dispatchers.Default).launch {
            val matches = MatchFinder.findAllMatches(grid, gridSize)
            if (matches.isEmpty()) {
                delay(100)
                withContext(Dispatchers.Main) {
                    val tempSwap = grid[index1]
                    grid[index1] = grid[index2]
                    grid[index2] = tempSwap
                    onGridUpdated()
                }
            } else {
                processMatches(matches, onGridUpdated)
            }
        }
    }

    private suspend fun processMatches(matches: Set<Int>, onGridUpdated: () -> Unit) {
        var currentMatches = matches
        while (currentMatches.isNotEmpty()) {
            removeMatches(currentMatches)
            applyGravity(onGridUpdated)
            withContext(Dispatchers.Main) { onGridUpdated() }
            delay(200)
            currentMatches = MatchFinder.findAllMatches(grid, gridSize)
        }
    }

    private fun removeMatches(matches: Set<Int>) {
        matches.forEach { index ->
            grid[index] = GridItem(nextId++, Color.Transparent)
        }
    }

    private fun applyGravity(onGridUpdated: () -> Unit) {
        for (col in 0 until gridSize) {
            val columnItems = mutableListOf<GridItem>()
            for (row in 0 until gridSize) {
                val index = row * gridSize + col
                if (grid[index].color != Color.Transparent) {
                    columnItems.add(grid[index])
                }
            }

            while (columnItems.size < gridSize) {
                columnItems.add(0, GridItem(nextId++, getRandomColor()))
            }

            for (row in 0 until gridSize) {
                grid[row * gridSize + col] = columnItems[row]
            }
        }

        // 🔥 After gravity applies, check for moves immediately
        if (!MatchFinder.findPotentialMatches(grid, gridSize)) {
            handleNoMoreMoves(onGridUpdated)
        } else {
            onGridUpdated() // If moves exist, update the UI
        }
    }
    private fun reshuffleGrid(onGridUpdated: () -> Unit) {
        do {
            grid.shuffle() // Randomly shuffle tiles
        } while (!MatchFinder.findPotentialMatches(grid, gridSize)) // Ensure a valid move exists

        println("Grid reshuffled!")
        onGridUpdated() // Notify UI to re-render
    }
    private fun handleNoMoreMoves(onGridUpdated: () -> Unit) {
        println("No more moves! Reshuffling grid...")
        reshuffleGrid(onGridUpdated) // Reshuffle instead of resetting
    }



    private fun getRandomColor(): Color = colors[Random.nextInt(colors.size)]
}