package com.example.stcompose.network

import android.util.Log
import com.example.stcompose.chat.data.bean.HttpResponse
import okhttp3.Response
import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.exception.ParseException
import rxhttp.wrapper.parse.TypeParser
import rxhttp.wrapper.utils.convertTo
import java.lang.reflect.Type

/**
 *    author : heyueyang
 *    time   : 2023/05/18
 *    desc   :
 *    version: 1.0
 */
@Parser(name = "Response")
open class ResultParser<T> : TypeParser<T> {

    //注意，以下两个构造方法是必须的
    protected constructor() : super()
    constructor(type: Type) : super(type)

    override fun onParse(response: Response): T {
        val data: HttpResponse<T> = response.convertTo(HttpResponse::class, *types)
        val t = data.data //获取data字段
        if (data.code != 0) {
            throw ParseException(data.code.toString(), data.msg, response)
        }
        Log.e("TAG", "ResultParser:" + t.toString())
        return t
    }
}