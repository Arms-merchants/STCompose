package com.example.stcompose.screen

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.stcompose.LoginPage
import com.example.stcompose.WelcomePage
import com.example.stcompose.brick.BrickPage
import com.example.stcompose.ui.theme.StComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //沉浸式
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = true

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
                BrickPage()
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

