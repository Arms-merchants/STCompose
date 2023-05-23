package com.example.stcompose.screen

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.stcompose.LoginPage
import com.example.stcompose.WelcomePage
import com.example.stcompose.brick.BrickPage
import com.example.stcompose.chat.data.bean.ModelPath
import com.example.stcompose.chat.ui.login.ChatLogin2
import com.example.stcompose.chat.ui.main.MainBottomBar
import com.example.stcompose.chat.ui.main.chatMainGraph
import com.example.stcompose.chat.ui.search.SearchScreen
import com.example.stcompose.ui.theme.StComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //沉浸式
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = false

        setContent {
            StComposeTheme {
                // A surface container using the 'background' color from the theme
                /*Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }*/
                //AppNavigation()
                AppSkipNavigation()
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun Greeting(str: String = "123") {
        Text(text = str)
    }


    @Composable
    fun AppSkipNavigation() {
        val navController = rememberNavController()
        val nacBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = nacBackStackEntry?.destination
        Scaffold(
            bottomBar = {
                if (currentDestination?.hierarchy?.any { it.route == "ChatMain" } == true) {
                    MainBottomBar(navController = navController)
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "SkipList",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("SkipList") {
                    SkipList() { type ->
                        when (type) {
                            "测试项目" -> {

                            }

                            "俄罗斯方块" -> {
                                navController.navigate("BrickPage")
                            }

                            "聊天项目" -> {
                                navController.navigate("Chat")
                            }
                        }
                    }
                }
                composable("BrickPage") {
                    BrickPage()
                }
                chatGraph(navController)
            }
        }
    }

    /**
     * 聊天项目的分组Graph
     *
     * @param navController
     */
    private fun NavGraphBuilder.chatGraph(navController: NavController) {
        navigation(startDestination = "loginPage", route = "Chat") {
            composable("loginPage") {
                ChatLogin2() {
                    navController.navigate("ChatMain")
                }
            }
            //主页：主页为基础空架构，通过脚手架实现内容展示区域和底部切换button
            //首页：顶部标题和搜索、banner、列表
            //项目：顶部导航条、导航内容对应的内容页面（内部列表）
            //公众号：顶部标题和搜索、导航、导航对应的内容界面
            //我的：顶部信息、功能菜单
            chatMainGraph(navController)
            composable(ModelPath.Search.route) {
                val type = it.arguments?.getInt("type", 1) ?: 1
                SearchScreen(type = type)
            }
        }
    }

    @Composable
    fun SkipList(handler: (String) -> Unit) {
        val list = listOf(
            "测试项目",
            "俄罗斯方块",
            "聊天项目"
        )
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(state = rememberLazyListState()) {
                items(list.size) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clickable {
                                handler.invoke(list[index])
                            }
                    ) {
                        Text(text = list[index])
                    }
                }
            }
        }

    }


    @Composable
    fun AppNavigation() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "welcome") {
            composable("welcome") {
                WelcomePage({
                    navController.navigate("login/123")
                }) {
                    val request = NavDeepLinkRequest.Builder.fromUri(
                        "android-app://stcompose/createAccount".toUri()
                    ).build()
                    navController.navigate(request = request)
                }
            }
            composable(
                "createAccount",
                deepLinks = listOf(navDeepLink {
                    uriPattern = "android-app://stcompose/createAccount"
                })
            ) {
                CreateAccountScreen()
            }
            //指定需要传递的参数名称并指定其类型
            //可选参数？argumentName={argumentName} 必填参数 /{argumentName}
            composable(
                "login/{userId}",
                arguments = listOf(navArgument("userId") {
                    //指定参数类型
                    type = NavType.StringType
                    //指定默认值,可选参数需要指定默认值
                    //defaultValue = "123"
                    defaultValue = null
                    //设置默认参数是否可为null
                    nullable = true
                })
            ) {
                //通过navBackStackEntry获取传递的参数
                LoginPage(it.arguments?.getString("userId")) {
                    //注意可选参数的调用方式，因为是可选参数，所以需要添加对应的参数名称
                    // navController.navigate("home?startIndex=${Screen.Cart.route}")
                    Log.e("TAG", "jump to home")
                    navController.navigate("home")
                    //通过deeplinks来进行跳转
                    /*  val request =
                          NavDeepLinkRequest.Builder.fromUri("android-app://stcompose/favorite".toUri())
                              .build()
                      navController.navigate(request = request)*/
                }
            }
            composable(
                "home?startIndex={startIndex}",
                arguments = listOf(navArgument("startIndex") {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                })
            ) {
                Log.e("TAG", "receiver event")
                BottomBarScreen(it.arguments?.getString("startIndex"))
            }
            /* //嵌套导航结构
             navigation(startDestination = "username", route = "login") {

             }
             //导航图变大时将其按这种方法拆分为多个方法，这也允许多个模块提交个自的导航图
             loginGraph(navController = navController)*/
        }
    }
}


fun NavGraphBuilder.loginGraph(navController: NavController) {
    navigation(startDestination = "username", route = "login") {

    }
}

