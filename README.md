# MVVMDemo
协程+kotlin + retrofit + mvvm

## 前言

最近一直闭关修炼Kotlin，说实话真香真好用，刚好公司准备交给我一个新项目，于是打算直接用Kotlin来构建项目。刚好整体架构搭建完毕了，于是把网络请求这一部分先分享给大家。这次使用到的是 协程+ retrofit +mvvm的模式，我这儿直接用一个简单的demo来看一下具体的实现方式吧。文章只是描述实现思路，需要demo的直接跳到文末
## 项目配置
首先先引入所需要的依赖
~~~
        implementation 'android.arch.lifecycle:extensions:1.1.1'
        //协程
        implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1'
        implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.1.1'
        //retrofit + okHttp3
        implementation 'com.squareup.retrofit2:retrofit:2.4.0'
        implementation 'com.squareup.retrofit2:converter-gson:2.4.0'
        implementation 'com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2'
~~~
## 实现思路
不管设计模式这些，先来一个简单的网络请求，就retrofit的基本实现，看看需要哪些步骤
### 1.创建retrofit
    ~~~
        val retrofit = Retrofit.Builder()
                    .baseUrl(RetrofitClient.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .build()
    ~~~
### 2.创建service接口
    ~~~
        interface RequestService {
            @GET("wxarticle/chapters/json")
            fun getDatas() : Call<DataBean>
        }
    ~~~
### 3.发起请求
    ~~~
        val service = retrofit.create(RequestService::class.java)
        service.getDatas().enqueue(object : Callback<DataBean> {
            override fun onFailure(call: retrofit2.Call<DataBean>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onResponse(call: retrofit2.Call<DataBean>, response: Response<DataBean>) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    ~~~
这只是描述了一个retrofit的简单请求方式，实际项目中基本上都会封装之后再使用，也为了提高代码的可读性，降低各部分的耦合性，
通俗点来说，只有各司其职才能把工作干好嘛，接下来咱们就围绕着各司其职来一个一个实现

## 协程实现
接下来把上面的请求换成协程的方式来实现
### 1.创建RetrofitClient
    object为了使RetrofitClient 只能有一个实例
    ~~~
        object RetrofitClient {
            val BASE_URL =  "https://wanandroid.com/"
            val reqApi by lazy {
                val retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(CoroutineCallAdapterFactory())
                        .build()
                return@lazy retrofit.create(RequestService::class.java)
            }
        }
    ~~~
### 2.创建service接口类
    ~~~
    interface RequestService {
        @GET("wxarticle/chapters/json")
        fun getDatas() : Deferred<DataBean>
    }
    ~~~
因为我们后续会使用到协程，所以这儿将Call换成了Deferred
### 3.发起请求
    ~~~
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO){
              val dataBean = RetrofitClient.reqApi.getDatas().await()
            }
            //更新ui
        }
    ~~~
上面用到了协程，这儿只讲述他的应用了，具体的移步官方文档进一步了解。
网络请求在协程中，并且在IO调度单元，所以不用担会阻塞主线程
## 协程 + ViewModel + LiveData实现
上面也只是简单的实现，只不过是换成了协程，在项目中，还可以进一步封装，方便使用前面也提到了MVVM，所以还用到了Android 新引入的组件架构之ViewModel和LiveData，先看ViewModel的实现
   ~~~
   class ScrollingViewModel  : ViewModel() {
       private val TAG = ScrollingViewModel::class.java.simpleName
       private val datas: MutableLiveData<DataBean> by lazy { MutableLiveData<DataBean>().also { loadDatas() } }
       private val repository = ArticleRepository()
       fun getActicle(): LiveData<DataBean> {
           return datas
       }
       private fun loadDatas() {
           GlobalScope.launch(Dispatchers.Main) {
               getData()
           }
           // Do an asynchronous operation to fetch users.
       }
       private suspend fun getData() {
           val result = withContext(Dispatchers.IO){
   //            delay(10000)
               repository.getDatas()
           }
          datas.value = result
       }
   }
   ~~~
   ViewModel将作为View与数据的中间人，Repository专职数据获取，下面看一下Repository的代码，用来发起网络请求获取数据
   ~~~
    class ArticleRepository {
        suspend fun getDatas(): DataBean {
             return RetrofitClient.reqApi.getDatas().await()
         }
     }
   ~~~
 在Activity中代码如下
   ~~~
       private fun initData() {
           model.getActicle().observe(this, Observer{
               //获取到数据
               toolbar.setBackgroundColor(Color.RED)
           })
       }
   ~~~
   
## 后续优化
### 1.内存泄漏问题解决方案
结和了各位大佬们的意见，将使用GlobalScope可能会出现内存泄漏的问题进行了优化。因为在协程进行请求的过程中，若此时ViewModel销毁，里面的协程正在请求的话，将无法销毁，出现内存泄漏，所以在ViewModel onCleared 里面，即使结束协程任务，参考代码如下。
~~~
 open class BaseViewModel : ViewModel(), LifecycleObserver{
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    //运行在UI线程的协程
    fun launchUI( block: suspend CoroutineScope.() -> Unit) {
        try {
            uiScope.launch(Dispatchers.Main) {
                block()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
~~~
当然，最好的方式是使用viewModelScope，但是我在引入该包的时候，会报错，由于最近比较忙暂时还没来得急解决，后续问题有时间我也会继续修改，还望各位大佬能帮忙指点

### 2.优化请求代码
先看下之前的请求代码
~~~
private suspend fun getData() {
        val result = withContext(Dispatchers.IO){
//            delay(10000)
            repository.getDatas()
        }
       datas.value = result
    }
~~~
每一次都需要写个withContext（），实际运用中，感觉有点不方便，于是乎想了一下，怎么才能给他封进请求方法里面？
代码如下
~~~
open class BaseRepository {
    suspend fun <T : Any> request(call: suspend () -> ResponseData<T>): ResponseData<T> {
        return withContext(Dispatchers.IO){ call.invoke()}
    }
}
~~~
通过在BaseRepository里面写了一个专门的请求方法，这样每次只需执行request就行了
请求参考如下
~~~
class ArticleRepository : BaseRepository() {
    suspend fun getDatas(): ResponseData<List<Data>> {
       return request {
           delay(10000)
           Log.i(ScrollingViewModel::class.java.simpleName,"loadDatas1 run in  ${Thread.currentThread().name}")
           RetrofitClient.reqApi.getDatas().await() }
    }
}
~~~
注：这个 delay(10000)只是我测试用的，意思是休眠当前协程，防止萌新在自己项目中加上了，还是有必要说一下的

再看看ViewModel中就太简单了
~~~
class ScrollingViewModel : BaseViewModel() {
    private val TAG = ScrollingViewModel::class.java.simpleName
    
    private val datas: MutableLiveData<List<Data>> by lazy { MutableLiveData<List<Data>>().also { loadDatas() } }
    
    private val repository = ArticleRepository()
    fun getActicle(): LiveData<List<Data>> {
        return datas
    }
    
    private fun loadDatas() {
        launchUI {
            Log.i(TAG,"loadDatas1 run in  ${Thread.currentThread().name}")
            val result = repository.getDatas()
            Log.i(TAG,"loadDatas3 run in  ${Thread.currentThread().name}")
            datas.value = result.data
        }
        // Do an asynchronous operation to fetch users.
    }
}
~~~
注意看请求部分，就两句话，一句发起请求val result = repository.getDatas()，然后就是为我们的LiveData赋值了，看起有没有同步代码的感觉，这就是协程的魅力所在，为了验证我们的请求没有阻塞主线程，我打印了日志
~~~
06-19 12:26:35.736 13648-13648/huaan.com.mvvmdemo I/ScrollingViewModel: loadDatas start run in  main
06-19 12:26:45.743 13648-13684/huaan.com.mvvmdemo I/ScrollingViewModel: request run in  DefaultDispatcher-worker-1
06-19 12:26:46.227 13648-13648/huaan.com.mvvmdemo I/ScrollingViewModel: loadDatas end  run in  main
~~~
看到了吧，各司其职，效果很棒

## 异常处理
搞了半天才发现没有弄异常处理，当请求失败之后，项目就崩溃了，这不是是我们想要的结果，由于好没有想到更好的处理方式，只能在外面套个tyr catch 顶一顶了，参考如下
~~~
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
~~~

## 结语
上面只是描述了一些实现过程，具体使用还得参考demo，基本上能满足大部分的需求，要是感兴趣的小伙伴，可以下载demo参考，感觉不错的话，顺手点个赞就很满足了。于所学不精，可能会有使用不当之处，希望各位大佬能指出不当的地方，深表感谢。
### 附上项目地址
https://github.com/qingtian521/MVVMDemo.git
