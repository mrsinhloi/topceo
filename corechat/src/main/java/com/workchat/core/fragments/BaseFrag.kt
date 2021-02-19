package com.workchat.core.fragments.base

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.workchat.core.event.SocketConnectEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class BaseFrag : Fragment(), CoroutineScope by MainScope() {

    abstract val fragLayout: Int

    val prefs: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(this@BaseFrag.activity) }

    var netWorkConnected = false
    var socketConnected = false

    /**
        Network handle
     */
    /*private val networkReceiver = NetworkReceiver(
        onConnected = {
            netWorkConnected = true
            onNetworkConnected()
        },
        onDisconnected = {
            netWorkConnected = false
            onNetworkDisconnected()
        }
    )
    open fun onNetworkConnected() { }
    open fun onNetworkDisconnected() { }*/

    /**
    Socket handle
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND, sticky = true)
    fun socketStatus(socketStatus: SocketConnectEvent) {
        //Logger.d("$socketStatus")
        when (socketStatus.isConnected) {
            true -> onSocketConnected(socketStatus)
            false -> onSocketDisconnected(socketStatus)
        }
        socketConnected = socketStatus.isConnected
    }
    open fun onSocketConnected(socketStatus: SocketConnectEvent) { }
    open fun onSocketDisconnected(socketStatus: SocketConnectEvent) { }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(
            fragLayout,
            container,
            false
    ).apply {
        onCreateViewBlock().invoke(this)
    }

    abstract fun onCreateViewBlock(): View.() -> Unit


    override fun onStart() {
        super.onStart()
//        registerNetworkReceiver(networkReceiver)
        EventBus.getDefault().register(this@BaseFrag)
    }

    override fun onStop() {
        super.onStop()
//        unregisterNetworkReceiver(networkReceiver)
        EventBus.getDefault().unregister(this@BaseFrag)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    fun runOnUiThread(block: () -> Unit) {
        activity?.runOnUiThread(block)
    }

    fun Toolbar.init() {
        (activity as AppCompatActivity).apply {
            setSupportActionBar(this@init)
            this@init.setNavigationOnClickListener { onBackPressed() }
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
            }
        }
    }

    fun closeKeyboard(view: View) {
        runCatching {
            val imm: InputMethodManager = context!!
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun logInMainLooper() {

    }

    fun FragmentActivity?.lockNav() {
        //(this as MainActivity).navigationLock = true
    }

    fun FragmentActivity?.unlockNav() {
        //(this as MainActivity).navigationLock = false
    }

}

typealias CreateViewBlock = View.() -> Unit
