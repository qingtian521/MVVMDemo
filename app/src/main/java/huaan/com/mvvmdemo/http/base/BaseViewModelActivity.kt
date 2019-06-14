package huaan.com.mvvmdemo.http.base
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import huaan.com.mvvmdemo.http.base.BaseViewModel
import huaan.com.mvvmdemo.http.base.BaseActivity

abstract class BaseViewModelActivity<VM: BaseViewModel> : BaseActivity() {

    protected lateinit var viewModel:VM

    override fun onCreate(savedInstanceState: Bundle?) {
        initVM()
        super.onCreate(savedInstanceState)
        startObserve()
    }

    private fun initVM() {
        providerVMClass()?.let {
            viewModel = ViewModelProviders.of(this).get(it)
            lifecycle.addObserver(viewModel)
        }
    }

    open fun providerVMClass(): Class<VM>? = null
    open fun startObserve() {}

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(viewModel)
    }
}