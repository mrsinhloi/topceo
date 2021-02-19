package com.topceo.login.weteam

import android.app.Activity
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class BaseActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    val prefs: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(this@BaseActivity) }
    open val fragContainer: Int = 0


    /**
        Life cycle
     */
    override fun onStart() {
        super.onStart()
        //EventBus.getDefault().register(this@BaseActivity)
    }

    override fun onStop() {
        super.onStop()
        //EventBus.getDefault().unregister(this@BaseActivity)
        closeKeyboard()
    }

    override fun onDestroy() {
        closeKeyboard()
        cancel()
        super.onDestroy()
    }



    /**
        Utils
     */
    fun Toolbar.init() {
        setSupportActionBar(this)
        this.setNavigationOnClickListener { onBackPressed() }
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            closeKeyboard()
            super.onBackPressed()
        }
        Log.d("Count", "${supportFragmentManager.backStackEntryCount }")
    }

    fun View.openKeyboard() {
        runCatching {
            (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }

    fun closeKeyboard() {
        runCatching {
            currentFocus?.windowToken?.let { token ->
                (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                        token, 0
                )
            }
        }
    }

    fun closeKeyboard(view: View) {
        runCatching {
            (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    view.windowToken, 0
            )
//            Logger.d("closeKeyboard")
        }
    }

    fun Int.isResultOk() = this == Activity.RESULT_OK
    fun Int.isResultCanceled() = this == Activity.RESULT_CANCELED
}