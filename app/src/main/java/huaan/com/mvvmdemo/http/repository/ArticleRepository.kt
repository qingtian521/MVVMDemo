package huaan.com.mvvmdemo.http.repository

import huaan.com.mvvmdemo.http.RetrofitClient
import huaan.com.mvvmdemo.http.base.BaseRepository
import huaan.com.mvvmdemo.http.base.ResponseData
import huaan.com.mvvmdemo.http.databean.Data

class ArticleRepository : BaseRepository() {
    suspend fun getDatas(): ResponseData<List<Data>> {
       return call {  RetrofitClient.reqApi.getDatas().await() }
    }
}