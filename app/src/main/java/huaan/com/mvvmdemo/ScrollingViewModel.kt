package huaan.com.mvvmdemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import huaan.com.mvvmdemo.http.base.BaseViewModel
import huaan.com.mvvmdemo.http.databean.Data
import huaan.com.mvvmdemo.http.repository.ArticleRepository

class ScrollingViewModel : BaseViewModel() {

    private val TAG = ScrollingViewModel::class.java.simpleName

    private val datas: MutableLiveData<List<Data>> by lazy { MutableLiveData<List<Data>>().also { loadDatas() } }

    private val repository = ArticleRepository()

    fun getActicle(): LiveData<List<Data>> {
        return datas
    }

    private fun loadDatas() = launchUI {
            val result = repository.getDatas()
            datas.value = result.data
    }
}