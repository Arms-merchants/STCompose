package com.example.stcompose.brick

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stcompose.ui.theme.BodyColor
import com.example.stcompose.ui.theme.BrickSpirit
import com.example.stcompose.ui.theme.ScreenBackground

/**
 *    author : heyueyang
 *    time   : 2023/04/13
 *    desc   :
 *    version: 1.0
 */
@Composable
fun AppIcon() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BodyColor, RoundedCornerShape(50.dp))
            .padding(top = 30.dp)
    ) {
        Box(modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
            Box(
                modifier = Modifier
                    .size(360.dp, 220.dp)
                    .align(Alignment.Center)
                    .padding(20.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawScreenBorder(
                        Offset(0f, 0f),
                        Offset(size.width, 0f),
                        Offset(0f, size.height),
                        Offset(size.width, size.height)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .background(ScreenBackground)
                )

                Text(
                    text = "TETRIS",
                    textAlign = TextAlign.Center,
                    color = BrickSpirit,
                    fontSize = 75.sp,
                    modifier = Modifier.align(Alignment.Center),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(start = 45.dp, end = 45.dp)
                .height(160.dp)
                .padding(bottom = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.55f)
            ) {
                GameButton(
                    size = DirectionButtonSize,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                GameButton(
                    size = DirectionButtonSize,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                GameButton(
                    size = DirectionButtonSize,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
                GameButton(
                    size = DirectionButtonSize,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.45f)
            ) {
                GameButton(
                    size = RotateButtonSize,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}