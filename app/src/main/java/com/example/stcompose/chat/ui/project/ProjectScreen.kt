package com.example.stcompose.chat.ui.project

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.stcompose.chat.HomeListItem
import com.example.stcompose.chat.LoadingContent
import com.example.stcompose.chat.data.bean.HomeListItemBean
import com.example.stcompose.chat.data.bean.ProjectTabItemBean
import com.example.stcompose.chat.model.ProjectModel
import com.example.stcompose.chat.model.ProjectUiState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

/**
 *    author : heyueyang
 *    time   : 2023/05/22
 *    desc   :
 *    version: 1.0
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProjectScreen(modifier: Modifier = Modifier, viewModel: ProjectModel = hiltViewModel()) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colors.primary)
            .fillMaxSize()
            .navigationBarsPadding()
            .systemBarsPadding()
    ) {
        val uiState by viewModel.uiState.collectAsState()
        val pagerState = rememberPagerState()
        Column(Modifier.background(MaterialTheme.colors.primary)) {
            when (uiState) {
                is ProjectUiState.HasData -> {
                    ProjectTabLayout(
                        tabList = (uiState as ProjectUiState.HasData).projectTabItemBean,
                        pagerState = pagerState, viewModel = viewModel
                    )
                    ProjectListPage(
                        tabList = (uiState as ProjectUiState.HasData).projectTabItemBean,
                        pagerState = pagerState,
                        viewModel = viewModel
                    )
                }

                else -> {

                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProjectTabLayout(
    modifier: Modifier = Modifier,
    tabList: List<ProjectTabItemBean>,
    pagerState: PagerState,
    viewModel: ProjectModel
) {
    val coroutineScope = rememberCoroutineScope()
    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = MaterialTheme.colors.primary,
        edgePadding = 1.dp
    ) {
        tabList.forEachIndexed { index, projectTabItemBean ->
            Tab(
                text = {
                    Text(
                        text = projectTabItemBean.name,
                        fontSize = if (pagerState.currentPage == index) 16.sp else 12.sp
                    )
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White,
            )
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProjectListPage(
    modifier: Modifier = Modifier,
    tabList: List<ProjectTabItemBean>,
    pagerState: PagerState,
    viewModel: ProjectModel
) {
    HorizontalPager(count = tabList.size, state = pagerState) { page ->
        tabList[page].apply {
            val pagingItems = viewModel.getProjectListData(id).collectAsLazyPagingItems()
            ProjectList(data = pagingItems)
        }
    }
}

@Composable
fun ProjectList(modifier: Modifier = Modifier, data: LazyPagingItems<HomeListItemBean>) {
    val refreshState = rememberSwipeRefreshState(isRefreshing = false)
    LoadingContent(loading = refreshState.isRefreshing, onRefresh = { data.refresh() }) {
        LazyColumn(content = {
            refreshState.isRefreshing = data.loadState.refresh == LoadState.Loading
            items(data) {
                if (it != null) {
                    HomeListItem(item = it)
                }
            }
        })
    }
}



