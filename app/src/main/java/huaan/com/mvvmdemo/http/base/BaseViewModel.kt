package huaan.com.mvvmdemo.http.base

import androidx.lifecycle.*
import kotlinx.coroutines.*
import java.lang.Exception


/**
 * create By 2019/6/3 actor 晴天
 */
open class BaseViewModel : ViewModel(), LifecycleObserver {


    private val error by lazy { MutableLiveData<Exception>() }

    private val finally by lazy { MutableLiveData<Int>() }

    //运行在UI线程的协程
    fun launchUI(block: suspend CoroutineScope.() -> Unit) = viewModelScope.launch {
        try {
            withTimeout(5000){
                block()
            }
        } catch (e: Exception) {
            error.value = e
        } finally {
            finally.value = 200
        }
    }

    /**
     * 请求失败，出现异常
     */
    fun getError(): LiveData<Exception> {
        return error
    }

    /**
     * 请求完成，在此处做一些关闭操作
     */
    fun getFinally(): LiveData<Int> {
        return finally
    }
}