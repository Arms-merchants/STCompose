package com.example.stcompose.demo7

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 *    author : heyueyang
 *    time   : 2023/03/29
 *    desc   : 第七章关于手势的处理
 *    version: 1.0
 */

/**
 * 点击，通过Modifier.clickable来实现
 */
@Composable
fun Demo7_1() {
    //通过Modifier.clickable给控件设置点击事件
    Box(modifier = Modifier.clickable(enabled = true) {

    })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Demo7_2() {
    Box(
        //组合点击处理
        modifier = Modifier.combinedClickable(onLongClick = {
            //长按
        }, onDoubleClick = {
            //双击
        }, onClick = {
            //单击
        })
    )
}


/**
 * 实现一个简单的滑块拖动效果
 * Modifier.draggable()
 */
@Composable
fun Demo7_3(modifier: Modifier = Modifier) {
    //remember 需要倒入getValue 和setValue 这as有bug吧
    var offsetX by remember { mutableStateOf(0f) }
    val boxSideLengthDp = 50.dp
    val density = LocalDensity.current
    val boxSlideLengthPx = with(density) {
        boxSideLengthDp.toPx()
    }
    val draggableState = rememberDraggableState() {
        //it返回的是事件相应滑动的距离，不是累加的，是一次事件的响应
        offsetX = (offsetX + it).coerceIn(0f, 3 * boxSlideLengthPx)
    }
    Box(
        modifier = Modifier
            .padding(top = 100.dp)
            .width(boxSideLengthDp * 4)
            .height(boxSideLengthDp)
            .background(color = Color.LightGray)
    ) {
        Box(
            modifier = Modifier
                .width(with(density) { offsetX.toDp() })
                .height(boxSideLengthDp)
                .background(Color.Green)
        )
        Box(
            modifier = Modifier
                .size(boxSideLengthDp)
                .offset(with(density) { offsetX.toDp() })
                .border(1.dp, Color.Black)
                .draggable(state = draggableState,
                    //这个只能支持横向或者竖向的滑动
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        offsetX = if (offsetX >= 1.5 * boxSlideLengthPx) {
                            3 * boxSlideLengthPx
                        } else {
                            0f
                        }
                    })
                .background(color = Color.Green)
        )
    }
}

enum class Status {
    OPEN, CLOSE
}

/**
 * 通过Swipeable来实现的滑动开关，效果和3类似，不过在Draggable中需要自己计算位置来实现边界值的处理
 * 实现一个滑动开关比原生的view简单多了
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Demo7_4() {
    val blockSize = 48.dp
    val blockPx = with(LocalDensity.current) {
        blockSize.toPx()
    }
    val swipeableState = rememberSwipeableState(initialValue = Status.CLOSE)
    Box(
        modifier = Modifier
            .padding(top = 100.dp, start = 50.dp)
            //这里必须要Dp写前面
            .size(width = (blockSize * 2), height = blockSize)
            .clip(RoundedCornerShape(50.dp))
            .background(Color.LightGray)
    ) {
        Box(modifier = Modifier
            //注意Modifier调用链的问题，所以offset必须在swipeable和background之前设置
            //也就是要在偏移之后在构建视图样式，不然的话先构建的样式不会有偏移的效果
            .offset {
                IntOffset(swipeableState.offset.value.toInt(), 0)
            }
            .swipeable(
                state = swipeableState,
                //设置锚点
                anchors = mapOf(0f to Status.CLOSE, blockPx to Status.OPEN),
                //反转方向，就是手势滑动方向想视图方向相反
                //reverseDirection = true,
                thresholds = { from, to ->
                    if (from == Status.CLOSE) {
                        //大于30%就触发
                        FractionalThreshold(0.3f)
                    } else {
                        //大于50%才触发
                        FractionalThreshold(0.5f)
                    }
                },
                orientation = Orientation.Horizontal
            )
            .size(blockSize)
            .clip(RoundedCornerShape(50.dp))
            .background(Color.DarkGray)
        )
    }
    //直接通过state的currentValue来获取当前的状态
    Log.e("TAG", "current Status:${swipeableState.currentValue}")
}

/**
 * transformable
 * 多指触控，实现一个方块响应缩放，旋转，拖动，单指不响应
 */
@Composable
fun Demo7_5() {
    val boxSize = 100.dp
    var offset by remember { mutableStateOf(Offset.Zero) }
    var ratationAngle by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }
    val transformableState =
        rememberTransformableState(onTransformation = { zoomChange: Float, panChange: Offset, rotationChange: Float ->
            //zoomChange 缩放变化 panChange 平移变化 rotationChange 旋转变化
            scale *= zoomChange
            offset += panChange
            ratationAngle += rotationChange
        })

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier
            .size(boxSize)
            //这里要注意调用顺序，如果旋转角度放在偏移的后面，那么会导致先偏移在旋转，会出先位置的不可预期
            .rotate(ratationAngle)
            .offset {
                IntOffset(offset.x.roundToInt(), offset.y.roundToInt())
            }
            .scale(scale)
            .background(Color.Green)
            //lockRotationOnZoomPan如果为true，那么在发生双指拖动或缩放的过程中，不会响应用户手势的旋转
            .transformable(state = transformableState, lockRotationOnZoomPan = false)
        )
    }
}

/**
 * Scrollable
 */
@Composable
fun Demo7_6() {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .height(100.dp)
            //reverseScrolling反向滚动，scroll的范围是0到MaxValue，默认情况下，向右滑动滑动距离增大，向左滑动距离减小，启始位置允许向右滑动
            // 而当reverseScrolling设置为true时，向右滑动距离减小，向左滑动距离增大，起始位置允许向左滑动
            .horizontalScroll(state = scrollState, reverseScrolling = true)
    ) {
        repeat(20) {
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Green)
            )
        }
    }
}

@Composable
fun Demo7_7() {
    val scrollState = rememberScrollState()
    Column {
        Row(
            Modifier
                .height(100.dp)
                //向右是数值增大，而要要布局向左的话需要将offset的x取反，并且要反转反向才能实现向左增大
                //但是因为Row的layout默认布局测量的效果，超出屏幕的子组件测量结果为0，所在我们增加偏移量时只能是增加一片空白
                .scrollable(
                    scrollState, orientation = Orientation.Horizontal, reverseDirection = true
                )
                .offset {
                    IntOffset(-scrollState.value, 0)
                }
        ) {
            repeat(20) {
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.Green)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            Modifier
                .height(100.dp)
                .scrollable(
                    scrollState,
                    orientation = Orientation.Horizontal,
                    reverseDirection = true
                )
                .layout { measurable, constraints ->
                    //构建新的childConstraints，不加宽度限制
                    val childConstraints = constraints.copy(maxWidth = Constraints.Infinity)
                    //根据新的childConstraints重新测量
                    val placeable = measurable.measure(childConstraints)
                    //Row没设置width，所以它的宽度为屏幕宽度，控件的宽度和屏幕宽度取小
                    val width = placeable.width.coerceAtMost(constraints.maxWidth)
                    val height = placeable.height.coerceAtMost(constraints.maxHeight)
                    //这里的placeable是Row的宽度，所以它的最大滑动距离是控件的最大宽度➖上面一屏幕宽度
                    val scrollDistance = placeable.width - width
                    layout(width, height) {
                        //确保滑动在范围内
                        val scroll = scrollState.value.coerceIn(0, scrollDistance)
                        //实际的偏移量
                        val xOffset = -scroll
                        placeable.placeRelativeWithLayer(xOffset, 0)
                    }
                }
        ) {
            repeat(20) {
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(Color.Green)
                )
            }
        }
    }
}

/**
 * 下拉刷新的状态
 */
class SmartSwipeRefreshState {
    //一个dp解析的动画
    private val indicatorOffsetAnimatable = Animatable(0.dp, Dp.VectorConverter)

    //当前偏移的值
    val indicatorOffset get() = indicatorOffsetAnimatable.value
    private val _indicatorOffsetFlow = MutableStateFlow(0f)
    val indicatorOffsetFlow: Flow<Float> = _indicatorOffsetFlow

    //是否是在刷新过程中
    val isSwipeInProgress by derivedStateOf { indicatorOffset != 0.dp }

    //是否是刷新
    var isRefreshing: Boolean by mutableStateOf(false)

    /**
     * 更新偏移量的值
     */
    fun updateOffsetDelta(value: Float) {
        _indicatorOffsetFlow.value = value
    }

    /**
     * 动画直接跳转到
     */
    suspend fun snapToOffset(value: Dp) {
        indicatorOffsetAnimatable.snapTo(value)
    }

    /**
     * 动画跳转到
     */
    suspend fun animateToOffset(value: Dp) {
        indicatorOffsetAnimatable.animateTo(value, tween(1000))
    }
}

/**
 * NestedScrollConnection 有四个方法
 */
private class SmartSwipeRefreshNestedScrollConnection(
    val state: SmartSwipeRefreshState,
    val height: Dp
) : NestedScrollConnection {

    /**
     * 在上滑时，先判断是否有加载的状态组件显示，如果有的话，先将状态视图偏移向上
     */
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        Log.e("TAG", "onPreScroll：${available.y}")
        //available.y<0手指向上话，先判断状态组件是否展示，如果展示的话那么先偏移隐藏状态组件，否则不消费事件
        //往上是负
        return if (source == NestedScrollSource.Drag && available.y < 0) {
            Log.e("TAG", "onPreScroll <0")
            state.updateOffsetDelta(available.y)
            //只有在加载视图显示的时候才先处理加载视图的偏移
            if (state.isSwipeInProgress) Offset(x = 0f, available.y) else Offset.Zero
        } else {
            Log.e("TAG", "onPreScroll <0")
            Offset.Zero
        }
    }


    /**
     * 手指向下滑动时，先要判断子组件是否到顶了，到顶了再响应刷新组件的展示
     */
    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        Log.e("TAG", available.y.toString())
        //onPostScroll是在子组件响应事件后再响应事件，那么只有available大于0说明内容布局的事件已经到顶部了，
        if (source == NestedScrollSource.Drag && available.y > 0) {
            Log.e("TAG", "onPostScroll>0")
            state.updateOffsetDelta(available.y)
            return Offset(0f, available.y)
        } else {
            Log.e("TAG", "onPostScroll =0")
            return Offset.Zero
        }
    }

    /**
     * 松手时需要判断状态视图是否需要吸顶展示，当状态中的偏移已经大于高度一半时，移动到整个高度
     * 当松手时onPreFling 和onPostFling 都会回调，只不过速度大小的问题
     */
    override suspend fun onPreFling(available: Velocity): Velocity {
        if (state.indicatorOffset > height / 2) {
            Log.e("TAG", "onPreFling >2")
            state.animateToOffset(height)
            state.isRefreshing = true
        } else {
            //如果不加这个判断每次不符合高度的时候都会近这个方法，只有在是刷新的时候处理就可以了
            if (state.isRefreshing) {
                Log.e("TAG", "onPreFling")
                state.animateToOffset(0.dp)
            }
        }
        //不需要消耗事件
        return super.onPreFling(available)
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        Log.e("TAG", "onPostFling")
        return super.onPostFling(consumed, available)
    }
}

/**
 * @param indicator loaidng的视图
 * @param content 内容视图
 */
@Composable
private fun SubcomposeSmartSwipeRefresh(
    indicator: @Composable () -> Unit,
    content: @Composable (Dp) -> Unit
) {
    SubcomposeLayout { constraints ->
        val indicatorPlaceable = subcompose("indicator", indicator).first().measure(constraints)
        val contentPlaceable = subcompose("content") {
            content(indicatorPlaceable.height.toDp())
        }.map {
            it.measure(constraints)
        }.first()
        layout(constraints.maxWidth, constraints.maxHeight) {
            contentPlaceable.placeRelative(0, 0)
        }
    }
}


@Composable
fun SmartSwipeRefresh(
    onRefresh: suspend () -> Unit,
    state: SmartSwipeRefreshState = remember { SmartSwipeRefreshState() },
    loadingIndicator: @Composable () -> Unit = { CircularProgressIndicator() },
    content: @Composable () -> Unit
) {
    SubcomposeSmartSwipeRefresh(indicator = loadingIndicator) { height ->
        val smartSwipeRefreshNestedScrollConnection = remember(state, height) {
            SmartSwipeRefreshNestedScrollConnection(state, height)
        }
        Box(
            Modifier.nestedScroll(smartSwipeRefreshNestedScrollConnection),
            contentAlignment = Alignment.TopCenter
        ) {
            //也就是视图在最开始的时候，loading是在屏幕外不可见的地方，content的顶部偏移等于视图能够响应的下拉
            Box(Modifier.offset(y = -height + state.indicatorOffset)) {
                loadingIndicator()
            }
            Box(modifier = Modifier.offset(y = state.indicatorOffset)) {
                content()
            }
        }
        val density = LocalDensity.current
        LaunchedEffect(Unit) {
            state.indicatorOffsetFlow.collect {
                val currentOffset = with(density) { state.indicatorOffset + it.toDp() }
                state.snapToOffset(currentOffset.coerceAtLeast(0.dp).coerceAtMost(height))
            }
        }
        LaunchedEffect(state.isRefreshing) {
            if (state.isRefreshing) {
                onRefresh()
                //这里因为是挂起函数，所以下面的方法需要等onRefresh执行完后才会执行，也就是隐藏了loadingview
                state.animateToOffset(0.dp)
                state.isRefreshing = false
            }
        }
    }
}

@Composable
fun SmartSwipeDemo() {
    val list = arrayListOf<String>()
    for (i in 0..100) {
        list.add(i.toString())
    }
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    //这个内部必须要要是能够处理滑动的组件
    SmartSwipeRefresh(onRefresh = {
        delay(1000)
        Toast.makeText(context, "刷新成功", Toast.LENGTH_LONG).show()
    }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .scrollable(
                    scrollState,
                    orientation = Orientation.Vertical,
                    reverseDirection = true
                )
                .layout { measurable, constraints ->
                    //主要这个copy设置的参数，因为这里是垂直列表，那么需要设置的就是maxHeight
                    val newConstraints = constraints.copy(maxHeight = Constraints.Infinity)
                    val placeable = measurable.measure(newConstraints)
                    val width = placeable.width.coerceAtMost(constraints.maxWidth)
                    val height = placeable.height.coerceAtMost(constraints.maxHeight)
                    val scrollDistance = placeable.height - height
                    Log.e("TAG", "scrollDistance:${scrollDistance}")
                    layout(width = width, height) {
                        val scroll = scrollState.value.coerceIn(0, scrollDistance)
                        val yOffset = -scroll
                        Log.e("TAG", "offset:${yOffset}")
                        placeable.placeRelative(0, yOffset)
                    }
                }
        ) {
            repeat(100) {
                Box(
                    Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                        .background(Color.Green)
                ) {
                    Text(it.toString())
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

/*        LazyColumn(content = {
            items(list.size) { index ->
                Box(
                    Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                        .background(Color.Green)
                ) {
                    Text(list[index])
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        })*/
    }
}

/**
 * 定制手势处理，pointerInput，可以更加戏颗粒度的定制
 */
@Composable
fun Demo7_8() {
    val boxSize = 100.dp
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(boxSize)
                .background(Color.Green)
                .pointerInput(Unit) {
                    //检测点击事件
                    detectTapGestures(
                        onDoubleTap = { Log.e("TAG", "onDoubleTap") },
                        onLongPress = { Log.e("TAG", "onLongPress") },
                        onPress = { Log.e("TAG", "onPress") },
                        onTap = { Log.e("TAG", "onTap") }
                    )
                    //检测任意方向的拖动手势
                    detectDragGestures(
                        onDrag = { change, dragAmount ->

                        },
                        onDragStart = {

                        },
                        onDragCancel = {

                        },
                        onDragEnd = {

                        }
                    )
                    //监听长按手势后的拖动手势
                    detectDragGesturesAfterLongPress(
                        onDrag = { change, dragAmount ->

                        }
                    )
                    //监听水平方向的拖动手势
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { change, dragAmount ->

                        }
                    )
                    //监听垂直方向的拖动手势
                    detectVerticalDragGestures(
                        onVerticalDrag = { change, dragAmount ->

                        }
                    )

                }
        )
    }
}

@Composable
fun Demo7_9() {
    val boxSize = 100.dp
    var offset by remember {
        mutableStateOf(Offset.Zero)
    }
    val density = LocalDensity.current
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(boxSize)
                .offset(
                    x = with(density) { offset.x.toDp() },
                    y = with(density) { offset.y.toDp() }
                )
                //还是要注意这个的调用时机，如果在offset之前调用了，那么就会在偏移之前的位置上绘制背景色了
                .background(Color.Green)
                //手势定制
                .pointerInput(Unit) {
                    //检测拖动手势
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            offset += dragAmount
                        }
                    )
                }
        )

    }
}

@Composable
fun Demo7_10() {
    var scale by remember {
        mutableStateOf(1f)
    }
    var rotate by remember {
        mutableStateOf(0f)
    }
    var offset by remember {
        mutableStateOf(Offset.Zero)
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .scale(scale = scale)
                //注意rotate需要在offset之前
                .rotate(rotate)
                .offset {
                    IntOffset(offset.x.roundToInt(), offset.y.roundToInt())
                }
                .background(Color.Green)
                .pointerInput(Unit) {
                    //多指手势检测 中心，偏移量，缩放，旋转角度
                    detectTransformGestures(panZoomLock = false) { centroid, pan, zoom, rotation ->
                        scale *= zoom
                        rotate += rotation
                        offset += pan
                    }
                }
        )
    }
}

@Composable
fun Demo7_11() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier
            .size(100.dp)
            .background(Color.Yellow)
            .pointerInput(Unit) {
                //上面的detect相关的api，内部都是通过forEachGesture来实现的，它是在协程中调用的
                //并且都会通过调用awaitAllPointersUp()来保证所有手指均以抬起，并且和当前的组件生命周期对其
                //完成一轮监听后，在组件生命周期未结束时，继续下一轮的监听
                forEachGesture {
                    //
                    awaitPointerEventScope {
                        //
                        var event = awaitPointerEvent()
                        Log.e("TAG", event.changes[0].pressed.toString())

                    }
                }
            }
        )
    }
}

@Composable
fun Demo7_12() {
    var offset by remember { mutableStateOf(Offset.Zero) }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(100.dp)
                .offset {
                    IntOffset(x = offset.x.roundToInt(), y = offset.y.roundToInt())
                }
                .background(Color.Green)
                .pointerInput(Unit) {
                    //通过下面的组合就实现了detectDragGestures的拖拽效果
                    forEachGesture {
                        awaitPointerEventScope {
                            /**
                             * 手势的分发阶段分为3个
                             * Initial阶段 自上而下的分发手势事件
                             * Main阶段 自下而上的分发手势事件
                             * Final阶段 自上而下的分发手势事件
                             * 通过这个来控制控件在什么时机去处理手势事件
                             */
                            val pointerEvent = awaitPointerEvent(PointerEventPass.Main)
                            //单指操作的完整信息被封装在PointerInputChange对象里
                            val pointerInputChange = pointerEvent.changes[0]

                            pointerInputChange.changedToDown()

                            //获取第一手指落下的ACTION_DOWN事件
                            val point = awaitFirstDown()
                            //detectDragGestures和Draggable内部都是通过drag实现
                            drag(point.id) {
                                //PointerInputChange封装了一个手指事件变化参数
                                //获取位置的偏移量
                                offset += it.positionChange()
                            }
                        }
                    }
                }
        ) {

        }
    }
}

@Composable
fun Demo7_13() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    val pointerEvent = awaitPointerEvent(PointerEventPass.Initial)
                    Log.e("TAG", "first layer,downChange:${pointerEvent.changes[0].isConsumed}")
                }

            }, contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(400.dp)
                .background(Color.Green)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        val pointerEvent = awaitPointerEvent(PointerEventPass.Final)
                        Log.e("TAG", "two layer,downChange:${pointerEvent.changes[0].isConsumed}")
                    }
                }, contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier
                .size(200.dp)
                .background(Color.Yellow)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        val pointerEvent = awaitPointerEvent(PointerEventPass.Main)
                        val pointerInputChange = pointerEvent.changes[0]
                        //这里和书里有区别，书上的api已经弃用
                        if (pointerInputChange.pressed != pointerInputChange.previousPressed) {
                            //消费事件,原理的api是允许position和down单独消费的，但现在改为只能一起消费，所以单独相关的api都被标记为废弃了
                            pointerInputChange.consume()
                        }
                        Log.e("TAG", "third layer,downChange:${pointerInputChange.isConsumed}")
                    }
                })
        }
    }
}

@Composable
fun Demo7_14() {
    var offset by remember { mutableStateOf(Offset.Zero) }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .offset {
                    IntOffset(offset.x.roundToInt(), offset.y.roundToInt())
                }
                .background(
                    Color.Green
                )
                .pointerInput(Unit) {
                    forEachGesture {
                        awaitPointerEventScope {
                            val event = awaitFirstDown()
                            //通过drag来实现拖动监听
                            /* drag(event.id) {
                                 offset += it.positionChange()
                             }*/
                            //通过awaitDragOrCancellation实现拖动效果
                            while (true) {
                                val change = awaitDragOrCancellation(event.id)
                                if (change == null || change.changedToUp()) {
                                    //拖动事件被取消或者所有手指以抬起
                                    break
                                }
                                offset += change.positionChange()
                            }
                        }
                    }
                }
        )
    }
}

@SuppressLint("ReturnFromAwaitPointerEventScope", "MultipleAwaitPointerEventScopes")
@Composable
fun Demo7_15() {
    //记录横轴的速度
    var horizontalVelocity by remember {
        mutableStateOf<Float>(0f)
    }
    //记录垂直方向的速度
    var verticalVelocity by remember {
        mutableStateOf<Float>(0f)
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            //展示移动中的速度
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp), contentAlignment = Alignment.Center
            ) {
                Text(text = "Velocity", fontSize = 42.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = String.format(
                        "Horizontal: %.2f Vertical: %.2f",
                        horizontalVelocity,
                        verticalVelocity
                    ),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            //用动画来记录偏移量
            val offset = remember {
                Animatable(Offset.Zero, Offset.VectorConverter)
            }
            Box(
                Modifier
                    .size(350.dp)
                    .background(Color.Gray)
                    .pointerInput(Unit) {
                        //设置动画的边界值0-320dp
                        offset.updateBounds(
                            lowerBound = Offset.Zero,
                            upperBound = Offset(320.dp.toPx(), 320.dp.toPx())
                        )
                        //pointerInput中的方法是挂起函数，因为在手势的处理中都是通过挂起函数恢复来实现方法调用的
                        coroutineScope {
                            //当协程处理完一轮手势交互后，便会结束，当进行第二次手势交互时由于负责手势监听的协程已经结束，手势事件便会被丢弃掉。
                            //forEachGesture用与连续处理手势交互的监听
                            //之前忘记写这个函数进行包裹了，那么会造成手势事件只响应第一次的，后续的事件不响应了
                            forEachGesture {
                                //拿到第一个down事件
                                val pointerInputChange = awaitPointerEventScope { awaitFirstDown() }
                                //如果有动画先
                                offset.stop()
                                awaitPointerEventScope {
                                    var validDrag: PointerInputChange?
                                    do {
                                        validDrag =
                                                //awaitTouchSlopOrCancellation用于定制一次有效的监听，这里的有效自己确定条件
                                                //传入一个手指的id
                                            awaitTouchSlopOrCancellation(pointerInputChange.id) { change, overSlop ->
                                                //如果当前手指的位置变化不为0，那么就消费掉当前事件
                                                if (change.positionChange() != Offset.Zero) {
                                                    change.consume()
                                                }
                                            }
                                    } while (validDrag != null && !validDrag.isConsumed)//查询条件为不为null，并且是将事件已经消费
                                    if (validDrag != null) {
                                        //创建一个速度追踪器，它可以根据手指离屏幕的位置和速度信息来计算组件最终停留的位置
                                        val velocityTracker = VelocityTracker()
                                        var dragAnimJob: Job? = null
                                        //响应拖动事件
                                        drag(validDrag.id) {
                                            dragAnimJob = launch {
                                                //因为在手指拖拽的时候是不需要动画效果的，所以用snapTo直接移动到对应的位置
                                                offset.snapTo(
                                                    offset.value + it.positionChange()
                                                )
                                                //
                                                velocityTracker.addPosition(
                                                    it.uptimeMillis,//手势事件时间戳
                                                    it.position//当前手指在控件上的相对位置
                                                )
                                                //分别得出横向和纵向的速度
                                                horizontalVelocity =
                                                    velocityTracker.calculateVelocity().x
                                                verticalVelocity =
                                                    velocityTracker.calculateVelocity().y
                                            }
                                        }
                                        //这里应该就是手指抬起后，获取一下现在的速度
                                        horizontalVelocity = velocityTracker.calculateVelocity().x
                                        verticalVelocity = velocityTracker.calculateVelocity().y
                                        //衰减样本？awaitPointerEventScope为this，屏幕密度
                                        val decay = splineBasedDecay<Offset>(this)
                                        //计算结果值
                                        val targetOffset = decay
                                            //需要的参数：对应类型的转换器，初始值，初始的速度
                                            .calculateTargetValue(
                                                Offset.VectorConverter,
                                                offset.value,
                                                Offset(horizontalVelocity, verticalVelocity)
                                            )
                                            .run {
                                                Offset(
                                                    //限制一下x和y的范围
                                                    x.coerceIn(
                                                        0f,
                                                        320.dp.toPx()
                                                    ),
                                                    y.coerceIn(0f, 320.dp.toPx())
                                                )
                                            }
                                        //结束对应的协程
                                        dragAnimJob?.cancel()
                                        launch {
                                            //执行动画
                                            offset.animateTo(
                                                targetOffset,
                                                tween(2000, easing = LinearOutSlowInEasing)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }) {
                //实际展示效果的小球
                Box(
                    Modifier
                        .offset {
                            IntOffset(
                                x = offset.value.x.roundToInt(),
                                y = offset.value.y.roundToInt()
                            )
                        }
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.Green))
            }
            val scopeRemember = rememberCoroutineScope()
            Button(onClick = {
                scopeRemember.launch {
                    offset.snapTo(Offset.Zero)
                    horizontalVelocity = 0f
                    verticalVelocity = 0f
                }
            }) {
                Text(text = "重置")
            }
        }
    }

}


@Composable
fun Demo7() {
    Demo7_11()
}



