package com.example.stcompose.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.stcompose.HomePage
import com.example.stcompose.ui.theme.caption
import com.example.stcompose.ui.theme.pink100
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

/**
 *    author : heyueyang
 *    time   : 2023/04/11
 *    desc   :
 *    version: 1.0
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun CreateAccountScreen() {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    var selectIndex by remember { mutableStateOf(0) }
    val screens = listOf(
        CreateScreen.Home,
        CreateScreen.Favorite,
        CreateScreen.Profile,
        CreateScreen.Cart
    )
    Scaffold(bottomBar = {
        CreateBottomBar(selectIndex, screens = screens, onClick = {
            selectIndex = it
            scope.launch {
                pagerState.scrollToPage(selectIndex)
            }
        })
    }) { paddingValues ->
        HorizontalPager(
            count = screens.size,
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = pagerState
        ) { page ->
            screens.forEachIndexed { index, createScreen ->
                when (page) {
                    index -> createScreen.content()
                }
            }
        }

        /*VerticalPager(
            count = screens.size,
            Modifier
                .fillMaxSize()
                .padding(paddingValues), state = pagerState
        ) { page ->
            screens.forEachIndexed { index, createScreen ->
                when (page) {
                    index -> createScreen.content()
                }
            }
        }*/
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            selectIndex = page
        }
    }
}

@Composable
fun CreateBottomBar(
    selectIndex: Int,
    screens: List<CreateScreen>,
    onClick: (targetIndex: Int) -> Unit
) {
    BottomNavigation(
        backgroundColor = pink100, modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        screens.forEachIndexed { index, item ->
            BottomNavigationItem(
                selected = index == selectIndex,
                onClick = { onClick(index) },
                icon = {
                    Icon(
                        imageVector = item.icon, contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(text = item.label, style = caption) }
            )
        }

    }
}

sealed class CreateScreen(
    val label: String,
    val icon: ImageVector,
    val content: @Composable () -> Unit,
) {

    object Home : CreateScreen("home", Icons.Filled.Home, { HomePage() })
    object Favorite : CreateScreen("favorite", Icons.Filled.Favorite, { FavoritePage() })
    object Profile : CreateScreen("profile", Icons.Filled.Person, { ProfilePage() })
    object Cart : CreateScreen("cart", Icons.Filled.ShoppingCart, {
        CartPage() {

        }
    })
}



