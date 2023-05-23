package com.example.stcompose.chat.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

/**
 *    author : heyueyang
 *    time   : 2023/05/10
 *    desc   :
 *    version: 1.0
 */

enum class HomeScreenTab(
    val icon: ImageVector
) {
    Conversation(
        icon = Icons.Filled.Favorite
    ),
    Friendship(
        icon = Icons.Filled.Album
    ),
    Person(
        icon = Icons.Filled.WbSunny
    )

}

data class HomeScreenTopBarState(
    val screenSelected: HomeScreenTab,
    val openDrawer: () -> Unit,
    val onAddFriend: () -> Unit,
    val onJoinGroup: () -> Unit
)