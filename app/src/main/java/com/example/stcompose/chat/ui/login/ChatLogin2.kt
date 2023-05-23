package com.example.stcompose.chat.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout

/**
 *    author : heyueyang
 *    time   : 2023/05/17
 *    desc   :
 *    version: 1.0
 */
@Composable
fun ChatLogin2(
    modifier: Modifier = Modifier,
    loginClick: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(start = 40.dp, end = 40.dp)
            .systemBarsPadding()
    ) {
        val (back, welcome, wan, username, password, login, sign_in, sign_upo) = createRefs()
        Image(
            imageVector = Icons.Filled.Backup,
            contentDescription = null,
            modifier = Modifier
                .size(15.dp)
                .constrainAs(back) {
                    top.linkTo(parent.top, margin = 15.dp)
                },
            colorFilter = ColorFilter.tint(Color.White)
        )
        Text(
            text = "Welcome",
            style = MaterialTheme.typography.h4,
            color = Color.White,
            modifier = Modifier.constrainAs(welcome) {
                top.linkTo(back.bottom, margin = 60.dp)
            }
        )
        Text(
            text = "玩Android", color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier.constrainAs(wan) {
                top.linkTo(welcome.bottom, 20.dp)
            }
        )
        var userName by remember {
            mutableStateOf<String>("")
        }
        TextField(
            value = userName, onValueChange = {
                userName = it
            },
            placeholder = {
                Text(text = "请输入用户名称")
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                disabledIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,
                focusedIndicatorColor = Color.White,
                focusedLabelColor = Color.White,
                errorIndicatorColor = Color.White,
                placeholderColor = Color.White,
                textColor = Color.White,
                cursorColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .constrainAs(username) {
                    top.linkTo(wan.bottom, 100.dp)
                }
        )
        var passWord by remember {
            mutableStateOf("")
        }
        TextField(
            value = passWord,
            onValueChange = {
                passWord = it
            },
            placeholder = {
                Text(text = "请输入用户密码")
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                disabledIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,
                focusedIndicatorColor = Color.White,
                focusedLabelColor = Color.White,
                errorIndicatorColor = Color.White,
                placeholderColor = Color.White,
                textColor = Color.White,
                cursorColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .constrainAs(password) {
                    top.linkTo(username.bottom, 20.dp)
                }
        )

        Text(
            text = "登录",
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .constrainAs(login) {
                    top.linkTo(password.bottom, 50.dp)
                }
                .clickable {
                    loginClick.invoke()
                })

        Text(
            text = "去注册",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.constrainAs(sign_in) {
                top.linkTo(login.bottom, 20.dp)
            })
    }
}