package com.example.stcompose.brick

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stcompose.R
import com.example.stcompose.ui.theme.BrickMatrix
import com.example.stcompose.ui.theme.BrickSpirit
import com.example.stcompose.ui.theme.ScreenBackground
import java.lang.Float.min
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.roundToInt

/**
 *    author : heyueyang
 *    time   : 2023/04/17
 *    desc   : 游戏的主屏幕，包括俄罗斯方块的展示模块以及分数等级等
 *    version: 1.0
 */
@Composable
fun GameScreen(modifier: Modifier = Modifier) {
    val viewModel = viewModel<GameViewModel>()
    val state = viewModel.viewState.value
    //中心文本的动画
    val animation = rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier
            .background(Color.Black)
            .padding(1.dp)
            .background(ScreenBackground)
            .padding(10.dp)
    ) {
        //游戏的主体的屏幕，显示宽度啊由方块的尺寸决定，
        Canvas(modifier = Modifier.fillMaxSize()) {
            //获取方块的尺寸，在宽高比例中获取小的值
            val breakSizeW = size.width / state.matrix.first.toFloat()
            val breakSizeH = size.height / state.matrix.second.toFloat()
            val breakSize = min(breakSizeW, breakSizeH)
            //绘制方块区域的背景
            drawMatrix(breakSize, state.matrix)
            //绘制游戏区域的边框
            drawMatrixBorder(breakSize, state.matrix)
            //绘制已经下落的方块
            drawBricks(state.bricks, brickSize = breakSize, state.matrix)
            //绘制下落中的方块
            drawSprite(state.spirit, brickSize = breakSize, state.matrix)
            //绘制文本（游戏欢迎页，游戏结束啊）
            drawText(
                state.gameStatus,
                brickSize = breakSize,
                matrix = state.matrix,
                animation.value
            )
        }
        //右侧的分数级别等
        GameScoreboard(
            modifier,
            spirit = run {
                if (state.spirit == Spirit.Empty) Spirit.Empty else
                    state.spiritNext
            },
            score = state.score,
            line = state.line,
            level = state.level,
            isMute = state.isMute,
            isPaused = state.isPaused
        )

    }
}

/**
 * 绘制游戏区域的边框
 *
 * @param brickSize
 * @param matrix
 */
fun DrawScope.drawMatrixBorder(brickSize: Float, matrix: Pair<Int, Int>) {
    val gap = matrix.first * brickSize * 0.05f

    drawRect(
        Color.Black,
        size = Size(
            matrix.first * brickSize + gap, matrix.second * brickSize + gap
        ),
        topLeft = Offset(-gap / 2, -gap / 2),
        style = Stroke(width = 1.dp.toPx())
    )
}

/**
 * 右侧分数等面板
 *
 * @param modifier
 * @param brickSize
 * @param spirit
 * @param score
 * @param line
 * @param level
 * @param isMute
 * @param isPaused
 */
@Composable
fun GameScoreboard(
    modifier: Modifier = Modifier,
    brickSize: Float = 35f,
    spirit: Spirit,
    score: Int = 0,
    line: Int = 0,
    level: Int = 1,
    isMute: Boolean = false,
    isPaused: Boolean = false
) {
    val textSize = 12.sp
    val margin = 12.dp
    Row(modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.weight(0.65f))
        Column(modifier = Modifier.weight(0.35f)) {
            Text(text = "Score", fontSize = textSize)
            LedNumber(Modifier.fillMaxWidth(), num = score, digits = 8)
            Spacer(modifier = Modifier.height(margin))
            Text(text = "Lines", fontSize = textSize)
            LedNumber(Modifier.fillMaxWidth(), num = line, digits = 8)
            Spacer(modifier = Modifier.height(margin))
            Text(text = "Level", fontSize = textSize)
            LedNumber(modifier = Modifier.fillMaxWidth(), num = level, digits = 2)
            Spacer(modifier = Modifier.height(margin))
            Text(text = "Next", fontSize = textSize)
            NextBrick(brickSize = brickSize, spirit = spirit)
            Spacer(modifier = Modifier.weight(1f))
            Row {
                Image(
                    modifier = Modifier.width(15.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_music_off_24),
                    colorFilter = ColorFilter.tint(if (isMute) BrickSpirit else BrickMatrix),
                    contentDescription = ""
                )
                Image(
                    modifier = Modifier.width(15.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_pause_24),
                    colorFilter = ColorFilter.tint(if (isPaused) BrickSpirit else BrickMatrix),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.weight(1f))
                //当前时间
                LedClock()
            }
        }
    }
}

@Composable
fun LedClock() {
    //通过动画来实现时钟的展示，不过因为是通过外部变量引起刷新，这是属于副作用范围
    //0-》1-》0 间隔1秒执行
    val animated by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 1000)
        )
    )

    var clock by remember { mutableStateOf(0 to 0) }
    //因为key为animated，它发生改变的时候下面的方法就会执行一次
    DisposableEffect(key1 = animated.roundToInt()) {
        val dateFormat = SimpleDateFormat("H,m")
        val (hour, minter) = dateFormat.format(Date()).split(",")
        clock = hour.toInt() to minter.toInt()
        onDispose {
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        LedNumber(num = clock.first, digits = 2, fillZero = true)
        Box {
            Text(text = ":", fontFamily = LedFontFamily, fontSize = 15.sp, color = BrickMatrix)
            if (animated.roundToInt() == 1) {
                Text(text = ":", fontFamily = LedFontFamily, fontSize = 15.sp, color = BrickSpirit)
            }
        }
        LedNumber(num = clock.second, digits = 2, fillZero = true)
    }
}


@Composable
fun NextBrick(brickSize: Float, spirit: Spirit) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        drawMatrix(brickSize = brickSize, NextMatrix)
        drawSprite(
            //因为所有的形状在构建时都加了偏移量，先去掉，然后左转一下，保证2行能够放下
            //在将形状调整到矩形范围内
            sprite = spirit.copy(offset = Offset.Zero).rotate().adjustOffset(
                NextMatrix, adjustY = true
            ), brickSize, NextMatrix
        )
    }
}


/**
 * 展示分数的列表
 *
 * @param modifier
 * @param num   需要显示的分数
 * @param digits 总的位数
 * @param fillZero 是否需要填充0
 */
@Composable
fun LedNumber(modifier: Modifier = Modifier, num: Int, digits: Int, fillZero: Boolean = false) {
    val textSize = 16.sp
    val textWidth = 8.dp
    Box(modifier = modifier) {
        Row(modifier = Modifier.align(Alignment.CenterEnd)) {
            repeat(digits) {
                Text(
                    "8",
                    color = BrickMatrix,
                    fontSize = textSize,
                    modifier = Modifier.width(textWidth),
                    fontFamily = LedFontFamily,
                    textAlign = TextAlign.End
                )
            }
        }
        Row(modifier = Modifier.align(Alignment.CenterEnd)) {
            val str = if (fillZero) String.format("%0${digits}d", num) else num.toString()
            str.iterator().forEach {
                Text(
                    text = it.toString(),
                    fontSize = textSize,
                    color = BrickSpirit,
                    fontFamily = LedFontFamily,
                    textAlign = TextAlign.End
                )
            }
        }

    }
}


@Preview
@Composable
fun PreviewGameScreen() {
    GameScreen(modifier = Modifier.size(260.dp, 300.dp))
}
