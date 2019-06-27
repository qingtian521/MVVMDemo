package huaan.com.mvvmdemo.http.base
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders

abstract class BaseViewModelActivity<VM: BaseViewModel> : BaseActivity() {

    protected lateinit var viewModel:VM

    override fun onCreate(savedInstanceState: Bundle?) {
        initVM()
        super.onCreate(savedInstanceState)
    }

    private fun initVM() {
        providerVMClass()?.let {
            viewModel = ViewModelProviders.of(this).get(it)
            lifecycle.addObserver(viewModel)
        }
    }

    open fun providerVMClass(): Class<VM>? = null

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(viewModel)
    }
}