package com.example.stcompose.chat.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.stcompose.chat.data.HomeDataRepository
import com.example.stcompose.chat.data.bean.BannerResponse
import com.example.stcompose.chat.data.bean.HomeListItemBean
import com.example.stcompose.chat.paging.HomeSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *    author : heyueyang
 *    time   : 2023/05/18
 *    desc   :
 *    例如首页现有的话就是获取列表上的数据，上拉加载，下拉刷新，点击跳转
 *    version: 1.0
 */
const val PAGE_SIZE = 20

sealed interface HomeUiState {
    val isLoading: Boolean
    val errorMessage: String

    data class NoData(
        override val isLoading: Boolean,
        override val errorMessage: String
    ) : HomeUiState

    data class HasData(
        val homeListData: Flow<PagingData<HomeListItemBean>>,
        val bannerResponse: List<BannerResponse>?,
        override val isLoading: Boolean,
        override val errorMessage: String
    ) : HomeUiState

}

private data class HomeViewModelState(
    val homeListData: Flow<PagingData<HomeListItemBean>>? = null,
    val bannerResponse: List<BannerResponse>? = null,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
) {
    fun toUiState(): HomeUiState {
        return if (homeListData == null) {
            HomeUiState.NoData(
                isLoading = isLoading,
                errorMessage = errorMessage
            )
        } else {
            HomeUiState.HasData(
                homeListData = homeListData,
                bannerResponse = bannerResponse,
                isLoading = isLoading,
                errorMessage = errorMessage
            )
        }
    }
}


@HiltViewModel
class HomeModel @Inject internal constructor(
    private val repository: HomeDataRepository
) : ViewModel() {
    private val viewModelState = MutableStateFlow(
        HomeViewModelState(
            isLoading = true, homeListData = Pager(
                //HomeSource()这里的数据源需要每次都创建一个信息，不能复用之前的对象，之前是在构造方法里通过注入的方式构建导致使用的是同一个对象
                //就会An instance of PagingSource was re-used when Pager expected to create a new
                //instance. Ensure that the pagingSourceFactory passed to Pager always returns a
                //new instance of PagingSource.
                PagingConfig(PAGE_SIZE), pagingSourceFactory = { HomeSource() }).flow.cachedIn(
                viewModelScope
            )
        )
    )

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            started = SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        getTopBanner()
    }

    fun getTopBanner(){
        viewModelScope.launch {
            val banner = repository.getTopBanner()
            viewModelState.update {
                it.copy(bannerResponse = banner)
            }
        }
    }

}