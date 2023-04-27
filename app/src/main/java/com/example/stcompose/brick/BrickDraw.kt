package com.example.stcompose.brick


import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.stcompose.ui.theme.BrickSpirit

/**
 *    author : heyueyang
 *    time   : 2023/04/13
 *    desc   : 所以和绘制相关的方法
 *    version: 1.0
 */
//方向按键的尺寸
val DirectionButtonSize = 60.dp
//旋转方向按钮的尺寸
val RotateButtonSize = 90.dp
//设置按钮的此次
val SettingButtonSize = 15.dp

/**
 * @param brickSize 俄罗斯方块的大小
 * @param color 俄罗斯方块的颜色
 * @param offset 俄罗斯方块在屏幕上的偏移位置
 */
fun DrawScope.drawBrick(brickSize: Float, offset: Offset, color: Color) {
    //通过偏移量和方块的尺寸确定方块的外边框位置
    val actualLocation = Offset(offset.x * brickSize, offset.y * brickSize)
    //外边框的尺寸
    val outSize = brickSize * 0.8f
    //因为实际外边框小于实际的方块尺寸，所以需要计算一个偏移量保证居中
    val outerOffset = (brickSize - outSize) / 2
    //外部是一个边框
    drawRect(
        color = color,
        topLeft = actualLocation + Offset(outerOffset, outerOffset),
        size = Size(outSize, outSize),
        style = Stroke(outSize / 10)
    )
    val innerSize = brickSize * 0.5f
    val innerOffset = (brickSize - innerSize) / 2
    //内部是一个方块矩形
    drawRect(
        color = color,
        topLeft = actualLocation + Offset(innerOffset, innerOffset),
        size = Size(innerSize, innerSize)
    )
}

/**
 * 绘制底部已经完成下落的方块
 *
 * @param bricks 完成下落的方块集合
 * @param brickSize 方块尺寸
 * @param matrix 范围矩阵
 */
fun DrawScope.drawBricks(bricks: List<Brick>, brickSize: Float, matrix: Pair<Int, Int>) {
    //裁切显示范围，保证不会超出范围矩阵
    clipRect(
        0f, 0f, matrix.first * brickSize, matrix.second * brickSize
    ) {
        //循环绘制方块
        bricks.forEach {
            drawBrick(brickSize, it.location, BrickSpirit)
        }
    }
}

/**
 * 游戏欢迎页以及游戏结束时的提示文本绘制
 *
 * @param gameStatus 游戏状态
 * @param brickSize 方块尺寸
 * @param matrix 范围矩阵
 * @param alpha 透明度这个值会发送变化从而完成一个闪动的动画效果
 */
fun DrawScope.drawText(
    gameStatus: GameStatus,
    brickSize: Float,
    matrix: Pair<Int, Int>,
    alpha: Float
) {
    //通过矩阵和方块的尺寸确定游戏区域的中心位置
    val center = Offset(
        brickSize * matrix.first / 2,
        brickSize * matrix.second / 2
    )
    val drawText = { text: String, size: Float ->
        drawIntoCanvas {
            it.nativeCanvas.drawText(
                text, center.x, center.y, Paint().apply {
                    color = Color.Black.copy(alpha = alpha).toArgb()
                    textSize = size
                    style = Paint.Style.FILL_AND_STROKE
                    textAlign = Paint.Align.CENTER
                    strokeWidth = size / 12
                }
            )
        }
    }
    if (gameStatus == GameStatus.Onboard) {
        drawText("TETRIS", 80f)
    } else if (gameStatus == GameStatus.GameOver) {
        drawText("GAME OVER", 60f)
    }
}


/**
 * 绘制方块的范围矩阵，绘制游戏区域背景的
 */
fun DrawScope.drawMatrix(brickSize: Float, matrix: Pair<Int, Int>) {
    (0 until matrix.first).forEach { x ->
        (0 until matrix.second).forEach { y ->
            drawBrick(brickSize, Offset(x.toFloat(), y.toFloat()), Color.Gray)
        }
    }
}

/**
 * 绘制形状集合 下落中的方块，还有Next方块
 *
 * @param sprite 当前的图形对象
 * @param brickSize 方块的尺寸
 * @param matrix 范围矩阵
 */
fun DrawScope.drawSprite(sprite: Spirit, brickSize: Float, matrix: Pair<Int, Int>) {
    //clipRect会限定一个绘制区域，如果超出这个区域将不会保留
    clipRect(0f, 0f, matrix.first * brickSize, matrix.second * brickSize) {
        sprite.location.forEach {
            drawBrick(brickSize = brickSize, Offset(it.x, it.y), Color.Black)
        }
    }
}

/**
 * 绘制游戏屏幕的边框效果，它是通过绘制两个阴影效果来叠加实现的
 * 但要注意因为游戏区域不是正方形的，所以在进行中间区域绘制的时候，不能直接选取对角线来实现
 * //要保证从当前角出来的线必须是45度的
 *
 * @param topLef
 * @param topRight
 * @param bottomLeft
 * @param bottomRight
 */
fun DrawScope.drawScreenBorder(
    topLef: Offset,
    topRight: Offset,
    bottomLeft: Offset,
    bottomRight: Offset
) {
    //黑色边框
    var path = Path().apply {
        moveTo(topLef.x, topLef.y)
        lineTo(topRight.x, topRight.y)
        //不直接链接过去是为了处理阴影的角度不是45度的问题
        lineTo(
            topRight.x / 2 + topLef.x / 2,
            topLef.y + topRight.x / 2 + topLef.x / 2
        )
        //向下保证和底部左边连接出来的角度也是45度
        lineTo(
            topRight.x / 2 + topLef.x / 2,
            bottomLeft.y - topRight.x / 2 + topLef.x / 2
        )
        lineTo(bottomLeft.x, bottomLeft.y)
        close()
    }
    drawPath(path, Color.Black.copy(0.5f))

    //白色边框
    path = Path().apply {
        moveTo(bottomRight.x, bottomRight.y)
        lineTo(bottomLeft.x, bottomLeft.y)
        lineTo(
            topRight.x / 2 + topLef.x / 2,
            bottomLeft.y - topRight.x / 2 + topLef.x / 2
        )
        lineTo(
            topRight.x / 2 + topLef.x / 2,
            topLef.y + topRight.x / 2 + topLef.x / 2
        )
        lineTo(topRight.x, topRight.y)
        close()
    }

    drawPath(path, Color.White.copy(0.5f))
}







