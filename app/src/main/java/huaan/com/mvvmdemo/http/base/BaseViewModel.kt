package huaan.com.mvvmdemo.http.base

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.*
import java.lang.Exception


/**
 * create By 2019/6/3 actor 晴天
 */
open class BaseViewModel : ViewModel(), LifecycleObserver{



    private val viewModelJob = SupervisorJob()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val error by lazy { MutableLiveData<Exception>() }

    private val finally by lazy { MutableLiveData<Int>() }

    //运行在UI线程的协程
    fun launchUI( block: suspend CoroutineScope.() -> Unit) {
        uiScope.launch(Dispatchers.Main) {
            try {
                block()
            }catch (e:Exception){
                error.value = e
            }finally {
                finally.value = 200
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
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