package com.myapplication.andreea

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    companion object {
        const val GRID_SIZE = 6
    }

    var gridItems = mutableStateListOf<GridItem>()
        private set

    // Create an instance of MatchFinder
    private val matchFinder = MatchFinder()

    init {
        initializeGrid()
    }

    private fun initializeGrid() {
        val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta)
        var idCounter = 0
        for (y in 0 until GRID_SIZE) {
            for (x in 0 until GRID_SIZE) {
                gridItems.add(GridItem(idCounter++, colors.random(), x, y))
            }
        }
    }

    fun onSwipe(index: Int, swipeDirection: SwipeDirection) {
        val currentItem = gridItems[index]
        val targetIndex = getTargetIndex(index, swipeDirection)

        if (targetIndex != -1) {
            val targetItem = gridItems[targetIndex]
            swapItems(currentItem, targetItem)
            // Call findAllMatches on the matchFinder instance
            val matches = matchFinder.findAllMatches(gridItems, GRID_SIZE)
            if (matches.isNotEmpty()) {
                removeMatches(matches)
            } else {
                swapItems(targetItem, currentItem)
            }
        }
    }

    private fun swapItems(item1: GridItem, item2: GridItem) {
        val index1 = gridItems.indexOf(item1)
        val index2 = gridItems.indexOf(item2)

        if (index1 != -1 && index2 != -1) {
            gridItems[index1] = item2.copy(x = item1.x, y = item1.y)
            gridItems[index2] = item1.copy(x = item2.x, y = item2.y)
        }
    }

    private fun getTargetIndex(index: Int, swipeDirection: SwipeDirection): Int {
        val x = index % GRID_SIZE
        val y = index / GRID_SIZE

        return when (swipeDirection) {
            SwipeDirection.UP -> if (y > 0) index - GRID_SIZE else -1
            SwipeDirection.DOWN -> if (y < GRID_SIZE - 1) index + GRID_SIZE else -1
            SwipeDirection.LEFT -> if (x > 0) index - 1 else -1
            SwipeDirection.RIGHT -> if (x < GRID_SIZE - 1) index + 1 else -1
            SwipeDirection.NONE -> -1
        }
    }

    private fun removeMatches(matches: Set<Int>) {
        matches.forEach { index ->
            gridItems[index] = gridItems[index].copy(color = Color.Transparent)
        }
    }
}