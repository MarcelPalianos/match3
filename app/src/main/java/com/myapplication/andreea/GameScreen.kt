package com.myapplication.andreea

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val gridItems = viewModel.gridItems

    LazyVerticalGrid(
        columns = GridCells.Fixed(6),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        itemsIndexed(gridItems) { index, item ->
            GridItem(item, index, viewModel::onSwipe)
        }
    }
}

@Composable
fun GridItem(
    item: GridItem,
    index: Int,
    onSwipe: (Int, SwipeDirection) -> Unit
) {
    var lockDirection by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .size(60.dp)
            .background(item.color, shape = MaterialTheme.shapes.medium)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        if (!lockDirection) {
                            val swipeDirection = calculateSwipeDirection(dragAmount.x, dragAmount.y)
                            if (swipeDirection != SwipeDirection.NONE) {
                                lockDirection = true
                                onSwipe(index, swipeDirection)
                            }
                        }
                    },
                    onDragEnd = {
                        lockDirection = false
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // You can add content here if needed, like an icon or text
    }
}

fun calculateSwipeDirection(dragAmountX: Float, dragAmountY: Float): SwipeDirection {
    val threshold = 30
    return when {
        abs(dragAmountX) > abs(dragAmountY) -> {
            if (dragAmountX > threshold) {
                SwipeDirection.RIGHT
            } else if (dragAmountX < -threshold) {
                SwipeDirection.LEFT
            } else {
                SwipeDirection.NONE
            }
        }

        abs(dragAmountY) > abs(dragAmountX) -> {
            if (dragAmountY > threshold) {
                SwipeDirection.DOWN
            } else if (dragAmountY < -threshold) {
                SwipeDirection.UP
            } else {
                SwipeDirection.NONE
            }
        }

        else -> SwipeDirection.NONE
    }
}