package huaan.com.mvvmdemo.http.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * actor 晴天 create 2019/5/31
 */
open class BaseRepository {

    suspend fun <T : Any> request(call: suspend () -> ResponseData<T>): ResponseData<T> {
        return withContext(Dispatchers.IO){ call.invoke()}
    }

}