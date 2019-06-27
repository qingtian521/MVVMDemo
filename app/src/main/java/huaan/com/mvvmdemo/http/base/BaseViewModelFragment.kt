package huaan.com.mvvmdemo.http.base


import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders

/**
 * create By 2019/5/28 actor 晴天
 * 需要用到viewModel才使用的baseFragment
 */
abstract class BaseViewModelFragment<VM : BaseViewModel> : Fragment(){

    private val fragmentName = javaClass.simpleName
    protected lateinit var viewModel:VM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initVM()
        initView()
        initData()
        startObserve()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initVM() {
        providerVMClass()?.let {
            viewModel = ViewModelProviders.of(this).get(it)
            lifecycle.addObserver(viewModel)
        }
    }

    open fun providerVMClass(): Class<VM>? = null
    open fun startObserve() {}

    /**
     * 必须实现的方法
     */
    abstract fun getLayoutId():Int

    abstract fun initView()

    abstract fun initData()

    override fun onDestroy() {
        super.onDestroy()
        if (this::viewModel.isInitialized)
        lifecycle.removeObserver(viewModel)
    }

}