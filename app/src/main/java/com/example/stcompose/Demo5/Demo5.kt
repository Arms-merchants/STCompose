package com.example.stcompose.Demo5

import android.annotation.SuppressLint
import com.example.stcompose.R
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.core.graphics.scale
import kotlin.math.min
import kotlin.math.sin

/**
 *    author : heyueyang
 *    time   : 2023/03/17
 *    desc   :
 *    version: 1.0
 */
/** ---    这里基本上就类似Android原生体系中的自定义View和自定义ViewGroup---*/

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.firstBaseLineToTop(baseLineToptoTop: Dp) = Modifier.layout { measurable, constraints ->
    val p = measurable.measure(constraints)
    //检测元素是否支持设置Baseline，如果没有理解错误的情况下，这玩意应该就是文本控件才有的
    check(p[FirstBaseline] != AlignmentLine.Unspecified)
    val firstLine = p[FirstBaseline]
    //获取到基线的高度，然后用需要需要的高度剪掉已有的基线高度
    val placeableY = baseLineToptoTop.roundToPx() - firstLine
    //所以实际的高度位置就是当前控件的高，加上排查掉控件已有的基线高度
    val height = p.height + placeableY
    layout(p.width, height) {
        //设置控件显示的位置
        p.placeRelative(0, placeableY)
    }
}

@Preview
@Composable
fun DemoScreen() {
    Row {
        Box(
            modifier = Modifier
                //.firstBaseLineToTop(50.dp) 这玩意设置就会报错，因为它没有对应属性
                .background(Color.Red)
                .size(50.dp)
        )
        Text("Hi there!", Modifier.firstBaseLineToTop(24.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = "Hi there!", Modifier.padding(top = 24.dp))
    }
    //自己去实现一个简单的列控件
    MyOwnColumn(modifier = Modifier.padding(8.dp)) {
        Text(text = "1")
        Text(text = "2")
        Text(text = "3")
        Text(text = "4")
        TwoTexts(
            text1 = "Hi123123" +
                    "\n11231231", text2 = "there"
        )
        IntrinsicRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Text(
                text = "1231\n23",
                Modifier
                    .wrapContentWidth(Alignment.Start)
                    .layoutId("main")
            )
            Divider(
                color = Color.Black, modifier = Modifier
                    .width(4.dp)
                    //这里就是依靠父组件的高度来确定，通过固有特性测量就可以预先获取到这个高度
                    .fillMaxHeight()
                    .layoutId("divider")
            )
            Text(
                text = "123123123423423432432432423",
                Modifier
                    .wrapContentWidth(Alignment.End)
                    .layoutId("main")
            )
        }

        SubcomposeRow(
            modifier = Modifier.fillMaxWidth(),
            text = {
                Text(text = "12312313", Modifier.wrapContentWidth(Alignment.Start))
                Text(text = "Right", Modifier.wrapContentWidth(Alignment.End))
            }) {
            Log.e("TAG", "构建Divider")
            //int转DP
            val heightDp = with(LocalDensity.current) { it.toDp() }
            Divider(
                color = Color.Yellow, modifier = Modifier
                    .width(4.dp)
                    .height(heightDp)
            )
        }
    }

    CanvasDemo()
}

@Composable
fun MyOwnColumn(modifier: Modifier, content: @Composable () -> Unit) {
    Layout(content = content, modifier = modifier) { measureables, constraints ->
        //placeables是经过测量的子元素，它拥有自身的尺寸
        val placeables = measureables.map { measurable ->
            //根据父布局的约束，测量子元素
            measurable.measure(constraints)
        }
        layout(constraints.maxWidth, constraints.maxHeight) {
            var y = 0
            //开始实际的布局控件
            placeables.forEach { placeable ->
                //遍历每个元素，将元素放置到预期的位置
                placeable.placeRelative(0, y)
                //因为是行布局，所以每个元素下移到下一行的位置
                y += placeable.height
            }
        }
    }
}

/**
 * 固有尺寸的例子，因为在Compose中是不许多次测量的，如果发生多次测量就会报错，
 * 例如在两个文本控件中间插入一个分割线，并且要求分割线的高度为高的文本控件的高度，固有特性测量为我们提供了预先测量所有子控件确定自身constraints的能力，并在
 * 正式测量阶段对子组件的测量产生影响
 */
@Composable
fun TwoTexts(modifier: Modifier = Modifier, text1: String, text2: String) {
    //为高度设置固有特性测量
    Row(
        modifier = modifier
            //相当于获取子组件的最大的高度作为Row的高度
            .height(IntrinsicSize.Min)
            .background(Color.Yellow)
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
                .wrapContentWidth(Alignment.Start), text = text1
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(Color.Black)
        )
        Text(
            text = text2, modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
                .wrapContentWidth(Alignment.End)
        )
    }
}

/**
 * SubcomposeLayout,可以对组件进行部分预测量，例如在线的例子里还是中间线的高度是需要依赖文本控件中更高的那个高度，
 *
 */
@Composable
fun SubcomposeRow(
    modifier: Modifier,
    text: @Composable () -> Unit,
    divider: @Composable (Int) -> Unit
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        Log.e("TAG", "Sub Scope")
        var maxHeight = 0
        //每个组件都有自己唯一的slotId,Text的就是text
        //subcompose就是通过slotId在后面的compose中获取指定的元素组合，下面的就是获取所有的text组件
        val placeables = subcompose("text", text).map {
            Log.e("TAG", "Text 测量")
            val placeable = it.measure(constraints = constraints)
            maxHeight = placeable.height.coerceAtLeast(maxHeight)
            placeable
        }
        val dividerPlaceable = subcompose("divider") {
            Log.e("TAG", "subcompose 回调divier")
            divider(maxHeight)
        }.map {
            it.measure(constraints.copy(minWidth = 0))
        }
        assert(dividerPlaceable.size == 1, { "DividerScope Error!" })
        layout(constraints.maxWidth, constraints.maxHeight) {
            Log.e("TAG", "layout")
            placeables.forEach {
                it.placeRelative(0, 0)
            }
            dividerPlaceable.forEach {
                it.placeRelative((constraints.maxWidth - it.width) / 2, 0)
            }
        }
    }
}


@Composable
fun TestLayout5(modifier: Modifier, content: @Composable () -> Unit) {
    Layout(content = content, modifier = modifier, measurePolicy = object : MeasurePolicy {
        override fun MeasureScope.measure(
            measurables: List<Measurable>,
            constraints: Constraints
        ): MeasureResult {
            val dividerConstraints = constraints.copy(minWidth = 0)
            val mainPlaceables = measurables.filter {
                it.layoutId == "main"
            }.map {
                it.measure(constraints)
            }
            val divierPlaceable =
                measurables.first { it.layoutId == "divider" }.measure(dividerConstraints)
            return layout(constraints.maxWidth, constraints.maxHeight) {
                mainPlaceables.forEach {
                    it.placeRelative(0, 0)
                }
                divierPlaceable.placeRelative(
                    constraints.maxWidth / 2 - divierPlaceable.width / 2,
                    0
                )
            }
        }

        override fun IntrinsicMeasureScope.minIntrinsicHeight(
            measurables: List<IntrinsicMeasurable>,
            width: Int
        ): Int {
            var maxHeight = 0
            measurables.forEach {
                maxHeight = it.minIntrinsicHeight(width = width).coerceAtLeast(maxHeight)
            }
            return maxHeight
        }

    })
}


/**
 * 实现Row的固有特性测量
 */
@Composable
fun IntrinsicRow(modifier: Modifier, content: @Composable () -> Unit) {
    Layout(content = content,
        modifier = modifier,
        measurePolicy = object : MeasurePolicy {
            override fun MeasureScope.measure(
                measurables: List<Measurable>,
                constraints: Constraints
            ): MeasureResult {
                val devideConstraints = constraints.copy(minWidth = 0)
                val mainPlaceables = measurables.filter {
                    it.layoutId == "main"
                }.map {
                    it.measure(constraints)
                }
                val devidePlaceable =
                    measurables.first { it.layoutId == "divider" }.measure(devideConstraints)
                val midPos = constraints.maxWidth / 2 - devidePlaceable.width / 2
                return layout(constraints.maxWidth, constraints.maxHeight) {
                    mainPlaceables.forEach {
                        it.placeRelative(0, 0)
                    }
                    devidePlaceable.placeRelative(midPos, 0)
                }
            }

            /**
             * 固定特性的测量方法，根据width和height以及max和min组合有4个方法
             */
            override fun IntrinsicMeasureScope.minIntrinsicHeight(
                measurables: List<IntrinsicMeasurable>,
                width: Int
            ): Int {
                var maxHeight = 0
                measurables.forEach {
                    //根据宽度获取最大的高度，因为这里的宽度是fillMaxWidth，所以宽度固定
                    maxHeight = it.minIntrinsicHeight(width).coerceAtLeast(maxHeight)
                }
                return maxHeight
            }
        }
    )
}

/**------ 绘制 -----*/

@Composable
fun CanvasDemo() {
    val sweepAngle = remember {
        mutableStateOf(180f)
    }
    Canvas(
        modifier = Modifier
            .size(300.dp)
            .padding(30.dp)

    ) {
        drawCircle(
            Color.Gray,
            center = Offset(drawContext.size.width / 2f, drawContext.size.height / 2f),
            style = Stroke(20.dp.toPx())
        )

        drawArc(
            color = Color.Yellow,
            startAngle = -90f,
            sweepAngle = sweepAngle.value,
            useCenter = false,
            style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun DrawDemo5_2() {
    Card(shape = RoundedCornerShape(10.dp), modifier = Modifier
        .size(100.dp)
        .padding(10.dp)
        .drawWithContent {
            //绘制本身的内容
            drawContent()
            drawCircle(
                color = Color.Red,
                radius = 10.dp.toPx(),
                center = Offset(drawContext.size.width, 0f)
            )
        }
        .drawBehind {
            //在背景层绘制，也就是如果在这里绘制红点的话会展示在图像的后面
        }) {
        Image(painter = painterResource(id = R.mipmap.ic_hotel_share_img), contentDescription = "")
    }
}

@Composable
fun DrawDemo5_3() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val transition = rememberInfiniteTransition()
        val alpla by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        val context = LocalContext.current
        Box(modifier = Modifier
            .size(340.dp, 300.dp)
            .drawWithCache {
                //在涉及到bitmap时等资源时，如果每次都跟随重组那么必然会造成内存的抖动，那么这个时候可以drawWithCache来使用
                val image1 = ImageBitmap.imageResource(context.resources, id = R.mipmap.easy_care)
                val image2 = ImageBitmap.imageResource(context.resources, id = R.mipmap.desert_chic)
                onDrawBehind {
                    drawImage(
                        image1,
                        dstSize = IntSize(100.dp.roundToPx(), 100.dp.roundToPx()),
                        dstOffset = IntOffset.Zero,
                        alpha = alpla
                    )
                    drawImage(
                        image2,
                        dstSize = IntSize(100.dp.roundToPx(), 100.dp.roundToPx()),
                        dstOffset = IntOffset(150.dp.roundToPx(), 0),
                        alpha = alpla
                    )
                }
            }) {
        }
    }
}

@Composable
fun Test5() {
    /*TestLayout(modifier = Modifier.fillMaxWidth()) {
        Text(text = "1")
        Text(text = "2")
        Text(text = "3")
    }*/
    /* TestTwoTextLayout(modifier = Modifier
         .fillMaxWidth()
         .padding(top = 200.dp), textContent = {
         Text(text = "12312\n3123", modifier = Modifier.wrapContentWidth(Alignment.Start))
         Text(text = "right", modifier = Modifier.wrapContentWidth(Alignment.End))
     }) { height ->
         val hd = with(LocalDensity.current) { height.toDp() }
         Divider(
             modifier = Modifier
                 .height(hd)
                 .width(4.dp), color = Color.Black
         )
     }*/
    /*TestLayout5(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Text(
            text = "Left", modifier = Modifier
                .wrapContentWidth(Alignment.Start)
                .layoutId("main")
        )
        Divider(
            color = Color.Black, modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .layoutId("divider")
        )
        Text(
            text = "Right",
            Modifier
                .wrapContentWidth(Alignment.End)
                .layoutId("main")
        )
    }*/
    //TestDraw()
    //TestDrawWidthContent()
    // TestDrawWithAnimate()
    // DrawDemo5_4()
    TestWaveLoading()
}


@Composable
fun TestWaveLoading() {
    var _progress by remember {
        mutableStateOf(0.0f)
    }
    var _velocity by remember {
        mutableStateOf(1.0f)
    }
    var _amplitude by remember {
        mutableStateOf(0.2f)
    }
    val size = LocalDensity.current.run {
        200.dp.roundToPx()
    }
    val _bitmap =
        ImageBitmap.imageResource(id = R.mipmap.logo_nba).asAndroidBitmap().scale(size, size)

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterHorizontally)
        ) {
            WaveLoading(
                modifier = Modifier
                    .size(200.dp)
                    .clipToBounds()
                    .align(Alignment.Center),
                waveConfig = WaveConfig(_progress, _amplitude, _velocity),
                bitmap = _bitmap
            )
        }
        LabelSlider(label = "Progress", value = _progress, onValueChange = {
            _progress = it
        }, range = 0f..1f)
        LabelSlider(label = "Velocity", value = _velocity, onValueChange = {
            _velocity = it
        }, range = 0f..1f)
        LabelSlider(label = "Amplitude", value = _amplitude, onValueChange = {
            _amplitude = it
        }, range = 0f..1f)
    }


}

@Composable
private fun LabelSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>
) {
    Row(Modifier.padding(start = 10.dp, end = 10.dp)) {
        Text(
            text = label, modifier = Modifier
                .width(100.dp)
                .align(Alignment.CenterVertically)
        )
        Slider(
            modifier = Modifier.align(Alignment.CenterVertically),
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = Color.Yellow,
                activeTrackColor = Color.Blue,
                inactiveTrackColor = Color.Green
            )
        )
    }
}


@Composable
fun TestTwoTextLayout(
    modifier: Modifier,
    textContent: @Composable () -> Unit,
    divider: @Composable (Int) -> Unit
) {
    SubcomposeLayout(modifier, measurePolicy = { constraints ->
        var maxHeight = 0
        val ps = subcompose("text", textContent).map {
            val p = it.measure(constraints)
            maxHeight = p.height.coerceAtLeast(maxHeight)
            p
        }
        val dividerP = subcompose("divider") {
            divider(maxHeight)
        }.map {
            it.measure(constraints.copy(minWidth = 0))
        }
        layout(constraints.maxWidth, constraints.maxHeight) {
            ps.forEach {
                it.placeRelative(0, 0)
            }
            dividerP.forEach {
                it.placeRelative(constraints.maxWidth / 2 - it.width / 2, 0)
            }
        }
    })
}


@Composable
fun TestLayout(modifier: Modifier, content: @Composable () -> Unit) {
    Layout(modifier = modifier.fillMaxWidth(), content = content) { measurables, constraints ->
        val p = measurables.map {
            it.measure(constraints)
        }
        var y = 0
        layout(constraints.maxWidth, constraints.maxHeight) {
            p.forEach {
                it.placeRelative(0, y)
                y += it.height
            }
        }
    }
}

@Composable
fun TestDraw() {
    Canvas(modifier = Modifier
        .size(300.dp)
        .padding(10.dp), onDraw = {
        drawCircle(
            color = Color.Gray,
            radius = drawContext.size.width / 2,
            center = Offset(drawContext.size.width / 2, drawContext.size.height / 2),
            style = Stroke(20.dp.toPx(), cap = StrokeCap.Round)
        )
        drawArc(
            color = Color.Yellow,
            startAngle = -90f,
            sweepAngle = 180f,
            useCenter = false,
            style = Stroke(20.dp.toPx(), cap = StrokeCap.Round)
        )
    })
}


@Composable
fun DrawDemo5_4() {
    val imageBitmap = ImageBitmap.imageResource(id = R.mipmap.easy_care)
    Canvas(modifier = Modifier.size(200.dp)) {
        drawImage(imageBitmap, colorFilter = run {
            val cm = ColorMatrix().apply { setToSaturation(0f) }
            ColorFilter.colorMatrix(cm)
        })
    }

    Canvas(modifier = Modifier, onDraw = {
        drawPath(
            path = buildWavePath(
                width = size.width,
                size.height,
                amplitude = size.height * 0.2f,
                progress = 0.5f
            ),
            brush = ShaderBrush(ImageShader(imageBitmap)),
            alpha = 0.5f
        )
    })
}

/**
 * @param width 画布绘制区域x
 * @param height 画布绘制区域y
 * @param amplitude 波浪y轴振幅
 * @param progress 加载进度
 */
private fun buildWavePath(width: Float, height: Float, amplitude: Float, progress: Float): Path {
    var adjustHeight = min(height * 0f.coerceAtLeast(1 - progress), amplitude)
    var adjustWidth = 2 * width
    val dp = 2
    return Path().apply {
        reset()
        moveTo(0f, height)
        lineTo(0f, height * (1 - progress))
        if (progress > 0f && progress < 1f) {
            if (adjustHeight > 0) {
                var x = dp
                while (x < adjustWidth) {
                    lineTo(
                        x.toFloat(),
                        height * (1 - progress) - adjustHeight / 2f * sin(4.0 * Math.PI * x / adjustWidth).toFloat()
                    )
                    x += dp
                }
            }
        }
        lineTo(adjustWidth, height * (1 - progress))
        lineTo(adjustWidth, height)
        close()
    }
}






