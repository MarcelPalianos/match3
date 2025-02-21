package com.myapplication.andreea


import android.os.Build
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random


class Match3Game(private val gridSize: Int = 6) {

    private val colors = listOf(
        Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta, Color.Cyan
    )

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

    fun onCellClicked(index: Int) {
        // Handle cell clicks (if needed for future features)
    }

    fun onSwipe(index: Int, direction: SwipeDirection, onGridUpdated: () -> Unit) {
        val targetIndex = getTargetIndex(index, direction)
        if (targetIndex != -1) {
            swapTiles(index, targetIndex, onGridUpdated)
        }
    }

    private fun getTargetIndex(index: Int, direction: SwipeDirection): Int {
        val row = index / gridSize
        val col = index % gridSize

        val targetRow = when (direction) {
            SwipeDirection.UP -> row - 1
            SwipeDirection.DOWN -> row + 1
            else -> row
        }

        val targetCol = when (direction) {
            SwipeDirection.LEFT -> col - 1
            SwipeDirection.RIGHT -> col + 1
            else -> col
        }

        if (targetRow < 0 || targetRow >= gridSize || targetCol < 0 || targetCol >= gridSize) {
            return -1
        }

        return targetRow * gridSize + targetCol
    }

    private fun swapTiles(index1: Int, index2: Int, onGridUpdated: () -> Unit) {
        val temp = grid[index1]
        grid[index1] = grid[index2]
        grid[index2] = temp

        CoroutineScope(Dispatchers.Default).launch {
            val matches = findAllMatches()
            if (matches.isEmpty()) {
                // No matches, swap back
                delay(100)
                swapTiles(index1, index2, onGridUpdated)
            } else {
                processMatches(matches, onGridUpdated)
            }
        }
    }

    private suspend fun processMatches(matches: Set<Int>, onGridUpdated: () -> Unit) {
        var currentMatches = matches
        while (currentMatches.isNotEmpty()) {
            removeMatches(currentMatches)
            applyGravity()
            withContext(Dispatchers.Main) {
                onGridUpdated()
            }
            delay(200)
            currentMatches = findAllMatches()
        }
    }

    private fun removeMatches(matches: Set<Int>) {
        val sortedMatches = matches.sortedDescending()
        for (index in sortedMatches) {
            grid.removeAt(index)
            grid.add(index, GridItem(nextId++, getRandomColor()))
        }
    }

    private fun applyGravity() {
        val newGrid = MutableList(gridSize * gridSize) { GridItem(-1, Color.Transparent) }
        for (col in 0 until gridSize) {
            val columnItems = mutableListOf<GridItem>()
            for (row in 0 until gridSize) {
                val index = row * gridSize + col
                if (index < grid.size) {
                    columnItems.add(grid[index])
                }
            }
            for (row in gridSize - 1 downTo 0) {
                val index = row * gridSize + col
                if (columnItems.isNotEmpty()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                        newGrid[index] = columnItems.removeLast()
                    }
                }
            }
        }
        grid.clear()
        grid.addAll(newGrid)
    }

    private fun findAllMatches(): Set<Int> {
        val matches = mutableSetOf<Int>()

        // Check horizontal matches
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize - 2) {
                val index1 = row * gridSize + col
                val index2 = index1 + 1
                val index3 = index2 + 1
                if (grid[index1].color == grid[index2].color && grid[index2].color == grid[index3].color) {
                    matches.addAll(listOf(index1, index2, index3))
                }
            }
        }

        // Check vertical matches
        for (col in 0 until gridSize) {
            for (row in 0 until gridSize - 2) {
                val index1 = row * gridSize + col
                val index2 = index1 + gridSize
                val index3 = index2 + gridSize
                if (grid[index1].color == grid[index2].color && grid[index2].color == grid[index3].color) {
                    matches.addAll(listOf(index1, index2, index3))
                }
            }
        }

        return matches
    }

    private fun getRandomColor(): Color {
        return colors[Random.nextInt(colors.size)]
    }
}