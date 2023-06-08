package com.example.stcompose.chat.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.stcompose.chat.data.bean.HomeListBean
import com.example.stcompose.chat.data.bean.HomeListItemBean
import rxhttp.RxHttp
import rxhttp.toAwaitResponse

/**
 *    author : heyueyang
 *    time   : 2023/05/24
 *    desc   :
 *    version: 1.0
 */
class ProjectSource(private val id: Int) : PagingSource<Int, HomeListItemBean>() {

    override fun getRefreshKey(state: PagingState<Int, HomeListItemBean>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HomeListItemBean> {
        return try {
            val page = params.key ?: 0
            val listBean = RxHttp.get("/project/list/${page}/json?cid=${id}")
                .toAwaitResponse<HomeListBean?>()
                .await()
            val prevKey = if (page > 1) page - 1 else null
            val nextKey = if (listBean?.datas?.isNotEmpty() == true) page + 1 else null
            LoadResult.Page(listBean?.datas ?: listOf(), prevKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}