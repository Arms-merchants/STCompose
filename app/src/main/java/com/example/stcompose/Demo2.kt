package com.example.stcompose


import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

/**
 *    author : heyueyang
 *    time   : 2023/03/09
 *    desc   : 第二章节常用UI
 *    version: 1.0
 */

@Composable
fun TextFieldDemo() {
    //来至键盘输入的文本并不能直接更新TextFiled，TextFiled是通过观察额外的状态更新自身，状态驱动UI
    var text by remember { mutableStateOf("") }
    TextField(value = text, onValueChange = {
        //这里获取键盘输入的内容，来更新text这个可变状态，从而引发界面刷新
        text = it
    }, label = { Text(text = "用户名") })
}

@Composable
fun TextFieldDemo2() {
    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("") }
    Column {
        TextField(value = userName, onValueChange = {
            userName = it
        }, label = { Text(text = "用户名") }, leadingIcon = {
            //虽说叫icon，但实际能够接受所有的Composable，例如下面的Text
            Icon(imageVector = Icons.Filled.AccountBox, contentDescription = "测试")
            //Text(text = "123")
        })
        TextField(value = password,
            onValueChange = {
                password = it
            },
            label = { Text(text = "密码") },
            trailingIcon = {
                IconButton(onClick = { password = "" }) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "测试2")
                }
            })
        OutlinedTextField(
            //TextField 和OutlinedTextField 直接修改高度会出现输入区域被截断，如果要修改高度的话需要使用BasicTextField
            value = sex, onValueChange = {
                sex = it
            },
            label = { Text(text = "性别") },
            maxLines = 1,
            singleLine = true
        )
    }
}

@Composable
fun BasicTextFileDemo() {
    var text by remember { mutableStateOf("") }
    BasicTextField(modifier = Modifier
        .padding(top = 10.dp)
        .height(20.dp), value = text, onValueChange = {
        text = it
    }, decorationBox = { innerTextField ->
        Column {
            Divider(
                thickness = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Blue)
            )
            innerTextField()
        }
    })
}

/**
 * 实现一个带有提示和删除操作的文本框
 */
@Composable
fun SearchBarDemo() {
    var text by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFD3D3D3)),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
            },
            //
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(vertical = 2.dp, horizontal = 8.dp)
                ) {
                    //Icon中对应的资源，imageVector  矢量图
                    //bitmap  就是bitmap的资源   jpg和png
                    //painter 对上面的两种都支持
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "搜索",
                        tint = Color.Red
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (text.isEmpty()) {
                            Text(text = "请输入", style = TextStyle(color = Color(0, 0, 0, 128)))
                        }
                        innerTextField()
                    }
                    if (text.isNotEmpty()) {
                        IconButton(onClick = { text = "" }, modifier = Modifier.size(16.dp)) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "删除")
                        }
                    }
                }
            },
            modifier = Modifier
                .padding(10.dp)
                .background(color = Color.White, CircleShape)
                .height(30.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun ImageDemo() {
    Image(
        painter = painterResource(id = R.mipmap.ic_hotel_share_img), contentDescription = "",
        contentScale = ContentScale.Inside,
        colorFilter = ColorFilter.tint(Color.Blue, BlendMode.Difference)
    )
}

@Composable
fun ButtonDemo() {
    val interactionSource = remember { MutableInteractionSource() }
    val pressState = interactionSource.collectIsPressedAsState()
    val border =
        if (pressState.value) BorderStroke(2.dp, Color.Gray) else BorderStroke(2.dp, Color.Yellow)
    Button(
        onClick = { },
        border = border,
        interactionSource = interactionSource,
        //设置圆角
        shape = RoundedCornerShape(10.dp),
        //修改按钮的背景色
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Blue,
            contentColor = Color.White
        ),
    ) {
        Icon(imageVector = Icons.Outlined.ExitToApp, contentDescription = "")
        Text(text = "确定")
    }
}

@Composable
fun CheckBoxDemo() {
    var checkState by remember {
        mutableStateOf(true)
    }
    Checkbox(
        checked = checkState, onCheckedChange = {
            checkState = it
        },
        colors = CheckboxDefaults.colors(checkedColor = Color.Blue, uncheckedColor = Color.Yellow)
    )
}

@Composable
fun TriStateCheckBoxDemo() {
    val (state, onStateChange) = remember {
        mutableStateOf(true)
    }
    val (state2, onStateChange2) = remember {
        mutableStateOf(true)
    }

    val parentState = remember(state, state2) {
        if (state && state2) ToggleableState.On
        else if (!state && !state2) ToggleableState.Off
        else ToggleableState.Indeterminate
    }

    val onParentClick = {
        val s = parentState != ToggleableState.On
        onStateChange(s)
        onStateChange2(s)
    }

    Column {
        TriStateCheckbox(
            state = parentState, onClick = onParentClick,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colors.primary
            )
        )
        Column(Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp)) {
            Checkbox(checked = state, onCheckedChange = onStateChange)
            Checkbox(checked = state2, onCheckedChange = onStateChange2)
        }
    }
}

@Composable
fun SliderDemo() {
    //这玩意类似原来的seekbar，就能能推送的进度条
    var sliderPosition by remember {
        mutableStateOf(0f)
    }
    Column {
        Text(text = "%.1f".format(sliderPosition * 100) + "%")
        Slider(
            value = sliderPosition, onValueChange = {
                sliderPosition = it
            },
            //相当于加几个分界点，并且在每一段的过程内，到达一半那么直接到阶段的末端，否则回调阶段起始
            steps = 3
        )
    }
}

@Composable
fun DialogDemo() {
    val openDialog = remember {
        mutableStateOf(false)
    }
    Button(onClick = { openDialog.value = true }) {
        Text(text = "dialog")
    }
    if (openDialog.value) {
        Dialog(onDismissRequest = { openDialog.value = false }) {
            Surface(
                modifier = Modifier
                    .width(200.dp)
                    .height(100.dp)
            ) {
                Text(text = "就是个弹窗")
            }
        }
    }
}

@Composable
fun AlertDialogDemo() {
    var isShow by remember {
        mutableStateOf(false)
    }
    Button(onClick = { isShow = true }) {
        Text(text = "展示弹窗")
    }
    if (isShow) {
        AlertDialog(onDismissRequest = { isShow = false }, title = {
            Text(text = "这是标题")
        }, text = {
            Text(text = "弹窗内容")
        }, confirmButton = {
            TextButton(onClick = { isShow = false }) {
                Text(text = "确认")
            }
        }, dismissButton = {
            TextButton(onClick = { isShow = false }) {
                Text(text = "取消")
            }
        })
    }
}

@Composable
fun ProgressDemo() {
    var progress by remember {
        mutableStateOf(0.1f)
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        //这玩意就是影响进度变化时的效果
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )

    Column {
        //圆环进度指示器
        CircularProgressIndicator(progress = animatedProgress)
        Spacer(modifier = Modifier.requiredHeight(30.dp))
        //直线进度指示器
        LinearProgressIndicator(progress = animatedProgress)
        Spacer(modifier = Modifier.requiredHeight(30.dp))
        OutlinedButton(onClick = {
            if (progress < 1f) progress += 0.1f
        }) {
            Text(text = "加一加")
        }
        Spacer(modifier = Modifier.requiredHeight(30.dp))
        AsyncImage(
            model = "https://img.tukuppt.com/photo-big/00/00/94/6152bc0ce6e5d805.jpg",
            contentDescription = null
        )
    }
}

/**
 * 约束布局的demo
 */
@Composable
fun ConstraintLayoutDemo() {
    ConstraintLayout(
        modifier = Modifier
            .width(300.dp)
            .fillMaxHeight()
            .background(color = Color.Yellow)
            .padding(10.dp)
    ) {
        //创建约束，类似于原来的id
        val (portraitImageRef, portraitImageRef2) = remember {
            createRefs()
        }
        AsyncImage(
            model = R.mipmap.ic_hotel_share_img,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .constrainAs(portraitImageRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                })
        Image(
            painter = painterResource(id = R.mipmap.ic_hotel_share_img),
            contentDescription = null,
            modifier = Modifier
                .background(color = Color.Blue)
                .constrainAs(portraitImageRef2) {
                    top.linkTo(portraitImageRef.bottom, margin = 10.dp)
                    start.linkTo(parent.start)
                    width = Dimension.preferredWrapContent
                }
        )
    }
}

/**
 * 约束布局中分界线的demo，还有一个GuideLine引导线
 */
@Composable
fun BarrierDemo() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (userNameRef, passwrodRef, userNameValueRef, passwordValueRef) = remember {
            createRefs()
        }
        var userName by remember {
            mutableStateOf("")
        }
        var password by remember {
            mutableStateOf("")
        }
        //一个引导线，从上到下，按2比8的比例平分，这个引导线在屏幕2的位置上
        val guideLine = createGuidelineFromTop(0.2f)
        val barrier = createEndBarrier(userNameRef, passwrodRef)
        Text(text = "UserName", modifier = Modifier.constrainAs(userNameRef) {
            top.linkTo(parent.top, 90.dp)
            start.linkTo(parent.start)
        })
        Text(text = "密码", modifier = Modifier.constrainAs(passwrodRef) {
            top.linkTo(userNameRef.bottom, 10.dp)
            start.linkTo(userNameRef.start)
        })
        TextField(value = userName, onValueChange = {
            userName = it
        }, modifier = Modifier.constrainAs(userNameValueRef) {
            top.linkTo(userNameRef.top)
            start.linkTo(barrier, 10.dp)
            end.linkTo(parent.end)
            height = Dimension.fillToConstraints
        })
        BasicTextField(value = password, onValueChange = {
            password = it
        }, modifier = Modifier
            .constrainAs(passwordValueRef) {
                top.linkTo(userNameValueRef.bottom, 10.dp)
                start.linkTo(barrier, 10.dp)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
            .background(color = Color.Gray, shape = RoundedCornerShape(5.dp))
            .height(30.dp))
    }
}

/**
 * 链接约束
 */
@Composable
fun ChainDemo() {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (t1, t2, t3, t4) = remember {
            createRefs()
        }
        //参与约束的元素，已经以什么规则来进行空间分配
        createVerticalChain(t1, t2, t3, t4, chainStyle = ChainStyle.SpreadInside)
        Text(text = "t1", modifier = Modifier.constrainAs(t1) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })
        Text(text = "t2", modifier = Modifier.constrainAs(t2) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })
        Text(text = "t3", modifier = Modifier.constrainAs(t3) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })
        Text(text = "t4", modifier = Modifier.constrainAs(t4) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })
    }
}

data class Item(val name: String, val icon: Int, val subTitle: String? = null)

/**
 * 脚手架demo
 */
@Composable
fun ScaffoldDemo() {
    var selectedItem by remember {
        mutableStateOf(0)
    }
    val items = listOf(
        Item("主页", R.mipmap.tab_home_hover),
        Item("列表", R.mipmap.tab_message_hover),
        Item("设置", R.mipmap.tab_mine_hover)
    )
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = { Text(text = "Drawer content") },
        topBar = {
            TopAppBar(
                title = { Text("主页") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            //添加协程的原因是drawerState的操作方法都是挂起函数
                            scope.launch { scaffoldState.drawerState.open() }
                        }
                    ) {
                        Icon(Icons.Filled.Menu, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigation(backgroundColor = Color.DarkGray) {
                items.forEachIndexed { index, item ->
                    BottomNavigationItem(
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        icon = {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = null
                            )
                        },
                        alwaysShowLabel = false,
                        label = { Text(item.name) },
                        selectedContentColor = Color.Blue,
                        unselectedContentColor = Color.Red,
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Inc") },
                onClick = { /* fab click handler */ }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "主页")
        }
    }
    //拦截返回按键判断是否有展开侧边框
    BackHandler(enabled = scaffoldState.drawerState.isOpen) {
        scope.launch {
            scaffoldState.drawerState.close()
        }
    }
}












