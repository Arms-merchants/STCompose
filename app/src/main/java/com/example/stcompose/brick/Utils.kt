package com.example.stcompose.brick

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.stcompose.R

/**
 *    author : heyueyang
 *    time   : 2023/04/14
 *    desc   :
 *    version: 1.0
 */

fun Offset(x: Int, y: Int) = androidx.compose.ui.geometry.Offset(x.toFloat(), y.toFloat())

/**
 * 摇杆触发的方向
 *
 */
enum class Direction {
    Left, Up, Right, Down
}

/**
 * 将方向转为偏移量描述
 */
fun Direction.toOffset() = when (this) {
    //向左x为-1
    Direction.Left -> -1 to 0
    //向上y为-1
    Direction.Up -> 0 to -1
    //向右x为➕1
    Direction.Right -> 1 to 0
    //向下y为➕1
    Direction.Down -> 0 to 1
}

/**
 * 特定字体的描述集合
 */
val LedFontFamily = FontFamily(
    Font(R.font.unidream_led, FontWeight.Light),
    Font(R.font.unidream_led, FontWeight.Normal),
    Font(R.font.unidream_led, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.unidream_led, FontWeight.Medium),
    Font(R.font.unidream_led, FontWeight.Bold)
)

/**
 * 下一个形状的区域大小
 */
val NextMatrix = 4 to 2
const val ScoreEverySpirit = 12

fun calculateScore(lines: Int) = when (lines) {
    1 -> 100
    2 -> 300
    3 -> 700
    4 -> 1500
    else -> 0
}