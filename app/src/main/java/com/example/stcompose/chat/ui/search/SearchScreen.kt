package com.example.stcompose.chat.ui.search

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
fun SearchScreen(modifier: Modifier = Modifier, type: Int = 1) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(text = "搜索${type}", Modifier.align(Alignment.Center))
    }
}