package com.smartapp.gallery

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smartapp.post_like_facebook.BaseAdapter
import com.smartapp.post_like_facebook.R
import kotlinx.android.synthetic.main.activity_edit_image_row.view.*

class ShowGalleryAdapter(context: Context, list: ArrayList<String>, val onClicked: OnClickImage) : BaseAdapter<String>(context, list) {

    override fun getItemViewType(position: Int): Int {
        return R.layout.activity_view_image_page
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun configure(position: Int, holder: RecyclerView.ViewHolder) {
        val item: String = list[position]

        Glide.with(context)
                .load(item)
                .override(screenWidth, screenHeight)
                .into(holder.itemView.img1)

        holder.itemView.img1.apply {
//                    layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

            setOnTouchListener { view, event ->
                var result = true
                //can scroll horizontally checks if there's still a part of the image
                //that can be scrolled until you reach the edge
                if (event.pointerCount >= 2 || view.canScrollHorizontally(1) && canScrollHorizontally(-1)) {
                    //multi-touch event
                    result = when (event.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                            // Disallow RecyclerView to intercept touch events.
                            parent.requestDisallowInterceptTouchEvent(true)
                            // Disable touch on view
                            false
                        }
                        MotionEvent.ACTION_UP -> {
                            // Allow RecyclerView to intercept touch events.
                            parent.requestDisallowInterceptTouchEvent(false)
                            true
                        }
                        else -> true
                    }
                }
                result
            }

            setOnClickListener { onClicked.onClicked() }
        }
    }

}