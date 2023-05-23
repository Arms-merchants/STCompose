package com.example.stcompose.chat.data

import com.example.stcompose.chat.data.bean.BannerResponse
import com.example.stcompose.chat.data.bean.HomeListBean
import rxhttp.RxHttp
import rxhttp.toAwaitResponse
import javax.inject.Inject

/**
 *    author : heyueyang
 *    time   : 2023/05/18
 *    desc   :
 *    version: 1.0
 */
class HomeDataRepository @Inject constructor() {

    suspend fun test2(): HomeListBean {
        return RxHttp.get("/article/list/0/json")
            .toAwaitResponse<HomeListBean>()
            .await()
    }

    suspend fun getTopBanner(): List<BannerResponse>? {
        return RxHttp.get("/banner/json")
            .toAwaitResponse<List<BannerResponse>?>()
            .await()
    }


}