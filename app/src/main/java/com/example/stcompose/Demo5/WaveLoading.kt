package com.example.stcompose.Demo5

import android.graphics.Bitmap
import androidx.annotation.FloatRange
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.core.graphics.transform
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 *    author : heyueyang
 *    time   : 2023/03/23
 *    desc   :
 *    version: 1.0
 */

private const val defaultAmlitude = 0.2f
private const val defaultVelocity = 1.0f
private const val waveDuration = 2000

data class WaveConfig(
    @FloatRange(from = 0.0, to = 1.0)
    val process: Float = 0f,//进度
    @FloatRange(from = 0.0, to = 1.0)
    val amplitude: Float = defaultAmlitude,//振幅
    @FloatRange(from = 0.0, to = 1.0)
    val velocity: Float = defaultVelocity //速度
)

@Composable
fun WaveLoading(modifier: Modifier, waveConfig: WaveConfig, bitmap: Bitmap) {
    //无限循环的动画，知道Composable生命周期结束
    val transition = rememberInfiniteTransition()
    val animates = listOf(
        1f, 0.75f, 0.5f
    ).map {
        //无限循环的动画,也就是在0到1
        transition.animateFloat(
            initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
                //设置3个间隔时间为2秒，1.5秒，1秒的三个动画
                //动画持续时间,默认为FastOutSlowInEasing，静止启动到中间后减速后停止
                animation = tween((it * waveDuration).roundToInt()),
                //动画效果为反复效果
                //Restart init->target init->target init->target
                //Reverse init->target target->init init->target
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Canvas(modifier = modifier
        .fillMaxSize()
        .background(Color.Yellow)) {
        drawWave(bitmap.asImageBitmap(), waveConfig, animates)
    }
}

private fun DrawScope.drawWave(
    imageBitmap: ImageBitmap,
    waveConfig: WaveConfig,
    animates: List<State<Float>>
) {
    //绘制图片，设置饱和度为0那么图片就会是灰度
    drawImage(image = imageBitmap, colorFilter = run {
        val cm = ColorMatrix().apply { setToSaturation(0f) }
        ColorFilter.colorMatrix(cm)
    })
    animates.forEachIndexed { index, anim ->
        val maxWidth = 2 * size.width / waveConfig.velocity.coerceAtLeast(0.1f)
        //在当前waveConfig不变的情况下，anim的value会不断变化，所以offSet会不断变化，所以在绘制好后通过offsetX来移动重新绘制Path
        val offsetX = maxWidth / 2 * (1 - anim.value)
        translate(-offsetX) {
            drawPath(
                path = buildWavePath(
                    width = maxWidth,
                    height = size.height,
                    amplitude = size.height * waveConfig.amplitude,
                    progress = waveConfig.process
                ),
                //通过
                brush = ShaderBrush(ImageShader(imageBitmap).apply {
                    transform { postTranslate(offsetX, 0f) }
                }),
                //除了第一个动画其他的路径绘制的都是50%的透明度
                alpha = if (index == 0) 1f else 0.5f
            )
        }
    }
}


private fun buildWavePath(
    dp: Float = 3f,
    width: Float,
    height: Float,
    amplitude: Float,
    progress: Float
): Path {
    //根据振幅以及当前的进度来确定当前的高度
    val adjustHeight = min(height * 0f.coerceAtLeast(1 - progress), amplitude)
    return Path().apply {
        reset()
        moveTo(0f, height)
        lineTo(0f, height * (1 - progress))
        if (progress > 0f && progress < 1f) {
            if (adjustHeight > 0) {
                var x = dp
                while (x < width) {
                    lineTo(
                        x,
                        height * (1 - progress) - adjustHeight / 2f * sin(4.0 * Math.PI * x / width).toFloat()
                    )
                    x += dp
                }
            }
        }
        lineTo(width, height * (1 - progress))
        lineTo(width, height)
        close()
    }
}

