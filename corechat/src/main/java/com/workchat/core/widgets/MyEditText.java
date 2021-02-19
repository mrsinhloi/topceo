package com.workchat.core.widgets;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.widget.AppCompatEditText;

/**
 * Created by MrPhuong on 3/9/2018.
 */

public class MyEditText extends AppCompatEditText {

    public MyEditText(Context context) {
        super(context);
        setupEditText();
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupEditText();
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupEditText();
    }

    public void setupEditText() {
        // Any time edit text instances lose their focus, dismiss the keyboard!
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !(findFocus() instanceof MyEditText)) {
                    hideKeyboard(v);
                } else {
                    showKeyboard(v);
                }
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, 0);
    }
}
