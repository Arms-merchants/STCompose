package com.example.stcompose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.stcompose.ui.theme.*

/**
 *    author : heyueyang
 *    time   : 2023/03/13
 *    desc   : 一个欢迎页并使用NavController来实现跳转
 *    version: 1.0
 */

@Composable
fun WelcomePage(onNavigateToLogin: () -> Unit, onNavigateToCreate: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
    ) {
        Image(
            painter = rememberVectorPainter(image = ImageVector.vectorResource(id = MaterialTheme.stAssets.background)),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        WelcomePageContent(onNavigateToLogin, onNavigateToCreate)
    }
}

@Composable
fun WelcomePageContent(onNavigateToLogin: () -> Unit, onNavigateToCreate: () -> Unit) {
    Column {
        Spacer(modifier = Modifier.height(72.dp))
        TopImage()
        Spacer(modifier = Modifier.height(48.dp))
        Title()
        Spacer(modifier = Modifier.height(40.dp))
        BottomButton(onNavigateToLogin, onNavigateToCreate)
    }
}

@Composable
fun TopImage() {
    Image(
        painter = rememberVectorPainter(image = ImageVector.vectorResource(id = MaterialTheme.stAssets.illos)),
        contentDescription = null,
        modifier = Modifier.padding(start = 88.dp)
    )
}

@Composable
fun Title() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = rememberVectorPainter(image = ImageVector.vectorResource(id = MaterialTheme.stAssets.logo)),
            contentDescription = null,
            modifier = Modifier.wrapContentWidth()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text = "Beautiful home garden solutions",
                style = subtitle1,
                color = MaterialTheme.colors.onPrimary
            )
        }
    }
}

@Composable
fun BottomButton(onNavigateToLogin: () -> Unit, onNavigateToCreate: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = { onNavigateToCreate.invoke() }, modifier = Modifier
                .height(48.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondary,
                contentColor = MaterialTheme.colors.onSecondary
            ),
            shape = medium
        ) {
            Text("Create account", style = button)
        }
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(onClick = {
            val userId = "111"
            //传递参数，因为Compose的参数传递是基于Navigation的Deeplinks方式来实现的，Deeplinks参数暂不支持对象类型，只能使用基本类型传参
            /*navController.navigate("login/${userId}") {
                //popUpTo如果不加后面的inclusive，那么就是将welcome到login中间的所有节点都退出，如果包含的就是把welcome也退出
                //例如现在的例子，在welcome到login之后，login之前不在有组件，在login回退的话就回回到桌面
                popUpTo("welcome") { inclusive = true }
                //当栈顶已经是login的时候，则不会重新入栈login节点
                launchSingleTop = true
            }*/
            //使用login的默认参数
            //navController.navigate("login")
            //最佳实践不要将navController传递到下层组件来进行跳转，使用回调来来实现状态上提，在顶层节点处理
            onNavigateToLogin.invoke()
        }) {
            Text(
                text = "Log in",
                style = button,
                color = pink900
            )
        }
    }
}


