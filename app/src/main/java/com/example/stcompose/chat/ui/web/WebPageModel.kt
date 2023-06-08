package com.example.stcompose.chat.ui.web

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 *    author : heyueyang
 *    time   : 2023/05/26
 *    desc   :
 *    version: 1.0
 */
@HiltViewModel
class WebPageModel @Inject constructor() : ViewModel() {
    var viewStates by mutableStateOf(WebState())
        private set

    fun dispatch(action: WebAction) {
        when (action) {
            is WebAction.WebBean -> {
                viewStates = viewStates.copy(
                    title = action.title,
                    url = action.url,
                    isShowPopup = action.isShowPopup
                )
            }

            is WebAction.ChangePopup -> {
                viewStates = viewStates.copy(isShowPopup = !viewStates.isShowPopup)
            }
        }
    }
}

sealed class WebAction {
    object ChangePopup : WebAction()
    data class WebBean(
        val title: String,
        val url: String,
        val isShowPopup: Boolean = false
    ) : WebAction()
}

data class WebState(
    val title: String = "",
    val url: String = "",
    val isShowPopup: Boolean = false
)
