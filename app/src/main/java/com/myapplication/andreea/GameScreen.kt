package com.myapplication.andreea

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun GridItem(
    item: GridItem,
    index: Int,
    onSwipe: (Int, SwipeDirection) -> Unit
) {
    // State variables for animation and drag
    val animatedOffsetX = remember { Animatable(0f) }
    val animatedOffsetY = remember { Animatable(0f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = item.x, key2 = item.y) {
        coroutineScope.launch {
            animatedOffsetX.animateTo(
                targetValue = item.x * 64f,
                animationSpec = tween(durationMillis = 500)
            )
        }
        coroutineScope.launch {
            animatedOffsetY.animateTo(
                targetValue = item.y * 64f,
                animationSpec = tween(durationMillis = 500)
            )
        }
    }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    (animatedOffsetX.value + offsetX).roundToInt(),
                    (animatedOffsetY.value + offsetY).roundToInt()
                )
            }
            .size(60.dp)
            .background(item.color, shape = MaterialTheme.shapes.medium)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    onDragEnd = {
                        isDragging = false
                        val swipeDirection = calculateSwipeDirection(offsetX, offsetY)
                        if (swipeDirection != SwipeDirection.NONE) {
                            onSwipe(index, swipeDirection)
                        }
                        offsetX = 0f
                        offsetY = 0f
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // ... (Your content for the GridItem)
    }
}

fun calculateSwipeDirection(dragAmountX: Float, dragAmountY: Float): SwipeDirection {
    val threshold = 10f
    val absX = abs(dragAmountX)
    val absY = abs(dragAmountY)

    return when {
        absX > absY -> {
            when {
                dragAmountX > threshold -> SwipeDirection.RIGHT
                dragAmountX < -threshold -> SwipeDirection.LEFT
                else -> SwipeDirection.NONE
            }
        }

        absY > absX -> {
            when {
                dragAmountY > threshold -> SwipeDirection.DOWN
                dragAmountY < -threshold -> SwipeDirection.UP
                else -> SwipeDirection.NONE
            }
        }

        else -> SwipeDirection.NONE
    }
}