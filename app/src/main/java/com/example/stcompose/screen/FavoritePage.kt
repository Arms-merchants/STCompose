package com.example.stcompose.screen

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 *    author : heyueyang
 *    time   : 2023/04/10
 *    desc   :
 *    version: 1.0
 */


@Composable
fun FavoritePage() {
    val viewModel = viewModel<TViewModel>()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val background by animateColorAsState(
        targetValue = viewModel.background,
        animationSpec = tween(1000)
    )
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
        onRefresh = { viewModel.refresh() }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(background), contentAlignment = Alignment.Center
        ) {
            Text(text = "FavoritePage", style = TextStyle(color = Color.White))
        }
    }
}

@Composable
fun ProfilePage() {
    var imageUrl by remember {
        mutableStateOf("https://img1.baidu.com/it/u=413643897,2296924942&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500")
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            Text(text = "ProfilePage")
            Spacer(modifier = Modifier.height(10.dp))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true).build(),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            SubcomposeAsyncImage(
                model = imageUrl,
                loading = {
                    CircularProgressIndicator()
                },
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                imageUrl =
                    "https://img1.baidu.com/it/u=4049022245,514596079&fm=253&fmt=auto&app=120&f=JPEG?w=889&h=500"
            }) {
                Text(text = "修改图片链接")
            }

        }
    }


}


@Composable
fun CartPage(goBrickPage: () -> Unit) {
    val sysController = rememberSystemUiController()
    SideEffect {
        //四个界面一个调用设置了，那么就会都设置了，如果其他的页面不需要这个还要再调用一次
        sysController.setSystemBarsColor(Color.Blue)
    }
    TopAppBar(
        title = { Text(text = "购物车", color = Color.White) },
        backgroundColor = Color.Blue,
        modifier = Modifier.statusBarsPadding(),
    )
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = {
            Log.e("TAG", "跳转到俄罗斯方块页")
            goBrickPage.invoke()
        }) {
            Text(text = "跳转到俄罗斯方块页")
        }
    }
}

