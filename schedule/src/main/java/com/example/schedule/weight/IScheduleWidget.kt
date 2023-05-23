package com.example.schedule.weight

import android.graphics.Point
import android.view.MotionEvent

/**
 *    author : heyueyang
 *    time   : 2023/05/04
 *    desc   :
 *    version: 1.0
 */
interface IScheduleWidget {
    val render: IScheduleRender
    fun onTouchEvent(motionEvent: MotionEvent): Boolean
    fun onScroll(x: Int, y: Int)
    fun scrollTo(x: Int, y: Int, duration: Int = 250)
    fun isScrolling(): Boolean
}

interface IScheduleRender {
    var widget: IScheduleWidget
    val scrollPosition: Point
    fun render(x: Int, y: Int)
}
