package com.example.stcompose.brick

import androidx.compose.ui.geometry.Offset
import kotlin.math.absoluteValue

/**
 *    author : heyueyang
 *    time   : 2023/04/14
 *    desc   :
 *    version: 1.0
 */
data class Spirit(val shape: List<Offset> = emptyList(), val offset: Offset = Offset(0f, 0f)) {
    //构成图形的方块都需要去加这个偏移量
    val location: List<Offset> = shape.map { it + offset }

    /**
     * 根据偏移量进行移动
     */
    fun moveBy(step: Pair<Int, Int>): Spirit =
        copy(offset = offset + Offset(step.first.toFloat(), step.second.toFloat()))

    /**
     * 旋转
     */
    fun rotate(): Spirit {
        val newShape = shape.toMutableList()
        shape.forEachIndexed { index, offset ->
            newShape[index] = Offset(shape[index].y, -shape[index].x)
        }
        return copy(shape = newShape)
    }

    /**
     * 调整，让方块只能在限定的范围内
     * @param matrix 整个俄罗斯方块的矩阵范围
     * @param adjustY 是否需要调整y，如果需要调整y的才调整，不然y的offset为0，例如方块下落这里不会需要调整Y
     */
    fun adjustOffset(matrix: Pair<Int, Int>, adjustY: Boolean = true): Spirit {
        val yOffset: Int = if (adjustY) {
            //顶部超出了向下移
            val top =
                ((location.minByOrNull { it.y }?.y?.takeIf { it < 0 }?.absoluteValue) ?: 0).toInt()
            //底部超出了向上移
            val bottom = ((location.maxByOrNull { it.y }?.y?.takeIf { it > matrix.second - 1 }
                ?.let { matrix.second - it - 1 }) ?: 0).toInt()
            //只会又一个有值
            top + bottom
        } else {
            0
        }
        val xOffset: Int
        val left = (location.minByOrNull { it.x }?.x?.takeIf { it < 0 }?.absoluteValue ?: 0).toInt()
        val right = (location.maxByOrNull { it.x }?.x?.takeIf { it > matrix.first - 1 }?.let {
            matrix.first - it - 1
        } ?: 0).toInt()
        xOffset = left + right
        return moveBy(xOffset to yOffset)
    }

    companion object {
        val Empty = Spirit()
    }
}


val SpriteType = listOf(
    listOf(Offset(1f, -1f), Offset(1f, 0f), Offset(0f, 0f), Offset(0f, 1f)),//Z
    listOf(Offset(0f, -1f), Offset(0f, 0f), Offset(1f, 0f), Offset(1f, 1f)),//S
    listOf(Offset(0f, -1f), Offset(0f, 0f), Offset(0f, 1f), Offset(0f, 2f)),//I
    listOf(Offset(0f, -1f), Offset(0f, 0f), Offset(1f, 0f), Offset(0f, 1f)),
    listOf(Offset(0f, 0f), Offset(0f, -1f), Offset(1f, 0f), Offset(1f, -1f)),
    listOf(Offset(0f, -1f), Offset(1f, -1f), Offset(1f, 0f), Offset(1f, 1f)),
    listOf(Offset(1f, -1f), Offset(0f, -1f), Offset(0f, 0f), Offset(0f, 1f))
)
