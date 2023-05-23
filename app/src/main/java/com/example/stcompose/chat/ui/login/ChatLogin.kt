package com.example.stcompose.chat

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stcompose.R
import com.example.stcompose.ui.theme.nunitoSansFamily

/**
 *    author : heyueyang
 *    time   : 2023/04/27
 *    desc   :
 *    version: 1.0
 */
@Composable
fun ChatLoginScreen(modifier: Modifier = Modifier, loginClick: () -> Unit) {
    var userName by remember {
        mutableStateOf("")
    }
    var passWord by remember {
        mutableStateOf("")
    }

    Column(
        modifier = modifier
            .padding(start = 20.dp, end = 20.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Chatty", fontFamily = nunitoSansFamily, fontSize = 50.sp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .shadow(3.dp)
        ) {
            BasicTextField(value = userName, onValueChange = {
                userName = it
            }, decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (userName.isEmpty()) {
                            Text(text = "用户名", color = Color.Gray)
                        }
                        innerTextField()
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.expand),
                        contentDescription = ""
                    )
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
            )
        }
        Spacer(modifier = Modifier.height(height = 20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    Color.Black
                )
        ) {
            TextField(value = passWord, onValueChange = { value ->
                passWord = value
            }, placeholder = {
                Text(text = "密码")
            }, trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.visibility),
                    contentDescription = null
                )
            }, modifier = Modifier.fillMaxWidth())
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp)),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
            onClick = {
                loginClick.invoke()
            }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.login),
                    tint = Color.White,
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "登入", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth()) {
            Text(text = "忘记密码？")
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "注册账号")
        }
    }

}


