package com.workchat.core.autolink;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * Tạo class TouchableSpan kế thừa từ ClickableSpan để gắn các thuộc tính màu, gạch dưới cho đoạn Text chứa link.
 */
public class TouchableSpan extends ClickableSpan {
    // Status link
    private boolean isPressed;
    // Color of link
    private int normalTextColor;
    // Color of link when user click
    private int pressedTextColor;
    private boolean isUnderLineEnabled;

    TouchableSpan(int normalTextColor, int pressedTextColor, boolean isUnderLineEnabled) {
        this.normalTextColor = normalTextColor;
        this.pressedTextColor = pressedTextColor;
        this.isUnderLineEnabled = isUnderLineEnabled;
    }

    void setPressed(boolean isSelected) {
        isPressed = isSelected;
    }

    @Override
    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);
        int textColor = isPressed ? pressedTextColor : normalTextColor;
        textPaint.setColor(textColor);
        textPaint.bgColor = Color.TRANSPARENT;
        textPaint.setUnderlineText(isUnderLineEnabled);
    }

    @Override
    public void onClick(@NonNull View widget) {

    }

}
