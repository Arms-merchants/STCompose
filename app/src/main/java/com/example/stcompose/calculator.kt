package com.example.stcompose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 *    author : heyueyang
 *    time   : 2023/04/20
 *    desc   :
 *    version: 1.0
 */
@Composable
fun CalculatorScreen() {

}

@Composable
fun KeyButton(modifier: Modifier = Modifier, handleClick: (key: String) -> Unit) {
    Box(modifier = modifier.clip(RoundedCornerShape(50.dp)))
}
