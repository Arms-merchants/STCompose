package com.example.schedule.weight

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.RectF

/**
 *    author : heyueyang
 *    time   : 2023/05/04
 *    desc   :
 *    version: 1.0
 */
interface IScheduleComponent<T : IScheduleModel> {
    val model: T
    val drawingRect: RectF
    fun updateDrawingRect(anchorPoint: Point)
    fun onDraw(canvas: Canvas, paint: Paint)
}