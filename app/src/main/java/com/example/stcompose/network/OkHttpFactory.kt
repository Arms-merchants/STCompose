package com.example.stcompose.network

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 *    author : heyueyang
 *    time   : 2023/05/18
 *    desc   :
 *    version: 1.0
 */
class OkHttpFactory private constructor() {

    val okHttpClient: OkHttpClient

    init {
        val builder = OkHttpClient.Builder()
        builder.readTimeout(TIMEOUT_READ.toLong(), TimeUnit.SECONDS)
        builder.writeTimeout(TIMEOUT_WRITE.toLong(), TimeUnit.SECONDS)
        builder.connectTimeout(TIMEOUT_CONNECTION.toLong(), TimeUnit.SECONDS)
        okHttpClient = builder.build()
    }

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            OkHttpFactory()
        }
        private const val TIMEOUT_READ = 30
        private const val TIMEOUT_WRITE = 30
        private const val TIMEOUT_CONNECTION = 30
    }


}