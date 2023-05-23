package com.example.schedule.interceptor

/**
 *    author : heyueyang
 *    time   : 2023/05/11
 *    desc   :
 *    version: 1.0
 */
class LoginInterceptor : BaseLoginInterceptImpl() {

    override fun intercept(chain: InterceptChain) {
        super.intercept(chain)
        if (LoginManager.isLogin()) {
            chain.process()
        } else {
            //登录
        }
    }

    fun loginFinished() {
        mChain?.process()
    }

}