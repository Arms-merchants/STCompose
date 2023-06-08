package com.example.stcompose.chat.data

import android.util.Log
import com.example.stcompose.chat.data.bean.ProjectTabItemBean
import rxhttp.RxHttp
import rxhttp.toAwaitResponse
import javax.inject.Inject

/**
 *    author : heyueyang
 *    time   : 2023/05/23
 *    desc   :
 *    version: 1.0
 */
class ProjectRepository @Inject constructor() {
    suspend fun getTabList(): List<ProjectTabItemBean>? {
        Log.e("TAG","ProjectRepository")
        return RxHttp.get("/project/tree/json")
            .toAwaitResponse<List<ProjectTabItemBean>?>()
            .await()
    }
}