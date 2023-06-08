package com.example.stcompose.chat.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 *    author : heyueyang
 *    time   : 2023/05/22
 *    desc   :
 *    version: 1.0
 */

@Composable
fun CommonTopBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    backgroundColor: Color = Color.Transparent
) {
    Column(
        modifier
            .navigationBarsPadding()
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = title,
            //利用run，也就是当back不为null的时候才会将后面的函数对象返回，不然就是空，为空的话那么
            //TopAppBar前面就不会设置图标
            navigationIcon = navigationIcon,
            actions = actions,
            modifier = Modifier.padding(
                WindowInsets.statusBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                    .asPaddingValues()
            )
        )
    }
}


@Composable
fun CommonTopBar(
    modifier: Modifier = Modifier,
    title: String,
    contentAlignment: Alignment = Alignment.TopStart,
    backgroundColor: Color = Color.Transparent,
    back: (() -> Unit)? = null,
    action: (() -> Unit)? = null
) {
    Column(
        modifier
            .navigationBarsPadding()
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = {
                Text(text = title, color = Color.White)
            },
            //利用run，也就是当back不为null的时候才会将后面的函数对象返回，不然就是空，为空的话那么
            //TopAppBar前面就不会设置图标
            navigationIcon = back?.run {
                {
                    IconButton(onClick = { invoke() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "back",
                            tint = Color.White
                        )
                    }
                }
            },
            actions = {
                action?.run {
                    IconButton(onClick = { invoke() }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "search",
                            tint = Color.White
                        )
                    }
                }
            },
            modifier = Modifier.padding(
                WindowInsets.statusBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                    .asPaddingValues()
            )
        )
    }
}