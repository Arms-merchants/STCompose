package com.example.schedule.interceptor

/**
 *    author : heyueyang
 *    time   : 2023/05/11
 *    desc   :
 *    version: 1.0
 */
abstract class BaseLoginInterceptImpl : Interceptor {
    protected var mChain: InterceptChain? = null

    override fun intercept(chain: InterceptChain) {
        mChain = chain
    }

}