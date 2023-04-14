package com.example.stcompose

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 *    author : heyueyang
 *    time   : 2023/03/14
 *    desc   :
 *    version: 1.0
 */

/**
 * CompositionLocal为了在Compose树中进行数据传递，提供两种创建方式staticCompositionLocalOf
 */
@Composable
fun CompositionDemo() {
    //每个Compose树层级所取到值都不一样
    val localString = staticCompositionLocalOf { "Jectpack Compose" }
    Column {
        CompositionLocalProvider(localString provides "Hello World") {
            Text(text = localString.current)
            CompositionLocalProvider(localString provides "Reguer McCarthy") {
                Text(text = localString.current)
            }
        }
        Text(text = localString.current)
    }
}

var recomposeFlag = "No Recompose"

@Composable
fun CompositionDemo2() {

    val currentLocalColor = compositionLocalOf { Color.Black }
    var color by remember {
        mutableStateOf(Color.Green)
    }
    CompositionLocalProvider(currentLocalColor provides color) {
        Column() {
            Log.e("TAG", "CompositionLocalProvider")
            Button(onClick = { color = Color.Gray }) {
                Text(text = "修改颜色")
            }
            Spacer(modifier = Modifier.height(100.dp))
            TaggedBox(string = "Wrapper:${recomposeFlag}", size = 400.dp, color = Color.Magenta) {
                TaggedBox(
                    string = "Middle:${recomposeFlag}",
                    size = 300.dp,
                    color = currentLocalColor.current
                ) {
                    TaggedBox(
                        string = "Inner:${recomposeFlag}",
                        size = 200.dp,
                        color = Color.Yellow
                    ) {

                    }
                }
            }
        }
    }
}

@Composable
fun TaggedBox(
    string: String, size: Dp, color: Color,
    content: @Composable BoxScope.() -> Unit
) {
    var t by remember {
        mutableStateOf(string)
    }
    Box(
        modifier = Modifier
            .size(size)
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Log.e("TAG", "Box")
        Text(text = t, Modifier.align(Alignment.TopCenter))
        content()
    }
}
