package com.example.stcompose.chat

import android.text.TextUtils
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.example.stcompose.chat.data.bean.BannerResponse
import com.example.stcompose.chat.data.bean.HomeListItemBean
import com.example.stcompose.chat.model.HomeModel
import com.example.stcompose.chat.model.HomeUiState
import com.example.stcompose.chat.ui.view.CommonTopBar
import com.example.stcompose.chat.utils.filterHtml
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay

/**
 *    author : heyueyang
 *    time   : 2023/05/06
 *    desc   : 作为首页的容器
 *    version: 1.0
 */
@Composable
fun ChatHomePage(viewModel: HomeModel = hiltViewModel(), toSearchPage: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val listData = (uiState as HomeUiState.HasData).homeListData.collectAsLazyPagingItems()
    val listState = if (listData.itemCount > 0) viewModel.listState else LazyListState()
    val refreshState = rememberSwipeRefreshState(isRefreshing = false)
    Scaffold(
        topBar = {
            CommonTopBar(title = "玩Android", action = {
                toSearchPage.invoke()
            }, backgroundColor = MaterialTheme.colors.primarySurface)
        }
    ) { paddingValues ->
        LoadingContent(
            Modifier.padding(paddingValues),
            loading = refreshState.isRefreshing,
            onRefresh = { listData.refresh() }) {
            refreshState.isRefreshing = listData.loadState.refresh is LoadState.Loading
            when (uiState) {
                is HomeUiState.HasData -> {
                    LazyColumn(state = listState) {
                        item {
                            (uiState as HomeUiState.HasData).bannerResponse?.let {
                                HomeBanner(
                                    modifier = Modifier.height(160.dp),
                                    list = it
                                )
                            }
                        }
                        items(listData, key = { message -> message.id }) {
                            if (it != null) {
                                HomeListItem(item = it)
                            }
                        }
                    }
                }

                else -> {

                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeBanner(modifier: Modifier = Modifier, list: List<BannerResponse>) {
    val pagerState = rememberPagerState()
    BoxWithConstraints(modifier) {
        HorizontalPager(count = list.size, state = pagerState) {
            BannerItem(
                Modifier
                    .height(maxHeight)
                    .fillMaxWidth(),
                data = list[pagerState.currentPage]
            )
        }
        if (list.isNotEmpty()) {
            BannerIndicator(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.2f))
                    .padding(8.dp),
                list = list,
                pagerState = pagerState
            )
        }
    }
}

@Composable
fun BannerItem(modifier: Modifier = Modifier, data: BannerResponse) {
    AsyncImage(
        modifier = modifier,
        model = data.imagePath,
        contentDescription = null,
        contentScale = ContentScale.FillBounds
    )
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun BannerIndicator(
    modifier: Modifier = Modifier,
    list: List<BannerResponse>,
    pagerState: PagerState
) {
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
    if (!isDragged) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(3000L)
                if (pagerState.currentPage < list.size - 1) {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                } else {
                    pagerState.scrollToPage(0)
                }
            }
        }
    }
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = list[pagerState.currentPage].title,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp
        )
        Row(
            Modifier
                .weight(1f)
                .wrapContentWidth(Alignment.End)
        ) {
            repeat(list.size) {
                val color = if (it == pagerState.currentPage) {
                    MaterialTheme.colors.primarySurface
                } else {
                    Color.White
                }
                Canvas(modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(8.dp), onDraw = {
                    drawCircle(color = color)
                })
            }
        }
    }
}


@Composable
fun LoadingContent(
    modifier: Modifier = Modifier,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    SwipeRefresh(
        modifier = modifier,
        state = rememberSwipeRefreshState(isRefreshing = loading),
        onRefresh = onRefresh,
        content = content
    )
}


@Composable
fun HomeListItem(modifier: Modifier = Modifier, item: HomeListItemBean) {
    Card(modifier.padding(8.dp), backgroundColor = Color.White, elevation = 5.dp) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            val (author, time, title, from, favorite, topTip, cover, desc) = createRefs()
            Text(text = run {
                if (TextUtils.isEmpty(item.author)) {
                    item.shareUser
                } else {
                    item.author
                }
            }, fontSize = 12.sp, modifier = Modifier.constrainAs(author) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            })
            if (item.type == 1) {
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.Red, RoundedCornerShape(5.dp))
                        .padding(4.dp)
                        .constrainAs(topTip) {
                            baseline.linkTo(author.baseline)
                            start.linkTo(author.end, 5.dp)
                        }) {
                    Text(
                        text = "置顶", fontSize = 10.sp, color = Color.Red
                    )
                }
            }
            Text(
                text = item.niceDate,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.constrainAs(time) {
                    end.linkTo(parent.end)
                    top.linkTo(author.top)
                    bottom.linkTo(author.bottom)
                })

            if (!TextUtils.isEmpty(item.envelopePic)) {
                AsyncImage(model = item.envelopePic, contentDescription = item.envelopePic,
                    modifier
                        .size(100.dp)
                        .constrainAs(cover) {
                            top.linkTo(author.bottom, 10.dp)
                            start.linkTo(parent.start)
                        }
                )
            }
            Text(
                text = item.title.filterHtml(),
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Start,
                fontSize = 16.sp, modifier = Modifier
                    .constrainAs(title) {
                        top.linkTo(author.bottom, 10.dp)
                        start.linkTo(cover.end, 10.dp, goneMargin = 0.dp)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
            )
            if (!TextUtils.isEmpty(item.desc)) {
                Text(
                    text = item.desc,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 3,
                    modifier = Modifier.constrainAs(desc) {
                        start.linkTo(title.start)
                        end.linkTo(parent.end)
                        top.linkTo(title.bottom, 2.dp)
                        width = Dimension.fillToConstraints
                    }, overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = item.superChapterName,
                fontSize = 12.sp,
                modifier = Modifier.constrainAs(from) {
                    val topLink = if(TextUtils.isEmpty(item.desc))title.bottom else desc.bottom
                    top.linkTo(topLink, 10.dp)
                    start.linkTo(parent.start)
                })
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier
                    .constrainAs(favorite) {
                        end.linkTo(parent.end)
                        top.linkTo(from.top)
                        bottom.linkTo(from.bottom)
                    }
                    .size(24.dp)
            )
        }
    }
}
