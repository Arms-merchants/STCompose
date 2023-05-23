package com.example.stcompose.chat.ui.mine

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 *    author : heyueyang
 *    time   : 2023/05/22
 *    desc   :
 *    version: 1.0
 */
@Composable
fun MineScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(text = "我的", modifier = modifier.align(Alignment.Center))
    }
}