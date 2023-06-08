package com.example.stcompose.chat.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.stcompose.chat.data.ProjectRepository
import com.example.stcompose.chat.data.bean.HomeListItemBean
import com.example.stcompose.chat.data.bean.ProjectTabItemBean
import com.example.stcompose.chat.paging.ProjectSource
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
 *    time   : 2023/05/23
 *    desc   :
 *    version: 1.0
 */
sealed interface ProjectUiState {
    val isLoading: Boolean
    val errorMessage: String

    data class NoData(
        override val isLoading: Boolean,
        override val errorMessage: String
    ) : ProjectUiState

    data class HasData(
        val projectTabItemBean: List<ProjectTabItemBean>,
        override val isLoading: Boolean,
        override val errorMessage: String
    ) : ProjectUiState

}

private data class ProjectViewModelState(
    val projectTabItemBean: List<ProjectTabItemBean>? = null,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
) {
    fun toUiState(): ProjectUiState {
        return if (projectTabItemBean.isNullOrEmpty()) {
            ProjectUiState.NoData(
                isLoading, errorMessage
            )
        } else {
            ProjectUiState.HasData(
                projectTabItemBean,
                isLoading,
                errorMessage
            )
        }
    }
}

@HiltViewModel
class ProjectModel @Inject internal constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val viewModelState = MutableStateFlow(
        ProjectViewModelState(
            isLoading = true
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
        getTabData()
    }

    private fun getTabData() {
        Log.e("TAG","getTabData")
        viewModelScope.launch {
            Log.e("TAG","launch")
            val tabData = projectRepository.getTabList()
            viewModelState.update {
                Log.e("TAG","update")
                it.copy(projectTabItemBean = tabData)
            }
        }
    }

    fun getProjectListData(id: Int): Flow<PagingData<HomeListItemBean>> {
        return Pager(
            PagingConfig(PAGE_SIZE), pagingSourceFactory = { ProjectSource(id) }
        ).flow.cachedIn(viewModelScope)
    }


}