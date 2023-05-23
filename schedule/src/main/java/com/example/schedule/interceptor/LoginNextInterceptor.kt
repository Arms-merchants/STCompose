package com.example.schedule.interceptor

/**
 *    author : heyueyang
 *    time   : 2023/05/11
 *    desc   :ji
 *    version: 1.0
 */
class LoginNextInterceptor(private val action: () -> Unit) : BaseLoginInterceptImpl() {
    override fun intercept(chain: InterceptChain) {
        super.intercept(chain)
        if (LoginManager.isLogin()) {
            action()
        }
        mChain?.process()
    }
}