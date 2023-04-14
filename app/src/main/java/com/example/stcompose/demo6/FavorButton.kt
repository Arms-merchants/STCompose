package com.example.stcompose.demo6

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.stcompose.ui.theme.Purple500

/**
 *    author : heyueyang
 *    time   : 2023/03/29
 *    desc   : 按钮上两种状态的变化过程
 *    version: 1.0
 */

/**
 * 两个状态：
 * 1未点之前，矩形边框+图标+文字
 * 2点击之后，圆形icon
 */
data class ButtonConfig(
    val backgroundColor: Color,
    val textColor: Color,
    val roundedCorner: Int,
    val buttonSize: Dp
)

enum class ButtonState(val ui: ButtonConfig) {
    Idle(ButtonConfig(Purple500, Color.White, 50, 60.dp)),
    Pressed(ButtonConfig(Color.White, Purple500, 6, 300.dp))
}

const val animateDuration = 3000


@Composable
fun FavButtonList() {
    Column {
        Spacer(modifier = Modifier.height(100.dp))
        AnimatedFavButton()
        Spacer(modifier = Modifier.height(10.dp))
        AnimatedFavButton2()
        Spacer(modifier = Modifier.height(10.dp))
        AnimatedFavButton3()
    }
}

/**
 * 使用高级动画AnimatedContent实现，控制的为整体，不能单个属性控制，颗粒度较大
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedFavButton(modifier: Modifier = Modifier) {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    Box(modifier = modifier) {
        //通过AnimatedContent定义的是一个整体的动画，颗粒度较大，不能配置单独的属性来
        AnimatedContent(targetState = buttonState, transitionSpec = {
            fadeIn(animationSpec = tween(durationMillis = animateDuration)) with fadeOut(
                animationSpec = tween(durationMillis = animateDuration)
            ) using SizeTransform { initialSize, targetSize ->
                tween(durationMillis = animateDuration)
            }
        }) { state ->
            FavButton(buttonState = state) {
                buttonState =
                    if (buttonState == ButtonState.Idle) ButtonState.Pressed else ButtonState.Idle
            }
        }
    }
}

/**
 * 使用animate*AsState来实现对各个属性的动画设置，并且相较于其他两种的实现，在动画未执行完成时再次点击会有完整的动画，而其他的两种没有
 */
@Composable
fun AnimatedFavButton2(modifier: Modifier = Modifier) {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val buttonSize = animateDpAsState(
        targetValue = buttonState.ui.buttonSize, animationSpec = tween(
            3000
        )
    )
    val roundedCorner = animateIntAsState(
        targetValue = buttonState.ui.roundedCorner, animationSpec = tween(
            1500
        )
    )
    val backgroundColor = animateColorAsState(
        targetValue = buttonState.ui.backgroundColor,
        animationSpec = tween(2000)
    )
    val textColor = animateColorAsState(
        targetValue = buttonState.ui.textColor, animationSpec = tween(
            1000
        )
    )
    FavButton(
        buttonState = buttonState,
        buttonSize = buttonSize.value,
        roundedCorner = roundedCorner.value,
        backgroundColor = backgroundColor.value,
        textColor = textColor.value
    ) {
        buttonState =
            if (buttonState == ButtonState.Idle) ButtonState.Pressed else ButtonState.Idle
        Log.e("TAG", "Current state:${buttonState.toString()}")
    }
}

@Composable
fun AnimatedFavButton3(modifier: Modifier = Modifier) {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val transition = updateTransition(targetState = buttonState, label = "button")
    val buttonSize = transition.animateDp(
        label = "button size",
        transitionSpec = { tween(durationMillis = 3000) }) {
        it.ui.buttonSize
    }
    val roundedCorner = transition.animateInt(label = "roundedCorner",
        transitionSpec = { tween(durationMillis = 1500) }) {
        it.ui.roundedCorner
    }
    val backgroundColor = transition.animateColor(label = "backgroundColor",
        transitionSpec = { tween(durationMillis = 2000) }) {
        it.ui.backgroundColor
    }
    val textColor = transition.animateColor(label = "textColor",
        transitionSpec = { tween(durationMillis = 1000) }) {
        it.ui.textColor
    }
    FavButton(
        buttonState = buttonState,
        buttonSize = buttonSize.value,
        roundedCorner = roundedCorner.value,
        backgroundColor = backgroundColor.value,
        textColor = textColor.value
    ) {
        buttonState = if (buttonState == ButtonState.Idle) ButtonState.Pressed else ButtonState.Idle
        Log.e("TAG", "Current state:${buttonState.toString()}")
    }
}


@Composable
fun FavButton(
    modifier: Modifier = Modifier,
    buttonState: ButtonState,
    buttonSize: Dp = buttonState.ui.buttonSize,
    roundedCorner: Int = buttonState.ui.roundedCorner,
    backgroundColor: Color = buttonState.ui.backgroundColor,
    textColor: Color = buttonState.ui.textColor,
    click: () -> Unit
) {
    Button(
        onClick = { click.invoke() },
        border = BorderStroke(1.dp, Purple500),
        modifier = Modifier.size(width = buttonSize, height = 60.dp),
        shape = RoundedCornerShape(roundedCorner.coerceIn(0..100)),
        colors = ButtonDefaults.buttonColors(backgroundColor)
    ) {
        if (buttonState == ButtonState.Idle) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "",
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Row {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterVertically),
                    tint = textColor
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "ADD TO FAVORITES!",
                    softWrap = false,
                    color = textColor,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}

