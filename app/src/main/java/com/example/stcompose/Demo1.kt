package com.example.stcompose

import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.stcompose.ui.theme.StComposeTheme

/**
 *    author : heyueyang
 *    time   : 2023/03/13
 *    desc   :
 *    version: 1.0
 */
/**
 * 普通的Text是不能复制的，SelectionContainer可以实现文本可复制的操作
 */
@Composable
fun SelectionContainerDemo() {
    SelectionContainer {
        Text(text = "复制试试")
    }
}

@Preview
@Composable
fun AnnotatedStringDemo() {
    val annotatedString = buildAnnotatedString {
        //SpanStyle控制字体样式
        withStyle(style = SpanStyle(fontSize = 24.sp)) {
            append("你现在学习的章节是")
        }
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.W900, fontSize = 24.sp
            )
        ) {
            append("Text")
        }
        append("\n")
        //ParagraphStyle控制段落样式，并且和SpanStyle的属性优先级是高于TextStyle中的同名属性设置
        withStyle(style = ParagraphStyle(lineHeight = 25.sp)) {
            append("12312312312312312312312312312312312")
            append("\n")
            append("我们现在学习的是")
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.W900,
                    textDecoration = TextDecoration.LineThrough,
                    color = Color(0xFF59A869)
                )
            ) {
                append("AnnotatedString")
            }
        }
        append("\n")
        //配合ClickableText来实现文本的可点击，这里相当于给给pushStringAnnotation到pop中所有添加的文本添加一个tag未URL的标注，标注内容为一个链接地址或其他
        //那么在ClickableText的点击事件里就能获取到对应的内容
        pushStringAnnotation(tag = "URL", annotation = "https://www.baiud.com")
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.W900,
                textDecoration = TextDecoration.Underline,
                color = Color.Red
            )
        ) {
            append("AnnotatedString")
        }
        append("\n")
        append("试一试")
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.W800,
                textDecoration = TextDecoration.LineThrough,
                color = Color.Yellow
            )
        ) {
            append("other Annotated String")
        }
        pop()
    }
    //普通样式展示
    Column {
        Text(
            //buildAnnotatedString感觉就是和SpannerString类似的效果
            text = annotatedString
        )
        Spacer(Modifier.height(10.dp))
        ClickableText(text = annotatedString, onClick = { offset ->
            Log.e("TAG", offset.toString())
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    Log.e("TAG", annotation.item)
                }
        })
    }
}

/**
 * 通过AndroidView来接入AndroidView体系的view
 */
@Composable
fun ShowWeb() {
    AndroidView(factory = { context ->
        WebView(context).apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            loadUrl("https://www.baidu.com")
        }
    }, modifier = Modifier.fillMaxSize())
}


@Composable
fun Greeting(modifier: Modifier = Modifier, name: String) {
    Box(
        modifier = modifier
            .fillMaxSize()
            //compose中只有padding的概念，根据调用的顺序可以实现原来的外边距和内边距的效果
            .padding(8.dp)
            .border(2.dp, Color.Yellow, shape = RoundedCornerShape(2.dp))
            .background(color = Color.Red)
            .padding(8.dp)

    ) {
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(color = Color.Green)
        )
    }
    Column {
        Text(text = "Hello $name!")
        Spacer(modifier = Modifier.height(10.dp))
        Image(
            painter = painterResource(id = R.mipmap.ic_hotel_share_img),
            contentDescription = null,
            modifier = Modifier
                .size(width = 200.dp, height = 200.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(color = Color.Red)
        ) {
            Text(text = "纯色", Modifier.align(Alignment.Center))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(
                    //背景的渐变效果
                    brush = verticalGradientBrush2
                )
        ) {
            Text(text = "渐变色", Modifier.align(Alignment.Center))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .size(100.dp)
                //设置偏移量
                //.offset(50.dp, 50.dp)
                //offset的重载方法
                .offset { IntOffset(50.dp.roundToPx(), 50.dp.roundToPx()) }
                .background(color = Color.Yellow)
        )
    }
}

//两种创建渐变色的方式
val verticalGradientBrush1 = Brush.verticalGradient(
    colors = listOf(Color.Red, Color.Yellow, Color.White)
)

val verticalGradientBrush2 = Brush.verticalGradient(
    0.1f to Color.Red,
    0.3f to Color.Green,
    0.9f to Color.Blue
)

@Composable
fun MatchParentSizeDemo() {
    Column {
        Box() {
            Box(
                Modifier
                    //matchParentSize是根据父布局展示的最大尺寸展示，外层box的尺寸是有UserInfo来确定的
                    .matchParentSize()
                    //fillMaxSize是父布局最大允许的尺寸，在这个例子中那就是铺满屏幕
                    //.fillMaxSize()
                    .background(color = Color.Yellow)
            ) {
            }
            UserInfo()
        }
    }
}

@Composable
fun UserInfo() {
    Row {
        Image(
            painter = painterResource(id = R.mipmap.ic_hotel_share_img),
            contentDescription = null,
            modifier = Modifier.size(150.dp)
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "名字")
            Text(text = "描述")
        }
    }
}

@Composable
fun WeightDemo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(color = Color.Red)
        ) {

        }
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(color = Color.Yellow)
        ) {

        }
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(color = Color.Blue)
        ) {

        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StComposeTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Greeting(Modifier, "Android")
        }
    }
}