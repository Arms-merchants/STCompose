package com.example.stcompose.Demo5

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.stcompose.R

/**
 *    author : heyueyang
 *    time   : 2023/03/23
 *    desc   :
 *    version: 1.0
 */
@Composable
fun TestDrawWidthContent() {
    Box(modifier = Modifier
        .size(100.dp)
        .padding(10.dp)
        .drawWithContent {
            drawContent()
            drawCircle(
                color = Color.Red,
                radius = 5.dp.toPx(),
                center = Offset(drawContext.size.width, 0f)
            )
        }) {
        Image(
            painter = painterResource(id = R.mipmap.desert_chic),
            contentDescription = "",
            modifier = Modifier.clip(shape = RoundedCornerShape(10.dp))
        )
    }
}

@Composable
fun TestDrawWithAnimate() {
    val transition = rememberInfiniteTransition()
    val alpha by transition.animateFloat(
        initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val context = LocalContext.current
    Box(modifier = Modifier
        .size(340.dp, 300.dp)
        .drawWithCache {
            val image1 = ImageBitmap.imageResource(context.resources, id = R.mipmap.desert_chic)
            val image2 = ImageBitmap.imageResource(context.resources, id = R.mipmap.easy_care)
            onDrawBehind {
                drawImage(
                    image1, dstSize = IntSize(100.dp.roundToPx(), 100.dp.roundToPx()),
                    dstOffset = IntOffset.Zero,
                    alpha = alpha
                )
                drawImage(
                    image2, dstSize = IntSize(100.dp.roundToPx(), 100.dp.roundToPx()),
                    dstOffset = IntOffset(200.dp.roundToPx(), 0),
                    alpha = alpha
                )
            }
        })
}
