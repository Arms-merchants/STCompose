package com.example.schedule.interceptor

/**
 *    author : heyueyang
 *    time   : 2023/05/11
 *    desc   :
 *    version: 1.0
 */
interface Interceptor {

    fun intercept(chain:InterceptChain)

}