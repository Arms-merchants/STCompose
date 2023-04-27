package com.example.stcompose.brick

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 *    author : heyueyang
 *    time   : 2023/04/13
 *    desc   :
 *    version: 1.0
 */
@Composable
fun BrickPage() {
    val viewModel = viewModel<GameViewModel>()
    val state = viewModel.viewState.value

    LaunchedEffect(key1 = Unit, block = {
        while (isActive) {
            //就是只要当前控件还活着这玩意一直执行，并且控制着自然下落的速度
            delay(650L - 45 * (state.level - 1))
            viewModel.dispatch(Action.GameTick)
        }
    })

    var isShowTips by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = viewModel.viewState.value.level) {
        isShowTips = true
        delay(1000)
        isShowTips = false
    }
    //处理切到后台的情况，自动暂停
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = Unit) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                viewModel.dispatch(Action.Resume)
            }

            override fun onPause(owner: LifecycleOwner) {
                viewModel.dispatch(Action.Pause)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    val clickable = Clickable(
        onMute = {
            viewModel.dispatch(Action.Mute)
        },
        onMove = { direction ->
            if (direction == Direction.Up) {
                viewModel.dispatch(Action.Drop)
            } else {
                viewModel.dispatch(Action.Move(direction = direction))
            }
        },
        onPause = {
            if (viewModel.viewState.value.isPaused) {
                viewModel.dispatch(Action.Resume)
            } else {
                viewModel.dispatch(Action.Pause)
            }
        },
        onRestart = {
            viewModel.dispatch(Action.Reset)
        },
        onRotate = {
            viewModel.dispatch(Action.Rotate)
        }
    )
    GameBody(clickable = clickable) {
        GameScreen()
    }
    if (isShowTips) {
        SupPopScreen()
    }
}


