package com.example.stcompose

import android.app.Application
import com.example.stcompose.network.OkHttpFactory
import dagger.hilt.android.HiltAndroidApp
import rxhttp.RxHttpPlugins

/**
 *    author : heyueyang
 *    time   : 2023/05/17
 *    desc   :
 *    version: 1.0
 */
@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        RxHttpPlugins.init(OkHttpFactory.instance.okHttpClient)
            .setDebug(true, false)
    }
}