package com.example.schedule.interceptor

/**
 *    author : heyueyang
 *    time   : 2023/05/11
 *    desc   :
 *    version: 1.0
 */
object InterceptChain {
    private var index: Int = 0
    private val interceptors by lazy(LazyThreadSafetyMode.NONE) {
        ArrayList<Interceptor>(2)
    }

    private val loginIntercept = LoginInterceptor()

    fun process() {
        if (interceptors.isEmpty()) return
        when (index) {
            in interceptors.indices -> {
                val interceptor = interceptors[index]
                index++
                interceptor.intercept(this)
            }

            interceptors.size -> {
                clearAllInterceptors()
            }
        }
    }

    fun addInterceptor(interceptor: Interceptor): InterceptChain {
        if (!interceptors.contains(loginIntercept)) {
            interceptors.add(loginIntercept)
        }
        if (!interceptors.contains(interceptor)) {
            interceptors.add(interceptor)
        }
        return this
    }


    fun loginFinished() {
        if (interceptors.contains(loginIntercept) && interceptors.size > 1) {
            loginIntercept.loginFinished()
        }
    }

    private fun clearAllInterceptors() {
        index = 0
        interceptors.clear()
    }


}