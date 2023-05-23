package com.example.stcompose.chat.data.bean

/**
 *    author : heyueyang
 *    time   : 2023/05/22
 *    desc   :
 *    version: 1.0
 */
sealed class ModelPath(val route: String) {
    object Login:ModelPath("login")
    object Main:ModelPath("main")
    object Search:ModelPath("search")
}