package com.example.stcompose.demo4

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 *    author : heyueyang
 *    time   : 2023/03/15
 *    desc   :
 *    version: 1.0
 */
class CounterViewModel : ViewModel() {
    private val _counter = mutableStateOf(0)
    val counter: State<Int> = _counter

    private val _tLiveData = MutableLiveData<Int>(0)
    val tLiveData: LiveData<Int> = _tLiveData
    fun increment() {
        // _counter.value = _counter.value + 1
        _tLiveData.value = _tLiveData.value?.plus(1)
    }

    fun decrement() {
        /*if (_counter.value > 1) {
            _counter.value = _counter.value - 1
        }*/
        if (_tLiveData.value!! > 1) {
            _tLiveData.value = _tLiveData.value!! - 1
        }
    }
}