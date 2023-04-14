package com.example.stcompose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.stcompose.ui.theme.*

/**
 *    author : heyueyang
 *    time   : 2023/03/13
 *    desc   :
 *    version: 1.0
 */
@Composable
fun HomePage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .scrollable(rememberScrollState(), orientation = Orientation.Vertical)
            .padding(horizontal = 16.dp)
    ) {
        HomeTopSearch()
        HomeHList()
        HomeBottomList()
    }
}

@Composable
fun HomeTopSearch() {
    BasicTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier
            .padding(top = 40.dp)
            .fillMaxWidth()
            .height(56.dp)
    ) { innerTextField ->
        Row(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .border(0.5.dp, color = gray, shape = small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            ) {
                Text(text = "Search", style = body1, color = gray)
                innerTextField()
            }
        }
    }
    /*OutlinedTextField(value = "", onValueChange = {}, placeholder = {
        Text(text = "Search", style = body1, color = gray)
    }, leadingIcon = {
        Icon(
            painter = rememberVectorPainter(image = Icons.Filled.Search),
            contentDescription = "Search", modifier = Modifier.size(18.dp)
        )
    }, shape = small, modifier = Modifier
        .padding(top = 40.dp)
        .fillMaxWidth()
        .height(56.dp), colors = TextFieldDefaults.outlinedTextFieldColors(
        backgroundColor = white,
        unfocusedBorderColor = Color.Black,
        focusedBorderColor = Color.Black
    )
    )*/
}

@Composable
fun HomeHList() {
    val items = arrayListOf(
        Item("Dsert chic", R.mipmap.desert_chic),
        Item("Tiny terrariums", R.mipmap.tiny_terrariums),
        Item("Jungle vibes", R.mipmap.jungle_vibes),
        Item("Easy care", R.mipmap.easy_care),
        Item("Statements", R.mipmap.statements)
    )
    Column {
        HomeTitle(name = "Browse themes", height = 32.dp)
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(content = {
            items(items.size) {
                if (it != 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
                HomeHItem(item = items[it])
            }
        })
    }
}

@Composable
fun HomeHItem(item: Item) {
    Card(
        modifier = Modifier
            .size(136.dp)
            .clip(small)
            .border(width = 1.dp, color = Color.Gray, shape = small)
    ) {
        Column {
            Image(
                painter = painterResource(id = item.icon), contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = item.name,
                    style = h2,
                    color = gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .paddingFromBaseline(top = 24.dp, bottom = 16.dp),
                )
            }
        }
    }
}

@Composable
fun HomeBottomList() {
    val list = arrayListOf<Item>(
        Item("Monstera", R.mipmap.monstera, "This is a description"),
        Item("Aglaonema", R.mipmap.aglaonema, "This is a description"),
        Item("Peace lily", R.mipmap.peace_lily, "This is a description"),
        Item("Fiddle leaf tree", R.mipmap.fiddle_leaf, "This is a description"),
        Item("Snake plant", R.mipmap.snake_plant, "This is a description"),
        Item("Pothos", R.mipmap.pothos, "This is a description")
    )
    Column {
        HomeTitle(name = "Design your home garden", height = 40.dp)
        LazyColumn(content = {
            items(list.size) {
                if (it != 0) {
                    Spacer(modifier = Modifier.height(5.dp))
                }
                HomeVItem(item = list[it])
            }
        })
    }
}

@Composable
fun HomeVItem2(item: Item) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = item.icon),
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(small)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = item.name,
                        style = h2,
                        color = gray,
                        modifier = Modifier.paddingFromBaseline(top = 24.dp)
                    )
                    item.subTitle?.let { Text(text = it, style = body1, color = gray) }
                }
                Checkbox(
                    checked = false,
                    onCheckedChange = {},
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .size(24.dp),
                    colors = CheckboxDefaults.colors(
                        checkmarkColor = white
                    )
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Divider(modifier = Modifier, color = gray, thickness = 0.5.dp)
        }
    }
}

@Composable
fun HomeVItem(item: Item) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        val (image, topText, bottomText, checkBox, line) = createRefs()
        Image(
            painter = painterResource(id = item.icon),
            contentDescription = item.name,
            modifier = Modifier
                .size(64.dp)
                .constrainAs(image) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                },
            contentScale = ContentScale.Crop
        )
        Text(
            text = item.name,
            style = h2,
            color = gray,
            modifier = Modifier
                .paddingFromBaseline(top = 24.dp)
                .constrainAs(topText) {
                    start.linkTo(image.end, 16.dp)
                }
        )
        item.subTitle?.let {
            Text(
                text = it,
                style = body1,
                color = gray,
                modifier = Modifier.constrainAs(bottomText) {
                    start.linkTo(topText.start)
                    top.linkTo(topText.bottom)
                }
            )
        }
        Checkbox(checked = false, onCheckedChange = {}, modifier = Modifier
            .constrainAs(checkBox) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }
        )
        Box(modifier = Modifier
            .constrainAs(line) {
                start.linkTo(topText.start)
                end.linkTo(checkBox.end)
                bottom.linkTo(parent.bottom)
            }
            .height(0.5.dp)
            .background(color = gray))
    }
}


@Composable
fun HomeTitle(name: String, height: Dp) {
    Box(
        contentAlignment = Alignment.BottomStart,
        modifier = Modifier
            .fillMaxWidth()
            .paddingFromBaseline(top = height)
    ) {
        Text(
            text = name,
            style = h1, color = gray,
        )
    }
}
