package com.example.compose.springdemo

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 *    author : heyueyang
 *    time   : 2023/03/29
 *    desc   : spring动画，基于物理引擎的动画效果（回弹啊，阻尼系数，弹簧强度等等啊）
 *    version: 1.0
 */
val spacerHeight = 10.dp

@Preview
@Composable
fun SpringDemo() {

    var targetValue by remember { mutableStateOf(0f) }

    val offset = remember {
        listOf(
            Animatable(0f) to spring<Float>(
                //设置阻尼,影响的是动画的范围，阻尼越小，动画的范围越大
                dampingRatio = Spring.DampingRatioHighBouncy,
                //刚度，这个值越大，动画静止的速度越快
                stiffness = Spring.StiffnessHigh
            ),
            Animatable(0f) to spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessVeryLow
            ),
            Animatable(0f) to spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessVeryLow
            ),
            Animatable(0f) to spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessVeryLow
            ),
            //visibilityThreshold是设置一个临界值，到达这个临界值，动画直接结束
            Animatable(0f) to spring(
                dampingRatio = 2f,
                stiffness = Spring.StiffnessVeryLow,
                0.5f
            ),
        )
    }

    LaunchedEffect(targetValue) {
        if (targetValue == 0f) {
            offset.forEach {
                launch { it.first.snapTo(targetValue) }

            }
        } else {
            offset.forEachIndexed { index, pair ->
                launch { pair.first.animateTo(targetValue, pair.second) }
            }
        }
    }

    Column {
        SpringDemoItem(name = "DampingRatioHighBouncy(0.2f)", offset[0].first.value)
        Spacer(modifier = Modifier.height(spacerHeight))
        SpringDemoItem(name = "DampingRatioMediumBouncy(0.5f)", offset[1].first.value)
        Spacer(modifier = Modifier.height(spacerHeight))
        SpringDemoItem(name = "DampingRatioLowBouncy(0.75f)", offset[2].first.value)
        Spacer(modifier = Modifier.height(spacerHeight))
        SpringDemoItem(name = "DampingRatioNoBouncy(1f)", offset[3].first.value)
        Spacer(modifier = Modifier.height(spacerHeight))
        Log.e("TAG", "value:${offset[4].first.value}")
        SpringDemoItem(name = "dampingRatio = 2f", offset[4].first.value)
        Spacer(modifier = Modifier.height(spacerHeight))
        Button(onClick = {
            targetValue = (1f - targetValue)
        }) {
            Text(text = "Start animations")
        }
    }
}

@Composable
fun SpringDemoItem(name: String, offsetX: Float) {
    Column {
        Text(name)
        Spacer(modifier = Modifier.height(5.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            Spacer(
                modifier = Modifier
                    .width(200.dp)
                    .height(15.dp)
                    .background(Color.Gray)
            )
            Box(modifier = Modifier.offset((offsetX * 185).dp)) {
                Spacer(
                    modifier = Modifier
                        .size(15.dp)
                        .background(Color.Red)
                )
            }
        }
    }
}