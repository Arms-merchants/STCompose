package com.example.stcompose

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.stcompose.ui.theme.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 *    author : heyueyang
 *    time   : 2023/03/13
 *    desc   :
 *    version: 1.0
 */
@Composable
fun LoginPage(userId: String?, onNavigateToHome: () -> Unit) {
    Log.e("TAG", "userId:${userId}")
    val systemUIController = rememberSystemUiController()
    SideEffect {
        //控制状态栏的颜色
        systemUIController.setSystemBarsColor(
            color = Color.Yellow
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = white)
            .padding(horizontal = 16.dp)
    ) {
        LoginTitle()
        LoginInputs()
        LoginTips()
        LoginBottomButton(onNavigateToHome)
    }
}

@Composable
fun LoginTitle() {
    Text(
        text = "Log in with email",
        style = h1,
        color = gray,
        modifier = Modifier
            .fillMaxWidth()
            .paddingFromBaseline(top = 184.dp, bottom = 16.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun LoginInputs() {
    LoginInput(placeholder = "Email address")
    Spacer(modifier = Modifier.height(8.dp))
    LoginInput(placeholder = "Password(8+characters)")

}

@Composable
fun LoginInput(placeholder: String) {
    OutlinedTextField(value = "", onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(small),
        placeholder = {
            Text(text = placeholder, style = body1, color = gray)
        })
}

@Composable
fun LoginTips() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .paddingFromBaseline(top = 24.dp, bottom = 16.dp)
    ) {
        LoginTopText()
        LoginBottomText()
    }
}

@Composable
fun LoginTopText() {
    val annotatedString = buildAnnotatedString {
        append("By clicking below,you agree to our ")
        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
            append("Terms of Use")
        }
        append(" and consent")
    }
    Text(
        text = annotatedString,
        style = body2,
        color = gray
    )
}

@Composable
fun LoginBottomText() {
    val annotatedString = buildAnnotatedString {
        append("to our ")
        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
            append("Privacy Policy")
        }
    }

    Text(
        text = annotatedString, style = body2,
        color = gray, modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
fun LoginBottomButton(onNavigateToHome: () -> Unit) {
    Button(
        onClick = { onNavigateToHome.invoke() },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = pink900, contentColor = white),
        shape = medium
    ) {
        Text(text = "Log in", style = button)
    }
}
