package com.example.stcompose.brick

import androidx.compose.ui.geometry.Offset

/**
 *    author : heyueyang
 *    time   : 2023/04/17
 *    desc   :方块对象，包含当前方块所在的位置，以及三个构建方法
 *    1.将一组偏移量转为方块的集合
 *    2.将一个形状的对象转为方块的集合
 *    3.将一个范围转为方块的集合
 *
 *    version: 1.0
 */
data class Brick(val location: Offset = Offset.Zero) {

    companion object {
        //将Offset的list专为方块的list
        fun of(offsetList: List<Offset>) = offsetList.map { Brick(it) }

        fun of(spirit: Spirit) = of(spirit.location)

        /**
         * 将一个范围专为方块的集合
         */
        fun of(xRange: IntRange, yRange: IntRange) =
            of(
                mutableListOf<Offset>().apply {
                    xRange.forEach { x ->
                        yRange.forEach { y ->
                            this += Offset(x.toFloat(), y.toFloat())
                        }
                    }
                }
            )
    }

    /**
     * 根据指定的数据做偏移
     *
     * @param step
     */
    fun offsetBy(step: Pair<Int, Int>) =
        copy(location = Offset(location.x + step.first, location.y + step.second))

}