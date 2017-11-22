package netset.com.kotlinbaseproject

import android.content.*
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.widget.Toast
import netset.com.kotlinbaseproject.MyInterFace.PermCallBack
import netset.com.kotlinbaseproject.Utils.ImageUtils
import netset.com.kotlinbaseproject.Utils.NetworkUtil
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * Created by netset on 21/11/17.
 */
open class BaseActivity : AppCompatActivity() {

    public val networkDialog: AlertDialog.Builder?=null
    public var networkAlertDialog: AlertDialog? = null
    public var networkStatus: String? = null
    public var permCallback: PermCallBack? = null
    public var reqCode: Int = 0
    private var networksBroadcast: NetworksBroadcast? = null


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        networkDialog = AlertDialog.Builder(this)
        initializeNetworkBroadcast();
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            if (networksBroadcast != null) {
                unregisterReceiver(networksBroadcast)
            }
        } catch (e: IllegalArgumentException) {
            networksBroadcast = null
        }
    }

    fun checkPermissions(perms: Array<String>, requestCode: Int, permCallback: PermCallBack): Boolean {
        this.permCallback = permCallback
        this.reqCode = requestCode
        val permsArray = ArrayList<String>()
        var hasPerms = true
        for (perm in perms) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                permsArray.add(perm)
                hasPerms = false
            }
        }
        if (!hasPerms) {
            val permsString = arrayOfNulls<String>(permsArray.size)
            for (i in permsArray.indices) {
                permsString[i] = permsArray[i]
            }
            ActivityCompat.requestPermissions(this@BaseActivity, permsString, 99)
            return false
        } else
            return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        var permGrantedBool = false
        when (requestCode) {
            99 -> {
                for (grantResult in grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        showToast("not_sufficient_permissions")
                        permGrantedBool = false
                        break
                    } else {
                        permGrantedBool = true
                    }
                }
                if (permGrantedBool) {
                    permCallback!!.permGranted(reqCode)
                } else {
                    permCallback!!.permDenied(reqCode)
                }
            }
        }
    }

    private fun initializeNetworkBroadcast() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        networksBroadcast = NetworksBroadcast()
        registerReceiver(networksBroadcast, intentFilter)
    }

    inner class NetworksBroadcast : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val status = NetworkUtil.getConnectivityStatusString(context)
            if (status != null)
                showNoNetworkDialog(status)
            else {
                if (networkAlertDialog != null && networkAlertDialog!!.isShowing())
                    networkAlertDialog!!.dismiss()
            }
        }
    }

    public fun showNoNetworkDialog(status: String) {
         networkDialog = AlertDialog.Builder(applicationContext)
        networkDialog.setTitle(getString(R.string.netwrk_status))
        networkDialog.setMessage(status)
        networkDialog.setPositiveButton(getString(R.string.retry), DialogInterface.OnClickListener { dialog, which ->
            if (!isNetworkAvailable()) {
                dialog.dismiss()
                showNoNetworkDialog(status)
            }
        })
        networkDialog.setCancelable(false)
        var networkAlertDialog = networkDialog.create()
        if (!networkAlertDialog.isShowing()) {
            networkAlertDialog.show()
        }
    }

    fun keyHash() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KeyHash:>>>>>>>>>>>>>>>", "" + Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

    }

    private fun showToast(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show();
    }


    fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager
                .activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ImageUtils.activityResult(requestCode, resultCode, data)
    }
}



