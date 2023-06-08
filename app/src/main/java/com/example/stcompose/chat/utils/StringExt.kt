package com.example.stcompose.chat.utils

/**
 *    author : heyueyang
 *    time   : 2023/05/24
 *    desc   :
 *    version: 1.0
 */
fun String.filterHtml() : String{
    return this.replace("<em class='highlight'>","")
        .replace("</em>","")
        .replace("&ldquo;","“")
        .replace("&rdquo;","”")
        .replace("&mdash;","—")
        .replace("&quot;","\"")
        .replace("&amp;","&")
}