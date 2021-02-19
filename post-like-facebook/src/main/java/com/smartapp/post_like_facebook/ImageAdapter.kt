package com.smartapp.post_like_facebook

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smartapp.collage.MediaLocal
import kotlinx.android.synthetic.main.activity_edit_image_row.view.*

class ImageAdapter(context: Context, list: ArrayList<MediaLocal>) : BaseAdapter<MediaLocal>(context, list) {

    override fun getItemViewType(position: Int): Int {
        return R.layout.activity_edit_image_row
    }

    override fun configure(position: Int, holder: RecyclerView.ViewHolder) {
        val item: MediaLocal = list[position]

        Glide.with(context)
                .load(item.path)
                .override(screenWidth, screenHeight)
                .into(holder.itemView.img1)
        holder.itemView.img2.setOnClickListener {
            //remove image
            removerItem(item)
        }
    }

}