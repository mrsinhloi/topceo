package com.smartapp.sizes

import android.graphics.Point
import android.view.Display

/**
ISize size = new SizeFromDisplay(getWindowManager().getDefaultDisplay());
size.width();
size.height();
 */
class SizeFromDisplay : ISize {
    private var display: Display? = null

    fun SizeFromDisplay(display: Display?) {
        this.display = display
    }

    override fun width(): Int {
        val point = Point()
        display!!.getRealSize(point)
        return point.x
    }

    override fun height(): Int {
        val point = Point()
        display!!.getRealSize(point)
        return point.y
    }


}