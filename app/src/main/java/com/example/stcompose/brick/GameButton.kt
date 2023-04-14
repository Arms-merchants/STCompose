package com.example.stcompose.brick

import android.view.MotionEvent.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.stcompose.ui.theme.Purple200
import com.example.stcompose.ui.theme.Purple500
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 *    author : heyueyang
 *    time   : 2023/04/13
 *    desc   :
 *    version: 1.0
 */
@OptIn(ExperimentalComposeUiApi::class, ObsoleteCoroutinesApi::class)
@Composable
fun GameButton(
    modifier: Modifier = Modifier,
    size: Dp,
    onClick: () -> Unit = {},
    autoInvokeWhenPressed: Boolean = true,
    content: @Composable (Modifier) -> Unit = {}
) {
    val backgroundShape = RoundedCornerShape(size / 2)
    lateinit var ticker: ReceiveChannel<Unit>

    //现有的手势事件都是响应一次，无法持续响应，例如长按只能响应一次事件，而如果我们要让长按方向键来让方块持续的变动位置，就要单独的处理手势事件
    val pressedInteraction = remember { mutableStateOf<PressInteraction.Press?>(null) }
    //但是如果拦截了手势事件那么按下的效果又会缺失，通过indication来手动实现
    val interactionSource = MutableInteractionSource()
    // interactionSource的方法是挂起函数
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .shadow(5.dp, shape = backgroundShape)
            .size(size = size)
            .clip(backgroundShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Purple200,
                        Purple500
                    ),
                    startY = 0f,
                    endY = 80f
                )
            )
            //在有交互时设置的效果。。但它这个设置的没啥效果啊
            .indication(
                interactionSource = interactionSource,//观察交互状态
                indication = rememberRipple()//设置Ripple风格的显示效果
            )
            .run {
                if (autoInvokeWhenPressed) {
                    pointerInteropFilter {
                        when (it.action) {
                            ACTION_DOWN -> {
                                coroutineScope.launch {
                                    //响应新的事件前先把老的停掉---这个去掉也没影响，那这个是做什么？
                                    /*pressedInteraction.value?.let { oldValue ->
                                        //Remove any old interactions if we didn't fire stop / cancel properly
                                         val interaction = PressInteraction.Cancel(oldValue)
                                         interactionSource.emit(interaction)//通知交互状态的改变，改变显示状态
                                         pressedInteraction.value = null
                                    }*/
                                    val interaction = PressInteraction.Press(Offset(50f, 50f))
                                    interactionSource.emit(interaction)
                                    pressedInteraction.value = interaction
                                }
                                //因为只在一个手势事件中来处理，所以每次要在down中创建新的管道
                                //延迟300毫秒后，以60毫秒的间隔发送一个Unit
                                ticker = ticker(initialDelayMillis = 300, delayMillis = 60)
                                coroutineScope.launch {
                                    ticker.receiveAsFlow()
                                        .collect { onClick() }
                                }
                            }
                            ACTION_CANCEL, ACTION_UP -> {
                                coroutineScope.launch {
                                    pressedInteraction.value?.let {
                                        val interaction = PressInteraction.Cancel(it)
                                        interactionSource.emit(interaction)
                                        pressedInteraction.value = null
                                    }
                                    //取消定时事件的发送
                                    ticker.cancel()
                                    if (it.action == ACTION_UP) {
                                        //抬起的时候补一个事件
                                        onClick()
                                    }
                                }
                            }
                            else -> {
                                if (it.action != ACTION_MOVE) {
                                    ticker.cancel()
                                }
                                return@pointerInteropFilter false
                            }
                        }
                        true
                    }
                } else {
                    clickable { onClick() }
                }
            }
    ) {
        content(Modifier.align(Alignment.Center))
    }
}