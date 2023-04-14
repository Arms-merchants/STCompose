package com.example.stcompose.demo6

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stcompose.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *    author : heyueyang
 *    time   : 2023/03/23
 *    desc   :
 *    version: 1.0
 */
/**
 * compose的动画分为高级动画和低级动画，
 * 高级动画是对低级动画的封装，开箱即用
 * AnimatedVisibility在视图可见状态发生改变时的动画，可以通过enter和exit来定制动画效果，并且进入和退出的动画分别有它们对应的父类限制
 */
@Composable
fun AnimatedVisibilityDemo() {
    var visible by remember {
        mutableStateOf(true)
    }
    val density = LocalDensity.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .wrapContentHeight(
                Alignment.CenterVertically
            )
    ) {
        AnimatedVisibility(
            visible = visible,
            //通过+运算符来组合多个已有的EnterTransition或ExitTransition
            enter = slideInVertically {
                //从顶部40dp的位置滑入
                with(density) { -40.dp.roundToPx() }
            } + expandVertically(
                //从顶部开始展开
                expandFrom = Alignment.Top
            ) + fadeIn(
                //从初始透明度0。3f开始淡入
                initialAlpha = 0.3f
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut()) {
            Image(
                painter = painterResource(id = R.mipmap.easy_care),
                contentDescription = "",
                modifier = Modifier
                    .size(200.dp)
                    .clickable {
                        visible = false
                    }
            )
        }
    }
}

/**
 * 动态修改状态触发AnimatedVisibility的重组
 */
@Composable
fun AnimatedVisibilityDemo2() {
    var visible by remember {
        mutableStateOf(true)
    }
    val density = LocalDensity.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight(
                Alignment.CenterVertically
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { visible = !visible }) {
                Text(text = "修改图片显示状态")
            }
            Spacer(modifier = Modifier.height(20.dp))
            AnimatedVisibility(
                enter = expandVertically(), visible = visible
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.easy_care),
                    contentDescription = "",
                    modifier = Modifier
                        .size(200.dp)
                )
            }
        }
    }
}

enum class AnimState {
    VISIBLE,
    INVISIBLE,
    APPEARING,
    DISAPPEARING
}

fun MutableTransitionState<Boolean>.getAnimationState(): AnimState {
    return when {
        this.isIdle && this.currentState -> AnimState.VISIBLE
        !this.isIdle && this.currentState -> AnimState.DISAPPEARING
        this.isIdle && !this.currentState -> AnimState.INVISIBLE
        else -> AnimState.APPEARING
    }
}

/**
 * AnimatedVisibility中通过visibleState来获取当前的状态
 */
@Composable
fun AnimatedState3() {
    val state = remember {
        //MutableTransitionState可变过度状态，这里初始的默认状态initialState为false，而后面将targetState修改为true，从而触发了引用state的composable重组
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    Column {
        AnimatedVisibility(visibleState = state) {
            Image(
                painter = painterResource(id = R.mipmap.easy_care),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
        }
        Text(text = state.getAnimationState().toString())
    }
}

/**
 * 去除默认动画的设置
 * 以及子组件去分别设置自己的动画效果
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Animation4() {
    val scope = rememberCoroutineScope()
    val state = remember {
        MutableTransitionState(true).apply {
            scope.launch {
                //延迟2秒修改为false
                delay(2000)
                targetState = false
            }
        }
    }

    AnimatedVisibility(
        visibleState = state,
        //去除默认的动画效果
        enter = EnterTransition.None,
        exit = ExitTransition.None
        //enter = fadeIn(),
        //exit = fadeOut(animationSpec = tween(5000))
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Green)
            /*.animateEnterExit(
                enter = slideInHorizontally(animationSpec = tween(5000)),
                exit = slideOutHorizontally(animationSpec = tween(5000)),
            )*/
        ) {
            val background by transition.animateColor(label = "") { state ->
                when (state) {
                    EnterExitState.Visible -> Color.Blue
                    else -> Color.Red
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    //给控件单独设置动画
                    .animateEnterExit(
                        enter = slideInHorizontally(animationSpec = tween(5000)),
                        exit = slideOutHorizontally(animationSpec = tween(5000))
                    )
                    .size(256.dp, 64.dp)
                    .background(background)
            )

            Box(
                modifier = Modifier
                    .padding(top = 200.dp)
                    .align(Alignment.Center)
                    .animateEnterExit(
                        enter = fadeIn(animationSpec = tween(1000)),
                        exit = fadeOut(animationSpec = tween(1000))
                    )
                    .size(100.dp, 20.dp)
                    .background(Color.LightGray)
            )

        }
    }
}

/**
 * AnimatedContent布局内容发生变化时的动画
 *
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedContentDemo() {
    Log.e("TAG", "AnimatedContentDemo")
    Row(modifier = Modifier.padding(top = 100.dp, start = 100.dp)) {
        var count by remember { mutableStateOf(0) }
        Button(onClick = {
            Log.e("TAG", "onClick")
            count++
        }) {
            Log.e("TAG", "Button")
            Text(text = "Add")
        }
        //targetState变化那么就会引起AnimatedContent的重组
        AnimatedContent(
            targetState = count,
            transitionSpec = {
                //with操作符号EnterTransition.with(exit: ExitTransition)
                //初始位置为width
                slideInHorizontally(initialOffsetX = { width -> -width }) + fadeIn() with slideOutHorizontally(
                    //目标位置为-width
                    targetOffsetX = { width -> width }) + fadeOut()
            }
        ) {
            Log.e("TAG", "AnimatedContent")
            Text(text = "Count:$count")
        }
    }
}

/**
 * 通过AnimatedContent来实现一个小图标和长文本布局的切换效果，
 * 延长了动画的切换时间，可以很清晰的看到动画的变化过程
 * 通过enterAnimated with exitAnimated构建ContentTransform
 * contentTransform 使用using操作符来添加SizeTransform来配置布局变化的关键帧，来确定动画的具体实现效果
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SizeTransformDemo() {
    var expanded by remember {
        mutableStateOf(false)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            //.background(color = MaterialTheme.colors.primary)
            .clickable {
                expanded = !expanded
            }
    ) {
        AnimatedContent(targetState = expanded, transitionSpec = {
            fadeIn(
                animationSpec = tween(
                    5000,
                    150
                )
            ) with fadeOut(animationSpec = tween(5000)) using SizeTransform { initialSize, targetSize ->
                if (targetState) {
                    //指定关键帧，持续时间为3秒，在1秒之前宽度变大，高度不变，在1秒后高度开始变化
                    keyframes {
                        IntSize(
                            targetSize.width,
                            initialSize.height
                        ) at 1000
                        durationMillis = 3000
                    }
                } else {
                    keyframes {
                        IntSize(initialSize.width, targetSize.height) at 1000
                        durationMillis = 3000
                    }
                }

            }
        }) { targetState ->
            if (targetState) {
                TextUI()
            } else {
                IconUi()
            }
        }
    }
}

@Composable
fun IconUi() {
    Icon(painter = painterResource(id = R.mipmap.logo_nba), contentDescription = "")
}

@Composable
fun TextUI() {
    Text(
        text = "在Android上，一个应用程序使用的特定加固工具可以影响它是否可以被其他加固工具覆盖安装和使用。在这种情况下，要回答这个问题，需要考虑以下几个因素：\n" +
                "\n" +
                "1.360加固和爱加密加固工具如何工作-这些加固工具可能使用不同的技术和算法来保护应用程序，因此它们可能会互相冲突或不兼容。\n" +
                "\n" +
                "2.被加固的应用程序提供什么样的保护-如果一个应用程序使用了比较强的保护措施，例如反调试或代码混淆，则可能会使其更难以被其他加固工具正常覆盖安装和使用。\n" +
                "\n" +
                "3.操作系统和设备的版本-不同的操作系统和设备版本可能会处理应用程序的加固方式有所不同，而且有些版本可能会更容易让应用程序正常覆盖安装。\n" +
                "\n" +
                "基于上述因素，无法得出具体答案，如果有需要更详细的答案请提供更详细的信息，同时，也建议在安装和覆盖安装任何应用程序前备份重要数据，以防止数据丢失。",
        modifier = Modifier.fillMaxSize()
    )
}

/**
 * 如果值需要淡入淡出的效果那么可以使用Crossfade，它是animatedContent的一种特化，但是它不能感知内容size的变化
 */
@Composable
fun TestCrossfade() {
    var currentPage by remember { mutableStateOf("A") }
    Column {
        Button(onClick = {
            currentPage = when (currentPage) {
                "A" -> "B"
                "B" -> "A"
                else -> "A"
            }
        }) {
            Text(text = "切换")
        }
        //Crossfade只能设置animationSpec相关的属性，不能设置动画的实现效果，它内部固定实现动画的展示效果，交叉淡入淡出
        Crossfade(
            targetState = currentPage,
            animationSpec = tween(durationMillis = 3000, easing = FastOutLinearInEasing)
        ) {
            when (it) {
                "A" -> Text(text = "PageA")
                "B" -> Text(text = "PageB")
            }
        }
    }
}

/***------------------低级别动画------*/

/**
 * animate*AsState为常用数据类型都提供了方法，例如Float，Int，Dp，Size
 */
@Composable
fun AnimatedStateDemo() {
    var change by remember { mutableStateOf(false) }
    var flag by remember { mutableStateOf(false) }
    val buttonSize by animateDpAsState(targetValue = if (change) 32.dp else 24.dp)
    //通过这个形式设置颜色没有效果？？？
    /* val buttonColor by animateColorAsState(
         targetValue = if (flag) Color.Yellow else Color.Blue,
         animationSpec = spring(Spring.StiffnessHigh)
     )*/
    //对于无法直接估算值的计算数据类型，可以使用通用的Value，自行实现TwoWayConverter估值计算器
    /* val test by animateValueAsState(targetValue = 1, typeConverter = object :TwoWayConverter<Int,AnimationVector1D>{
         override val convertFromVector: (AnimationVector1D) -> Int
             get() =
         override val convertToVector: (Int) -> AnimationVector1D
             get() =

     })*/
    if (buttonSize == 32.dp) {
        change = false
    }
    Box(
        modifier = Modifier
            .fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = {
            change = true
            Log.e("TAG", "current flag :$flag")
            flag = !flag
        }) {
            Icon(
                Icons.Rounded.Favorite,
                contentDescription = "",
                modifier = Modifier.size(buttonSize),
                tint = if (flag) Color.Red else Color.Gray
            )
        }
    }
}

/**
 * 用Animatable来重写实现上面的Animated*State
 * 注意Animatable这玩意的很多方式都是挂起函数，所以需要协程环境
 * Animatable相比于Animated*State可以指定一个初始值，以及一个目标阀值，即达到这个值直接动画结束，有初始值的话可以通过修改这个值，来触发一次动画的执行
 */
@Composable
fun TestOverWriteAnimatedStateByAnimatable() {
    var change by remember { mutableStateOf(false) }
    var flag by remember { mutableStateOf(false) }
    val buttonSize = remember { Animatable(24.dp, Dp.VectorConverter) }
    val buttonColor = remember { Animatable(Color.Gray) }
    LaunchedEffect(key1 = flag, key2 = change) {
        buttonSize.animateTo(if (change) 32.dp else 24.dp)
        buttonColor.animateTo(if (flag) Color.Red else Color.Gray)
    }
    if (buttonSize.value == 32.dp) {
        change = false
    }
    Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
        IconButton(onClick = {
            change = true
            flag = !flag
        }) {
            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = "",
                modifier = Modifier.size(buttonSize.value),
                tint = buttonColor.value
            )
        }

    }
}


@Composable
fun TestAnimatableDemo() {
    var flag by remember { mutableStateOf(false) }
    var buttonColor = remember { Animatable(Color.Blue) }
    LaunchedEffect(key1 = flag, block = {
        buttonColor.animateTo(if (flag) Color.Gray else Color.Blue)
    })
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(
            onClick = { flag = !flag },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = buttonColor.value,
                contentColor = Color.White
            )

        ) {
            Text(text = "修改看看")
        }
    }
}

sealed class SelectStates {
    object OpenState : SelectStates()
    object CloseState : SelectStates()
}

/**
 * Transition可以让多个对象的动画同步的结束，类似与原生的AnimationSet，上面的Animated*State和Animatable都是作用在单个对象上的动画
 * 使用transition需要将所有的动画状态都列举出来
 */
@Composable
fun TestTranistionDemo() {
    var viewState: SelectStates by remember { mutableStateOf(SelectStates.CloseState) }
    val transition = updateTransition(targetState = viewState, label = "vewState")
    val selectBarTopPadding by transition.animateDp(
        label = "topPadding",
        transitionSpec = { tween(1000) }) {
        when (it) {
            SelectStates.OpenState -> {
                0.dp
            }
            SelectStates.CloseState -> {
                40.dp
            }
        }
    }
    val textAlpha by transition.animateFloat(
        label = "textAlpha",
        transitionSpec = { tween(1000) }) {
        when (it) {
            SelectStates.OpenState -> 0f
            SelectStates.CloseState -> 1f
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier
            .size(200.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                viewState = when (viewState) {
                    SelectStates.OpenState -> SelectStates.CloseState
                    SelectStates.CloseState -> SelectStates.OpenState
                }
            }) {
            Image(
                painter = painterResource(id = R.mipmap.easy_care),
                contentDescription = "",
                modifier = Modifier.size(width = 200.dp, height = 200.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = "选我", modifier = Modifier
                    .align(Alignment.Center)
                    .alpha(textAlpha),
                style = TextStyle(
                    color = Color.White,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                )
            )
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(top = selectBarTopPadding)
                    .background(Color.Gray)
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .wrapContentHeight(Alignment.CenterVertically)
                    .alpha(1 - textAlpha)
            ) {
                Icon(imageVector = Icons.Rounded.Star, contentDescription = "", tint = Color.White)
                Spacer(modifier = Modifier.width(width = 2.dp))
                Text(
                    text = "我已经选好了", style = TextStyle(
                        color = Color.White
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun TestFoAA() {
    var selected by remember { mutableStateOf(0) }
    val transition = updateTransition(targetState = selected, label = "selectState")
    val borderColor by transition.animateColor(label = "borderColor") { isSelect ->
        if (isSelect == 1) {
            Color.Magenta
        } else {
            Color.White
        }
    }
    val elevation by transition.animateDp(label = "elevation") { isSelect ->
        if (isSelect == 1) 10.dp else 2.dp
    }
    Surface(
        onClick = {
            //实现0和1的切换
            selected = 1 - selected
            Log.e("TAG", "current:$selected")
        }, shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, borderColor),
        elevation = elevation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .wrapContentHeight(Alignment.CenterVertically)
        ) {
            Text(text = "Hello world", color = Color.Black)
            //通过Transition的扩展方法来将AnimatedVisibility作为动画效果的一部分
            transition.AnimatedVisibility(
                //如果不是boolean值的话需要转化为boolean值
                visible = { targetSelected -> targetSelected == 1 },
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(text = "It is fine today.", color = Color.Black)
            }
            //通过Transition的扩展方法来将AnimatedContent作为动画效果的一部分
            transition.AnimatedContent { targetState ->
                if (targetState == 1) {
                    Text(text = "Seleted", color = Color.Black)
                } else {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

/**
 * 无限循环的动画，直到Composable生命周期结束
 */
@Composable
fun TestRememberInfiniteTransition() {
    val color = rememberInfiniteTransition()
    val stateColor = color.animateColor(
        initialValue = Color.Blue, targetValue = Color.Red, animationSpec = infiniteRepeatable(
            /**
             * animation的三种实现，keyframes 关键帧 tween 过度 snap等到时间直接展示结果没有过度
             */
            animation =
            //不知道为什么没效果
            //snap(5000)
            keyframes {
                durationMillis = 5000
                Color.Yellow at 1000
                Color.Gray at 2000
                Color.Green at 3000
                Color.Cyan at 4000
            },//tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(
        modifier = Modifier
            .size(200.dp)
            .background(color = stateColor.value)
    )

}

/**
 * 通过关键帧设置动画的执行效果，
 * 0.2f at 15 with FastOutLinerInEasing  就在在15毫秒的时达到0.2 以FastOutLinearInEasing的效果 也就是0-0.2 执行时间为15毫秒，效果为FastOutLinearInEasing
 */
@Composable
fun TestForKeyFrames() {
    val value by animateFloatAsState(targetValue = 1f, animationSpec = keyframes {
        durationMillis = 300
        0.0f at 0 with LinearOutSlowInEasing
        0.2f at 15 with FastOutLinearInEasing
        0.4f at 75
        0.4f at 255
    })
}

@Composable
fun TestRepeatable() {
    val infinteTransition = rememberInfiniteTransition()
    val degrees by infinteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 359f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1500
                0f at 0
                359f at 1500
            }
        )
    )
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "123123123", modifier = Modifier.rotate(degrees))
    }
}

data class MySize(val width: Dp, val height: Dp)

/**
 * 自定义TwoWayConverter,将任意类型的值专为矢量对象从而实现动画效果
 */
@Composable
fun MyAnimation(target: MySize) {
    var content by remember { mutableStateOf("刚开始") }
    val animSize: MySize by animateValueAsState<MySize, AnimationVector2D>(
        targetValue = target,
        typeConverter = TwoWayConverter(
            //将MySize转换为AnimationVector2D，因为这里需要转换的参数只有两个，所以转化为的矢量对象为2D，
            //1D-4D，也就是支持最多四个参数，之前的animateFloatAsState等，也是通过转换为矢量对象来实现的
            convertToVector = { size: MySize ->
                AnimationVector2D(size.width.value, size.height.value)
            },
            convertFromVector = { vector: AnimationVector2D ->
                MySize(vector.v1.dp, vector.v2.dp)
            }
        ),
        animationSpec = tween(1000, easing = LinearOutSlowInEasing), finishedListener = {
            content = "动画结束"
        }
    )
    Box(
        modifier = Modifier
            .width(animSize.width)
            .height(animSize.height)
            .background(color = Color.Blue),
        contentAlignment = Alignment.Center
    ) {
        Text(text = content, style = TextStyle(color = Color.White))
    }
}

/**
 * 使用animateValueAsState来处理自定义值到动画的解析
 */
@Composable
fun TestMyAnimationDemo() {
    var size by remember { mutableStateOf(MySize(100.dp, 100.dp)) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                size = MySize(200.dp, 200.dp)
            }, contentAlignment = Alignment.Center
    ) {
        MyAnimation(size)
    }
}


@Composable
fun Screen6() {
    TestRememberInfiniteTransition()
}


