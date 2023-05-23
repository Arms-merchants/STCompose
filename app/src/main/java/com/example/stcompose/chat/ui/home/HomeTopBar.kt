package com.example.stcompose.chat.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout

/**
 *    author : heyueyang
 *    time   : 2023/05/06
 *    desc   :
 *    version: 1.0
 */
@Composable
fun HomeTopBar() {
    TopAppBar(
        modifier = Modifier
            .background(Color.Blue)
            .statusBarsPadding(),
        //不设置的话这就要展示主颜色了
        backgroundColor = Color.Transparent,
        contentColor = Color.Transparent,
        elevation = 0.dp,
        contentPadding = PaddingValues(0.dp)
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (drawerIcon, appLogo, menuIcon, divider) = createRefs()
            Icon(modifier = Modifier
                .constrainAs(ref = drawerIcon) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .padding(start = 10.dp)
                .size(size = 28.dp)
                .clickable { },
                imageVector = Icons.Filled.Menu,
                contentDescription = null,
                tint = MaterialTheme.colors.surface
            )

            Icon(imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                modifier = Modifier
                    .constrainAs(ref = menuIcon) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
                    .padding(end = 10.dp)
                    .clickable { },
                tint = MaterialTheme.colors.surface
            )
            Text(
                text = "玩Android",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.constrainAs(ref = appLogo) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(drawerIcon.end, margin = 20.dp)
                })
        }
    }
}