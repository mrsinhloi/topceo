package com.smartapp.sizes

import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.util.Size
import com.smartapp.collage.getImageOrientation
import com.smartapp.collage.getImageSize
import java.io.File

/**
ISize size = new SizeFromImage(imgFilePath);
size.width();
size.height();
 */
class SizeFromImage(var path: String) : ISize {
    private var width = -1
    private var height = -1

    override fun width(): Int {
        if (width == -1) {
            init()
        }
        return width
    }

    override fun height(): Int {
        if (height == -1) {
            init()
        }
        return height
    }

    private fun init() {
        val size = getImageSize(path)
        width = size.width
        height = size.height

    }



}

