package com.myapplication.andreea

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel


class GameViewModel : ViewModel() {
    private val game = Match3Game()
    private val _gridItems = mutableStateListOf<GridItem>()
    val gridItems: List<GridItem> = _gridItems

    init {
        _gridItems.addAll(game.getGrid())
    }

    fun onSwipe(index: Int, direction: SwipeDirection) {
        game.onSwipe(index, direction) {
            _gridItems.clear()
            _gridItems.addAll(game.getGrid())
        }
    }
}