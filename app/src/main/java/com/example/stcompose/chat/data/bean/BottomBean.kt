package com.example.stcompose.chat.data.bean

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Source
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.ui.graphics.vector.ImageVector

/**
 *    author : heyueyang
 *    time   : 2023/05/22
 *    desc   :
 *    version: 1.0
 */
sealed class BottomBean(val route: String, val name: String, val icons: ImageVector) {
    object Home : BottomBean("home", "首页", Icons.Filled.Home)
    object Project : BottomBean("project", "项目", Icons.Filled.Source)
    object PubAccount : BottomBean("pubaccount", "公众号", Icons.Filled.SupervisorAccount)
    object Mine : BottomBean("mine", "我的", Icons.Filled.Person)
}