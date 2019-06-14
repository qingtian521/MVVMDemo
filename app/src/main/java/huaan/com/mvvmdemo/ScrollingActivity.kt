package huaan.com.mvvmdemo

import android.arch.lifecycle.Observer

import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import huaan.com.mvvmdemo.http.adapter.KotlinDataAdapter
import huaan.com.mvvmdemo.http.base.BaseViewModelActivity
import huaan.com.mvvmdemo.http.databean.Data
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.article_item.view.*
import kotlinx.android.synthetic.main.content_scrolling.*

class ScrollingActivity : BaseViewModelActivity<ScrollingViewModel>() {

    override fun getLayoutId(): Int = R.layout.activity_scrolling

    override fun providerVMClass(): Class<ScrollingViewModel>? = ScrollingViewModel::class.java

    private val TAG = this.javaClass.simpleName

    private val datas = mutableListOf<Data>()

    override fun initView() {
        recycleView.layoutManager = LinearLayoutManager(applicationContext,LinearLayoutManager.VERTICAL,false)
        recycleView.adapter = createAdapter()
    }

    override fun initData() {
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        viewModel.getActicle().observe(this, Observer{
            //获取到数据
            it?.run {
                datas.addAll(it)
                recycleView.adapter?.notifyDataSetChanged()
            }
        })
    }

    private fun createAdapter():KotlinDataAdapter<Data>{
        return KotlinDataAdapter.Builder<Data>()
                .setData(datas)
                .setLayoutId(R.layout.article_item)
                .addBindView { itemView, itemData ->
                    itemView.tv_name.text = itemData.name
                    itemView.tv_role.text = itemData.name
                }
                .onItemClick { itemView, itemData ->
                    showToast("点击了 ${itemData.name}")
                }
                .create()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
