package com.example.stcompose

/**
 *    author : heyueyang
 *    time   : 2023/03/08
 *    desc   :
 *    version: 1.0
 */
class AScop {
    fun visitA() {}
}

class BScop {
    fun visitB() {}
}

fun funA(scope: AScop.() -> Unit) {
    scope(AScop())
}

fun funB(scope: BScop.() -> Unit) {
    scope(BScop())
}

fun main() {
    funA {
        //在这里只能调用A的visitA方法，访问不到B的方法
        funB {
            //但B在A的内部，这里可以访问visitA
            visitA()
        }
    }
}

