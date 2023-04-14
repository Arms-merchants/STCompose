package com.example.stcompose

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stcompose.demo4.CounterViewModel
import kotlinx.coroutines.launch


/**
 *    author : heyueyang
 *    time   : 2023/03/14
 *    desc   :
 *    version: 1.0
 */
@Composable
fun CounterPage() {
    //remember只能够实现跨重组保存数据，但无法跨进程等场景来进行保存
    var count by remember {
        mutableStateOf(0)
    }
    //rememberSaveable可以处理像是因为屏幕旋转等引起的页面重建引起的数据保存，实现的就是之前在activity中onSaveInstanceState,其实也是通过这个方法来实现的
    //存储的值的key是Compose函数的唯一key，它内部使用的是Bundle，也就是Bundle支持的数据格式它就能去保存
    var test by rememberSaveable() {
        mutableStateOf("")
    }

    CountContent(count = count, increment = { count += 1 }) {
        count -= 1
    }
}

/**
 * 但是当有复杂的操作时，可以用ViewModel来隔离ui和数据，保证ui只负责界面的展示
 */
@Composable
fun CounterPageViewModel() {
    //viewModel是一个composable函数，需要添加对应的依赖
    val viewModel: CounterViewModel = viewModel()
    /* CountContent(count = viewModel.counter.value, increment = { viewModel.increment() }) {
         viewModel.decrement()
     }*/
    //将LiveData转为state
    //Flow.collectAsState
    val tempState = viewModel.tLiveData.observeAsState()
    CountContent(count = tempState.value ?: 0, increment = { viewModel.increment() }) {
        viewModel.decrement()
    }
}


/**
 * 这里将状态上提，将CountContent由Stateful 为Stateless,就是由方法内部的状态引起变化，到
 * 方法的重组是有方法如参引起的。
 * 当状态的变化涉及多个Composable时，我们可以将状态上升到它们最小Space中
 */
@Composable
fun CountContent(count: Int, increment: () -> Unit, decrement: () -> Unit) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        Text(
            text = "Click the buttons to adjust your value:"
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "$count", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Row() {
            Button(
                onClick = { increment() }, modifier = Modifier
                    .width(100.dp)
                    .height(48.dp)
            ) {
                Text(text = "+", color = Color.White)
            }
            Spacer(modifier = Modifier.width(20.dp))
            Button(
                onClick = { decrement() }, modifier = Modifier
                    .width(100.dp)
                    .height(48.dp)
            ) {
                Text(text = "-", color = Color.White)
            }
        }
    }
}


data class City(val name: String, val address: String)

//自定义Saver
object CitySaver : Saver<City, Bundle> {
    override fun restore(value: Bundle): City? {
        return value.getString("name")?.let { name ->
            value.getString("address")?.let { address ->
                City(name, address)
            }
        }
    }

    override fun SaverScope.save(value: City): Bundle {
        return Bundle().apply {
            putString("name", value.name)
            putString("address", value.address)
        }
    }
}

//通过mapSaver的形式实现Saver,还有个listSaver专为list
val CitySaver2 = run {
    val nameKey = "name"
    val addreseKey = "address"
    mapSaver(
        save = { mapOf(nameKey to it.name, addreseKey to it.address) },
        restore = { City(it[nameKey] as String, it[addreseKey] as String) }
    )
}

@Composable
fun RememberDemo() {
    //自定义Saver来保存哪些不支持添加Parcelable接口的类，例如三方库内的数据对象
    var selectedCity by rememberSaveable(stateSaver = CitySaver) {
        mutableStateOf(City("1", "2"))
    }
}

@Composable
fun SnackbarDemo() {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(scaffoldState = scaffoldState) {
        MyContent(modifier = Modifier.padding(it), showSnackbar = { message ->
            coroutineScope.launch {
                scaffoldState.snackbarHostState.showSnackbar(message = message)
            }
        })
    }
    LaunchedEffect(1) {
        launch {
            scaffoldState.snackbarHostState.showSnackbar(message = "试试意思")
        }
    }
}

@Composable
fun MyContent(modifier: Modifier = Modifier, showSnackbar: (message: String) -> Unit) {

    Button(onClick = { showSnackbar("我就是想试试") }) {
        Text(text = "showSnackbar")
    }
}

/**
 * StateHolder一般用于管理UI相关的状态和逻辑，ViewModel用来管理与UI无关的状态和逻辑
 */
class ExampleState(
    val lazyListState: LazyListState,
    private val resources: Resources, private val expandedItems: List<Item> = emptyList()
) {

}

@Composable
fun rememberExampleState(
    lazyListState: LazyListState = rememberLazyListState(),
    resources: Resources = LocalContext.current.resources,
    expandedItems: List<Item> = emptyList()
) {
    remember(lazyListState, resources, expandedItems) {
        ExampleState(lazyListState, resources, expandedItems)
    }
}

@Composable
fun test(viewModel: CounterViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val count = viewModel.counter
    val exampleState = rememberExampleState()

}


/**
 * 重组的范围
 */
@Composable
fun TestRange() {
    Log.e("TAG", "scope 1")
    var count by remember {
        mutableStateOf(0)
    }
    Column {
        Log.e("TAG", "scope 2")
        Button(onClick = run {
            Log.e("TAG", "onClick")
            return@run { count++ }
        }) {
            Log.e("TAG", "scope 3")
            Text(text = "测试")
        }
        Text(text = "$count")
    }
    val context = LocalContext.current
    backPressHandler() {
        Toast.makeText(context, "返回键操作", Toast.LENGTH_LONG).show()
    }
}

@Composable
fun MoviesScreen(items: List<Item>) {
    Column {
        for (item in items) {
            //在for循环中的Composable函数无法直接在编译期来确定key，只能在运行时以index为key，这样的话如果在头部插入数据
            //就会导致整个列表都会重组，而实际上我们需要的仅仅时插入的数据进行重组。
            //这个时候就需要使用到key函数，它能够手动指定一个值作为key，例如item的唯一id，那么比较的时候就会以id为基准去进行比较判断是否发生变化从而进行
            //重组
            key(item.name) {
                MoviesListItem(item = item)
            }
        }
    }
}

@Composable
fun MoviesListItem(item: Item) {
    Text(text = item.name)
}

/**
 * 如果是和UI无关的Composable方法命名安普通函数命名即可
 */
@Composable
fun backPressHandler(enabled: Boolean = true, onBackPressed: () -> Unit) {
    val backDispatcher = checkNotNull(LocalOnBackPressedDispatcherOwner.current) {
        "为空的话抛出的消息"
    }.onBackPressedDispatcher

    val backPressedCallback = remember {
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
    }
    DisposableEffect(backDispatcher) {
        backDispatcher.addCallback(backPressedCallback)
        onDispose {
            backPressedCallback.remove()
        }
    }
}

/**  -----副作用的Api----  */
@Composable
fun TestEffect() {
    SideEffect {
        //在SideEffect中的代码只有重组成功时才会执行
    }
    //如果在Composable中需要异步的操作，那使用LaunchedEffect，它包含一个key，如果key发生变化会取消内部的协程后再重新开启一个新的，而当Compsable方法的生命
    //周期结束时，它内部的协程也会取消，所以不需要处理onDispose
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(scaffoldState.snackbarHostState) {
        //这里可以开启子协程，或调用挂起函数
        scaffoldState.snackbarHostState.showSnackbar(
            message = ""
        )
    }
    //Scaffold脚手架，能够去添加topbar以及bottombar（添加BottomNavigation等）
    Scaffold(scaffoldState = scaffoldState,
        bottomBar = {},
        topBar = {}
    ) {
        //Scaffold需要content的外层布局的padding使用这个it，不然会出现content的内容在bottomBar的下方，如果不使用的话编辑器会有提示
        //可以使用注解去除该提示，不过内容高度的问题需要自己去处理
        Text(text = "", modifier = Modifier.padding(it))
    }

    val scopeRemember = rememberCoroutineScope()
    Button(onClick = {
        //showSnackbar是一个挂起函数，onClick内只是一个普通函数，那么这里就需要使用rememberCoroutineScope，rememberCoroutineScope内的的CoroutineScope
        //会跟随它所在的Composable的生命周期
        scopeRemember.launch {
            scaffoldState.snackbarHostState.showSnackbar("")
        }
    }) {
        Text(text = "123")
    }
    val value = rememberUpdatedState(newValue = scopeRemember)
    //在LaunchedEffect用Unit作为key，那么在Compsable函数重组的时候，不会中断协程的执行，但为了让内容获取到实时的协程中的状态
    //就可以使用rememberUpdatedState，它实际是由remember和mutableState组合实现，remember保证了内部的state能够跨重组
    LaunchedEffect(Unit) {
        value.value.launch {

        }
    }
}


@Composable
fun TestProduceState() {
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val onStop = {}
    val onStart = {}
    val currentStop = rememberUpdatedState(newValue = onStop)
    val currentStart = rememberUpdatedState(onStart)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            currentStart.value.invoke()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}










