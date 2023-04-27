package com.example.stcompose.brick

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 *    author : heyueyang
 *    time   : 2023/04/20
 *    desc   :
 *    version: 1.0
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun SupPopScreen() {
    val viewModel = viewModel<GameViewModel>()
    val animat = rememberInfiniteTransition()
    val degrees = animat.animateFloat(
        initialValue = 45f,
        targetValue = -45f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500),
            repeatMode = RepeatMode.Reverse
        )
    )
    val brush = Brush.horizontalGradient(
        listOf(
            Color(0xFF22B6FF),
            Color(0xFFB732FF),
            Color(0xFFFF1D37)
        )
    )

    val textStyle = MaterialTheme.typography.body1.copy(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        brush = brush
    )
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "呦呦，升级了啊",
            modifier = Modifier
                .rotate(degrees.value),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            style = textStyle
        )
    }
}