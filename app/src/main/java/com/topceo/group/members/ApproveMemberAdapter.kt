package com.topceo.group.members

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.topceo.R
import com.topceo.config.MyApplication
import com.topceo.fragments.GlideCircleTransform
import com.topceo.group.models.GroupMember
import com.topceo.services.ReturnResult
import com.topceo.services.Webservices
import com.topceo.utils.MyUtils
import com.google.gson.JsonObject
import com.smartapp.loadmore.ItemViewHolder
import com.smartapp.post_like_facebook.BaseAdapter
import kotlinx.android.synthetic.main.activity_like_row_approve.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ApproveMemberAdapter(context: Context, list: ArrayList<GroupMember>, val ownerId: Long, val groupId: Long) : BaseAdapter<GroupMember>(context, list) {

    override fun getItemViewType(position: Int): Int {
        return R.layout.activity_like_row_approve
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(viewType, parent, false)

        return ItemViewHolder(view)
    }

    override fun configure(position: Int, holder: RecyclerView.ViewHolder) {
        val item = list[position]

        val avatarSize = context.resources.getDimensionPixelSize(R.dimen.avatar_size_list_item)
        Glide.with(context)
                .load(item.avatarSmall)
                .placeholder(R.drawable.ic_no_avatar)
                .override(avatarSize, avatarSize)
                .transform(GlideCircleTransform(context))
                .into(holder.itemView.img1)

        holder.itemView.txt1.text = item.userName
        if (!item.fullName.isNullOrEmpty()) {
            holder.itemView.txt2.text = item.fullName
            holder.itemView.txt2.visibility = View.VISIBLE
        } else {
            holder.itemView.txt2.visibility = View.GONE
        }


        holder.itemView.img1.setOnClickListener(View.OnClickListener { MyUtils.gotoProfile(item.userName, context) })
        holder.itemView.linear1.setOnClickListener(View.OnClickListener { holder.itemView.img1.performClick() })


        //duyet
        holder.itemView.btn1.setOnClickListener { accept(item.groupId, item.userId, position)}
        //xoa
        holder.itemView.btn2.setOnClickListener { refuse(item.groupId, item.userId, position)}


    }


    fun accept(groupId: Long, userId: Long, position: Int) {
        MyApplication.apiManager.groupMemberRequestAccept(groupId, userId,
                object : Callback<JsonObject?> {
                    override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                        whenSuccess(response.body(), position)
                    }
                    override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                        MyUtils.log("error")
                    }
                })

    }

    fun refuse(groupId: Long, userId: Long, position: Int) {
        MyApplication.apiManager.groupMemberRequestRefuse(groupId, userId,
                object : Callback<JsonObject?> {
                    override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                        whenSuccess(response.body(), position)
                    }
                    override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                        MyUtils.log("error")
                    }
                })

    }
    private fun whenSuccess(data: JsonObject?, position: Int) {
        if (data != null) {
            val result = Webservices.parseJson(data.toString(), Boolean::class.java, false)
            if (result != null) {
                if (result.errorCode == ReturnResult.SUCCESS) { //da ton tai thi vao
                    list.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
        }
    }
}