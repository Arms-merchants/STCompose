package com.example.stcompose.chat.ui.web

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stcompose.chat.ui.view.CommonTopBar
import com.google.accompanist.web.rememberWebViewState

/**
 *    author : heyueyang
 *    time   : 2023/05/26
 *    desc   :
 *    version: 1.0
 */
@Composable
fun WebPage(
    modifier: Modifier = Modifier,
    title: String,
    url: String,
    viewModel: WebPageModel = hiltViewModel()
) {
    val state = rememberWebViewState(url)
    LaunchedEffect(Unit) {
        viewModel.dispatch(
            WebAction.WebBean(
                title = title,
                url = url
            )
        )
    }

    Scaffold(modifier.fillMaxSize(), topBar = {
        CommonTopBar(title = {
            Marquee
        }, navigationIcon = { }, actions = {

        })
    }) {
        WebContent(Modifier.padding(it))
    }
}

@Composable
fun WebContent(modifier: Modifier = Modifier) {

}
