package huaan.com.mvvmdemo.http.base

/**
 * actor 晴天 create 2019/5/31
 */
open class BaseRepository {

    suspend fun <T : Any> call(call: suspend () -> ResponseData<T>): ResponseData<T> {
        return call.invoke()
    }

}