package huaan.com.mvvmdemo.http.base

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.*
import java.lang.Exception


/**
 * create By 2019/6/3 actor 晴天
 */
open class BaseViewModel : ViewModel(), LifecycleObserver{

    //运行在UI线程的协程
    fun launchUI( block: suspend CoroutineScope.() -> Unit) {
        try {
            GlobalScope.launch(Dispatchers.Main) {
                block()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

}