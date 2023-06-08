package com.example.stcompose.chat.ui.main

import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import com.example.stcompose.chat.ChatHomePage
import com.example.stcompose.chat.data.bean.BottomBean
import com.example.stcompose.chat.data.bean.ModelPath
import com.example.stcompose.chat.ui.account.PubAccountScreen
import com.example.stcompose.chat.ui.mine.MineScreen
import com.example.stcompose.chat.ui.project.ProjectScreen
import com.example.stcompose.chat.utils.navigate
import com.example.stcompose.ui.theme.caption

/**
 *    author : heyueyang
 *    time   : 2023/05/22
 *    desc   : 主页的架构，分为4个内容
 *    version: 1.0
 *    NavHost嵌套NavHost，但是NavController必须是单独各自设置，如果把父级别的controller传下来使用会报错。。。
 *    如果要使用一个NavController的话，现在采用的办法是全局使用脚手架包裹，在底部导航栏进行判断处理，只在主页显示
 */
fun NavGraphBuilder.chatMainGraph(navController: NavController) {
    navigation(startDestination = BottomBean.Home.route, route = "ChatMain") {
        composable(BottomBean.Home.route) {
            ChatHomePage() {
                val bundle = Bundle()
                bundle.putInt("type", 2)
                navController.navigate(route = ModelPath.Search.route, args = bundle)
            }
        }
        composable(BottomBean.Project.route) {
            ProjectScreen()
        }
        composable(BottomBean.PubAccount.route) {
            PubAccountScreen()
        }
        composable(BottomBean.Mine.route) {
            MineScreen()
        }
    }
}


@Composable
fun MainBottomBar(navController: NavController) {
    val items = listOf(
        BottomBean.Home,
        BottomBean.Project,
        BottomBean.PubAccount,
        BottomBean.Mine
    )
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEachIndexed { index, bottomBean ->
            BottomNavigationItem(
                selected = currentDestination?.hierarchy?.any { it.route == bottomBean.route } == true,
                onClick = {
                    val route = bottomBean.route
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true

                        popUpTo(BottomBean.Home.route) {
                            saveState = true
                        }

                    }
                },
                icon = {
                    Icon(
                        imageVector = bottomBean.icons,
                        contentDescription = bottomBean.name,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(text = bottomBean.name, style = caption)
                },
                alwaysShowLabel = false
            )

        }

    }
}
