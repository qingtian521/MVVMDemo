package huaan.com.mvvmdemo.http.base


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_scrolling.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

/**
 * create By 2019/5/27 actor 晴天
 */
abstract class BaseActivity : AppCompatActivity(),EasyPermissions.PermissionCallbacks {

    companion object{
        const val PERMISSION_CODE = 0X01
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initView()
        setSupportActionBar(toolbar)
        initData()
    }

    abstract fun getLayoutId(): Int
    abstract fun initView()
    abstract fun initData()

    protected fun startActivity(z : Class<*>){
        startActivity(Intent(applicationContext,z))
    }


    protected fun showToast(msg:String){
        Toast.makeText(applicationContext,msg,Toast.LENGTH_SHORT).show()
    }


    //请求一些必须要的权限
    protected fun requestPermission(permission : Array<String>) {
        if (EasyPermissions.hasPermissions(this, *permission)) {
            //具备权限 直接进行操作
            onPermissionSuccess()
        } else {
            //权限拒绝 申请权限
            EasyPermissions.requestPermissions(this, "为了正常使用，需要获取以下权限", PERMISSION_CODE, *permission); }
    }

    //权限申请相关
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 将结果转发到EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    //权限获取成功
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        onPermissionSuccess()
    }

    //权限获取被拒绝
    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        /**
         * 若是在权限弹窗中，用户勾选了'NEVER ASK AGAIN.'或者'不在提示'，且拒绝权限。
         * 这时候，需要跳转到设置界面去，让用户手动开启。
         */
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            //拒绝了权限，而且选择了不在提醒，需要去手动设置了
            AppSettingsDialog.Builder(this).build().show()
        }
        //拒绝了权限，重新申请
        else{
            onPermissionFail()
        }
    }

    /**
     * 权限申请成功执行方法
     */
    protected open fun onPermissionSuccess(){

    }
    /**
     * 权限申请失败
     */
    protected open fun onPermissionFail(){

    }

}