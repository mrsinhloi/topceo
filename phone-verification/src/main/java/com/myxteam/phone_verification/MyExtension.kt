package com.myxteam.phone_verification

import android.app.Activity
import android.content.Context
import android.os.IBinder
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*
import kotlin.concurrent.schedule


//TOAST//////////////////////////////////////////////////////////////////////////////////////////
fun Context.toast(message: String, isLenghtLong: Boolean = false) {
    Toast.makeText(this, message, if (isLenghtLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT)
        .show()
}

fun Context.toast(message: Int, isLenghtLong: Boolean = false) {
    toast(getString(message), isLenghtLong)
}

////////////////////////////////////////////////////////////////////////////////////////////////
fun Context.createDialogLoading(title: Int, isCancelable: Boolean = true): AlertDialog {
    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
    builder.also {
        if (title > 0) it.setTitle(title)
        it.setView(R.layout.loading_dialog)
        it.setCancelable(isCancelable)
    }
    return builder.create()
}
//TOAST//////////////////////////////////////////////////////////////////////////////////////////


//ALERT//////////////////////////////////////////////////////////////////////////////////////////
fun Context.simpleAlert(message: String, title: String = "") {
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(R.string.ok, null)
        .show()
}

fun Context.simpleAlert(message: Int, title: String = "") {
    simpleAlert(getString(message), title)
}

fun Context.alert(
    message: Int,
    titleRes: Int? = null,
    positiveBtn: Int = R.string.ok,
    negativeBtn: Int = R.string.cancel,
    action: () -> Unit
) {
    val builder = MaterialAlertDialogBuilder(this)
    builder.also {
        titleRes?.let { builder.setTitle(it) }
        it.setMessage(message)
        it.setPositiveButton(
            positiveBtn
        ) { dialog, which -> action() }
        it.setNegativeButton(negativeBtn, null)
    }
    builder.show()
}
//ALERT//////////////////////////////////////////////////////////////////////////////////////////


// functions//////////////////////////////////////////////////////////////////////////////////////////
/**
 * Show the soft keyboard.
 * @param activity the current activity
 */
/*fun showKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}*/

/**
 * Hide the soft keyboard.
 * @param activity the current activity
 */
/*fun hideKeyboard(activity: Activity) {
    val view = activity.currentFocus
    if (view != null) {
        hideKeyboard(activity, view.windowToken)
    }
}

fun hideKeyboard(activity: Activity, windowToken: IBinder?) {
    val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}*/

public fun AppCompatActivity.showKeyboard() {
    Timer().schedule(600) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
}

fun AppCompatActivity.hideKeyboard() {
    val view = this.currentFocus
    if (view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    // else {
//    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    // }
}

fun Fragment.hideKeyboard() {
    val activity = this.activity
    if (activity is AppCompatActivity) {
        activity.hideKeyboard()
    }
}
////////////////////////////////////////////////////////////////////////////////////////////

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}


fun View.focusAndShowKeyboard() {
    /**
     * This is to be called when the window already has focus.
     */
    fun View.showTheKeyboardNow() {
        if (isFocused) {
            post {
                // We still post the call, just in case we are being notified of the windows focus
                // but InputMethodManager didn't get properly setup yet.
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    requestFocus()
    if (hasWindowFocus()) {
        // No need to wait for the window to get focus.
        showTheKeyboardNow()
    } else {
        // We need to wait until the window gets focus.
        viewTreeObserver.addOnWindowFocusChangeListener(
            object : ViewTreeObserver.OnWindowFocusChangeListener {
                override fun onWindowFocusChanged(hasFocus: Boolean) {
                    // This notification will arrive just before the InputMethodManager gets set up.
                    if (hasFocus) {
                        this@focusAndShowKeyboard.showTheKeyboardNow()
                        // It’s very important to remove this listener once we are done.
                        viewTreeObserver.removeOnWindowFocusChangeListener(this)
                    }
                }
            })
    }
}