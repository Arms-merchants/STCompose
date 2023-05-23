package com.example.stcompose.chat.data.bean

data class HomeListBean(
    val curPage: Int,
    val datas: ArrayList<HomeListItemBean>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)