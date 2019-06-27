package huaan.com.mvvmdemo.http.repository

import android.util.Log
import huaan.com.mvvmdemo.ScrollingViewModel
import huaan.com.mvvmdemo.http.RetrofitClient
import huaan.com.mvvmdemo.http.base.BaseRepository
import huaan.com.mvvmdemo.http.base.ResponseData
import huaan.com.mvvmdemo.http.databean.Data
import kotlinx.coroutines.delay

class ArticleRepository : BaseRepository() {

    suspend fun getDatas(): ResponseData<List<Data>> = request {
        RetrofitClient.reqApi.getDatas()
    }
}