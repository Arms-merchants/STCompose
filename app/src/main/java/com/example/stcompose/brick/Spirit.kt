package com.example.stcompose.brick

import androidx.compose.ui.geometry.Offset
import kotlin.math.absoluteValue
import kotlin.random.Random

/**
 *    author : heyueyang
 *    time   : 2023/04/14
 *    desc   : 形状的对象，包含构建形状的所有方块的位置集合，当前形状的偏移位置量，两者相加那么就构成了当前图形的位置
 *    偏移量对应矩阵范围，不是直接显示在屏幕上的位置，需要乘以方块的尺寸才是对应的位置
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
     * 左旋转
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
            //只会一个有值
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

/**
 * 判断所有的方块是否在有效范围内
 *
 * @param blocks 方块集合
 * @param matrix 矩阵范围
 * @param isGameOver 是否是判断游戏结束，结束的时候
 * @return false不在有效范围，true 在有效范围
 */
fun Spirit.isValidInMatrix(
    blocks: List<Brick>,
    matrix: Pair<Int, Int>,
    isGameOver: Boolean = false
): Boolean {
    if (isGameOver) {
        val isTop = blocks.any { it.location.y.toInt() <= 0 }
        if (isTop) {
            return false
        }
    }
    return location.none { location ->
        //构建当前形状的方块没有一个是超出边界的
        location.x < 0 || location.x > matrix.first - 1 || location.y > matrix.second - 1 ||
                //并且当前的形状不能和在底部的方块有重叠的部分
                blocks.any { it.location.x == location.x && it.location.y == location.y }
    }
}

/**
 * 通过SpriteType构建next的列表集合
 *
 * @param matrix 矩阵范围
 * @return
 */
fun generateSpiritReverse(matrix: Pair<Int, Int>): List<Spirit> {
    return SpriteType.map {
        //修改每个的Offset随机范围为0到矩阵列数减1，并且要保证当前方块要在有效的范围内，所以调整只需要在x轴进行
        Spirit(it, Offset(Random.nextInt(matrix.first - 1), -1)).adjustOffset(matrix, false)
    }   //打乱列表的顺序
        .shuffled()
        //截取一个随机的长度
        .subList(0, Random.nextInt(SpriteType.size - 1).coerceAtLeast(1))
}
