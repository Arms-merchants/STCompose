package com.example.stcompose.chat.data.bean

/**
 *    author : heyueyang
 *    time   : 2023/05/23
 *    desc   :
 *    version: 1.0
 */
data class BannerResponse(
    var desc: String = "",
    var id: Int = 0,
    var imagePath: String = "",
    var isVisible: Int = 0,
    var order: Int = 0,
    var title: String = "",
    var type: Int = 0,
    var url: String = ""
)