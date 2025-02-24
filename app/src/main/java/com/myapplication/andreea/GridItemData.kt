package com.myapplication.andreea

import androidx.compose.ui.graphics.Color

data class GridItem(
    val id: Int,
    val color: Color,
    var x: Int = 0, // Column position
    var y: Int = 0, // Row position
    var content: String? = null, // Optional content (e.g., text)
    var isSelected: Boolean = false, // Example state
    var isHighlighted: Boolean = false, // Example state
)