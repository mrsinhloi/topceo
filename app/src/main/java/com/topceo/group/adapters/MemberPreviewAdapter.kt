package com.topceo.group.adapters

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.topceo.R
import com.topceo.group.models.GroupMember
import com.smartapp.post_like_facebook.BaseAdapter
import kotlinx.android.synthetic.main.group_member_preview_item.view.*

class MemberPreviewAdapter(context: Context, list: ArrayList<GroupMember>, val listener: View.OnClickListener) : BaseAdapter<GroupMember>(context, list) {

    override fun getItemViewType(position: Int): Int {
        return R.layout.group_member_preview_item
    }

    override fun configure(position: Int, holder: RecyclerView.ViewHolder) {
        val item: GroupMember = list[position]

        val avatarSize = screenWidth / 11
        val layout = LinearLayout.LayoutParams(avatarSize, avatarSize)
        holder.itemView.img1.layoutParams = layout

        Glide.with(context)
                .load(item.avatarSmall)
                .override(avatarSize, avatarSize)
                .placeholder(R.drawable.ic_no_avatar)
                .into(holder.itemView.img1)
        holder.itemView.img1.setOnClickListener {
            //go to list member + admin
            listener.onClick(it)
        }

        val params = holder.itemView.linearImage.layoutParams as RecyclerView.LayoutParams
        if (position == 0) {
            params.marginStart = 0
        } else {
            val margin = context.resources.getDimensionPixelOffset(R.dimen.group_margin_left)
            params.marginStart = margin
        }
    }

}
