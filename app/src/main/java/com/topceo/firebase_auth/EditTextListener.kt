package com.topceo.firebase_auth

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

fun EditText.textWatcher(
        beforeTextChanged: ((charSequence: CharSequence, start: Int, count: Int, after: Int) -> Unit) = {_: CharSequence, _: Int, _: Int, _: Int -> },
        afterTextChanged: ((editable: Editable)-> Unit) = {_: Editable -> },
        onTextChanged: ((charSequence: CharSequence, start: Int, count: Int, after: Int) -> Unit) = {_: CharSequence, _: Int, _: Int, _: Int -> }
) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
            charSequence?.let {
                beforeTextChanged(charSequence, start, count, after)
            }
        }
        override fun afterTextChanged(editable: Editable?) {
            editable?.let { afterTextChanged(it) }
        }
        override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
            charSequence?.let { onTextChanged(charSequence, start, before, count)}
        }
    })
}