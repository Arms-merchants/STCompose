package com.example.stcompose.brick


import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp

/**
 *    author : heyueyang
 *    time   : 2023/04/13
 *    desc   : 俄罗斯方块基础绘制的方法
 *    version: 1.0
 */

val DirectionButtonSize = 60.dp
val RotateButtonSize = 90.dp
val SettingButtonSize = 15.dp

/**
 * @param brickSize 俄罗斯方块的大小
 * @param color 俄罗斯方块的颜色
 * @param offset 俄罗斯方块在屏幕上的偏移位置
 */
fun DrawScope.drawBrick(brickSize: Float, offset: Offset, color: Color) {
    val actualLocation = Offset(offset.x * brickSize, offset.y * brickSize)
    val outSize = brickSize * 0.8f
    val outerOffset = (brickSize - outSize) / 2
    drawRect(
        color = color,
        topLeft = actualLocation + Offset(outerOffset, outerOffset),
        size = Size(outSize, outSize),
        style = Stroke(outSize / 10)
    )
    val innerSize = brickSize * 0.5f
    val innerOffset = (brickSize - innerSize) / 2
    drawRect(
        color = color,
        topLeft = actualLocation + Offset(innerOffset, innerOffset),
        size = Size(innerSize, innerSize)
    )
}

/**
 * 绘制方块的范围矩阵
 */
fun DrawScope.drawMatrix(brickSize: Float, matrix: Pair<Int, Int>) {
    (0 until matrix.first).forEach { x ->
        (0 until matrix.second).forEach { y ->
            drawBrick(brickSize, Offset(x.toFloat(), y.toFloat()), Color.Gray)
        }
    }
}


fun DrawScope.drawSprite(sprite: Spirit, brickSize: Float, matrix: Pair<Int, Int>) {
    //clipRect会限定一个绘制区域，如果超出这个区域将不会保留
    clipRect(0f, 0f, matrix.first * brickSize, matrix.second * brickSize) {
        sprite.location.forEach {
            drawBrick(brickSize = brickSize, Offset(it.x, it.y), Color.Black)
        }
    }
}







