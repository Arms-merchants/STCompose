package com.example.stcompose.chat.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.stcompose.chat.data.bean.HomeListBean
import com.example.stcompose.chat.data.bean.HomeListItemBean
import rxhttp.RxHttp
import rxhttp.toAwaitResponse
import rxhttp.tryAwait


/**
 *    author : heyueyang
 *    time   : 2023/05/23
 *    desc   :
 *    version: 1.0
 */
class HomeSource : PagingSource<Int, HomeListItemBean>() {

    override fun getRefreshKey(state: PagingState<Int, HomeListItemBean>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HomeListItemBean> {
        return try {
            val page = params.key ?: 0
            val topListBean = RxHttp.get("/article/top/json").toAwaitResponse<List<HomeListItemBean>>()
                .tryAwait()
            val homeListBean = RxHttp.get("/article/list/${page}/json")
                .toAwaitResponse<HomeListBean?>()
                .await()
            if (page == 0) {
                topListBean?.let { top ->
                    homeListBean?.datas?.addAll(0, top)
                }
            }
            val prevKey = if (page > 1) page - 1 else null
            val nextKey = if (homeListBean?.datas?.isNotEmpty() == true) page + 1 else null
            LoadResult.Page(homeListBean?.datas ?: listOf(), prevKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}