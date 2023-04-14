package com.example.stcompose.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


/**
 *    author : heyueyang
 *    time   : 2023/04/11
 *    desc   :
 *    version: 1.0
 */

class TViewModel : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    private val colorPanel = listOf(
        Color.Gray,
        Color.Green,
        Color.Red,
        Color.Blue,
        Color.Black,
        Color.Magenta,
        Color.DarkGray
    )

    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

    var background by mutableStateOf(Color.Gray)

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            delay(1000)
            background = colorPanel.random()
            _isRefreshing.emit(false)
        }
    }


}