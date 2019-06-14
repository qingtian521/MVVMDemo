package huaan.com.mvvmdemo.http.base

/**
 * actor 晴天 create 2019/5/31
 * 响应结果数据
 */
data class ResponseData<out T>(val errorCode: Int, val errorMsg: String, val data: T)