package huaan.com.mvvmdemo.http

import huaan.com.mvvmdemo.http.base.ResponseData
import huaan.com.mvvmdemo.http.databean.Data
import kotlinx.coroutines.Deferred
import retrofit2.http.GET

interface RequestService {
    @GET("wxarticle/chapters/json")
    fun getDatas() : Deferred<ResponseData<List<Data>>>
}