package com.example.stcompose.screen

import android.util.Log
import androidx.annotation.StringRes
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.stcompose.HomePage
import com.example.stcompose.R
import com.example.stcompose.brick.BrickPage
import com.example.stcompose.ui.theme.caption
import com.example.stcompose.ui.theme.pink100

/**
 *    author : heyueyang
 *    time   : 2023/04/11
 *    desc   :
 *    version: 1.0
 */


/*
fun NavGraphBuilder.bottomBarScreen(navController: NavController, startRoute: String? = null) {
    navigation(startDestination = startRoute ?: Screen.Home.route, route = "bottomBar") {
        composable(Screen.Home.route) { HomePage() }
        val uri = "android-app://stcompose"
        composable(Screen.Favorite.route, deepLinks = listOf(
            navDeepLink { uriPattern = "$uri/favorite" }
        )) { FavoritePage() }
        composable(Screen.Profile.route) { ProfilePage() }
        composable(Screen.Cart.route) {
            CartPage()
        }
    }
}
*/

@Composable
fun BottomBarScreen(startRoute: String?) {
    val navController = rememberNavController()
    Scaffold(bottomBar = {
        BottomBar(navController)
    }) { innerPadding ->
        //找到deeplink无发跳转的问题了，这里单独创建了NavHost，所以和MainActivity里的不是一个分组，
        //那么如果要这么做的话，需要创建的app结构因为为App里有rootGraph
        NavHost(
            navController = navController,
            startDestination = startRoute ?: Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomePage() }
            val uri = "android-app://stcompose"
            composable(Screen.Favorite.route, deepLinks = listOf(
                navDeepLink { uriPattern = "$uri/favorite" }
            )) { FavoritePage() }
            composable(Screen.Profile.route) { ProfilePage() }
            composable(Screen.Cart.route) {
                CartPage() {
                    Log.e("TAG", "jump")
                    navController.navigate("brickpage")
                }
            }
            composable("brickpage"){
                BrickPage()
            }
            //testGraph(navController = navController)
        }
    }
}

/**
 * 使用扩展方法提供子model的graph，对外不需要暴露实现细节，自己内部的跳转自己去处理
 */
fun NavGraphBuilder.testGraph(navController: NavHostController) {
    navigation(startDestination = "test", route = "test") {

    }
}


sealed class Screen(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    object Home : Screen("home", R.string.home, Icons.Filled.Home)
    object Favorite :
        Screen("favorite", R.string.favorite, Icons.Filled.Favorite)

    object Profile : Screen("profile", R.string.profile, Icons.Filled.Person)
    object Cart : Screen("cart", R.string.cart, Icons.Filled.ShoppingCart)
}

fun NavController.navigateAndArgument(
    route: String,
    args: List<Pair<String, Any>>? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    navigate(route = route, navOptions = navOptions, navigatorExtras)
    if (args == null && args?.isEmpty() == true) {
        return
    }
    val bundle = backQueue.lastOrNull()?.arguments
    if (bundle != null) {
        bundle.putAll(bundleOf(*args?.toTypedArray()!!))
    } else {
        println("The last argument of NavBackStackEntry is NULL")
    }
}


@Composable
fun BottomBar(navController: NavHostController) {
    var selectedItem by remember {
        mutableStateOf(0)
    }
    val items = listOf(
        Screen.Home,
        Screen.Favorite,
        Screen.Profile,
        Screen.Cart
    )
    BottomNavigation(
        backgroundColor = pink100, modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEachIndexed { index, item ->
            BottomNavigationItem(
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    var route = item.route
                    if (route == Screen.Cart.route) {
                        route += "?id=123"
                    }

                    navController.navigate(route) {
                        //点击item时，清空栈内到NavoptionsBuilder.popUpTo Id 之间的所有item
                        //避免栈内节点的持续正价，同时saveState用于页面状态的恢复
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        //避免点击同一个底部icon创建出多个实例
                        launchSingleTop = true
                        //再次点击之前的Item时，恢复状态
                        restoreState = true
                    }

                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(id = item.resourceId),
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(stringResource(id = item.resourceId), style = caption) }
            )
        }
    }
}

