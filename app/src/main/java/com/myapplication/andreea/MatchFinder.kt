package com.myapplication.andreea

class MatchFinder {
    companion object {
        private fun findAllMatches(grid: List<GridItem>, gridSize: Int): Set<Int> {
            val matches = mutableSetOf<Int>()
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

        fun findPotentialMatches(grid: MutableList<GridItem>, gridSize: Int): Boolean {
            for (row in 0 until gridSize) {
                for (col in 0 until gridSize - 1) {
                    val index1 = row * gridSize + col
                    val index2 = index1 + 1
                    swap(grid, index1, index2)
                    if (findAllMatches(grid, gridSize).isNotEmpty()) {
                        swap(grid, index1, index2) // Swap back
                        return true
                    }
                    swap(grid, index1, index2) // Swap back
                }
            }

            for (col in 0 until gridSize) {
                for (row in 0 until gridSize - 1) {
                    val index1 = row * gridSize + col
                    val index2 = index1 + gridSize
                    swap(grid, index1, index2)
                    if (findAllMatches(grid, gridSize).isNotEmpty()) {
                        swap(grid, index1, index2) // Swap back
                        return true
                    }
                    swap(grid, index1, index2) // Swap back
                }
            }
            return false
        }

        private fun swap(grid: MutableList<GridItem>, index1: Int, index2: Int) {
            val temp = grid[index1]
            grid[index1] = grid[index2]
            grid[index2] = temp
        }
    }
}
