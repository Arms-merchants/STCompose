package com.example.stcompose.brick

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stcompose.R
import com.example.stcompose.ui.theme.BodyColor
import com.example.stcompose.ui.theme.ScreenBackground

/**
 *    author : heyueyang
 *    time   : 2023/04/14
 *    desc   :
 *    version: 1.0
 */
@Composable
fun GameBody(
    clickable: Clickable,
    screen: @Composable () -> Unit
) {
    //这里应该就是重组导致clickable丢失导致的问题，用
    val clickableValue by remember {
        mutableStateOf(clickable)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .background(
                BodyColor,
                RoundedCornerShape(10.dp)
            )
            .padding(20.dp)
    ) {
        //上半部分的游戏屏幕
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Box(
                modifier = Modifier
                    .size(330.dp, 400.dp)
                    .padding(top = 20.dp)
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(5.dp)
                    .background(BodyColor)
            )

            Box(
                modifier = Modifier
                    .size(120.dp, 45.dp)
                    .align(Alignment.TopCenter)
                    .background(BodyColor)
            ) {
                Text(
                    text = stringResource(id = R.string.body_label),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(260.dp, 300.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
                    drawScreenBorder(
                        Offset(0f, 0f),
                        Offset(size.width, 0f),
                        Offset(0f, size.height),
                        Offset(size.width, size.height)
                    )
                })
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                        .background(ScreenBackground)
                ) {
                    screen()
                }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        //设置按钮
        val SettingText = @Composable { text: String, modifier: Modifier ->
            Text(
                text = text,
                modifier = modifier,
                color = Color.Black.copy(alpha = 0.8f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
        Row {
            SettingText(stringResource(id = R.string.button_sounds), Modifier.weight(1f))
            SettingText(stringResource(id = R.string.button_pause), Modifier.weight(1f))
            SettingText(stringResource(id = R.string.button_reset), Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row {
            GameButton(
                size = SettingButtonSize,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 20.dp, end = 20.dp),
                onClick = { clickableValue.onMute() }
            )
            GameButton(
                size = SettingButtonSize, modifier = Modifier
                    .weight(1f)
                    .padding(start = 20.dp, end = 20.dp),
                onClick = { clickableValue.onPause() }
            )
            GameButton(
                size = SettingButtonSize,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 20.dp, end = 20.dp),
                onClick = { clickableValue.onRestart() }
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        //游戏操作遥感
        val ButtonText = @Composable { text: String, modifier: Modifier ->
            Text(
                text = text, modifier = modifier, color = Color.White.copy(alpha = 0.8f),
                fontSize = 18.sp
            )
        }
        Row(
            modifier = Modifier
                .height(160.dp)
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                GameButton(
                    size = DirectionButtonSize,
                    modifier = Modifier.align(Alignment.TopCenter),
                    autoInvokeWhenPressed = false,
                    onClick = {
                        clickableValue.onMove(Direction.Up)
                    }
                ) {
                    ButtonText(stringResource(id = R.string.button_up), it)
                }
                GameButton(
                    size = DirectionButtonSize,
                    modifier = Modifier.align(Alignment.CenterStart),
                    autoInvokeWhenPressed = true,
                    onClick = {
                        Log.e("TAG","GameButton <")
                        clickableValue.onMove(Direction.Left)
                    }
                ) {
                    ButtonText(stringResource(id = R.string.button_left), it)
                }
                GameButton(
                    size = DirectionButtonSize,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    autoInvokeWhenPressed = true,
                    onClick = {
                        Log.e("TAG","GameButton >")
                        clickableValue.onMove(Direction.Right) }
                ) {
                    ButtonText(stringResource(id = R.string.button_right), it)
                }
                GameButton(
                    size = DirectionButtonSize,
                    modifier = Modifier.align(Alignment.BottomCenter),
                    autoInvokeWhenPressed = true,
                    onClick = { clickableValue.onMove(Direction.Down) }
                ) {
                    ButtonText(stringResource(id = R.string.button_down), it)
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                GameButton(
                    size = RotateButtonSize,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    autoInvokeWhenPressed = false,
                    onClick = {
                        clickableValue.onRotate()
                    }
                ) {
                    ButtonText(stringResource(id = R.string.button_rotate), it)
                }
            }
        }
    }
}

data class Clickable(
    val onMove: (Direction) -> Unit,
    val onRotate: () -> Unit,
    val onRestart: () -> Unit,
    val onPause: () -> Unit,
    val onMute: () -> Unit
)
