package com.smartapp.collage

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.smartapp.post_like_facebook.R
import kotlinx.android.synthetic.main.view_collage_image.view.*

class CollageImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var images: List<Bitmap> = emptyList()
        set(value) {
            field = value

            collageImageView.adapter?.let {
                (collageImageView.adapter as CollageAdapter).resetImages(value)
            } ?: let {
                collageImageView.adapter = CollageAdapter(value)
            }
        }

    lateinit var viewRoot: View

    init {
        viewRoot = View.inflate(context, R.layout.view_collage_image, this)
    }

}
