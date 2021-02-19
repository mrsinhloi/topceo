package com.topceo.group

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.topceo.R
import com.topceo.config.MyApplication
import com.topceo.fragments.Fragment_1_Home_SonTung
import com.topceo.group.models.GroupInfo
import com.topceo.services.ReturnResult
import com.topceo.services.Webservices
import com.topceo.utils.MyUtils
import com.google.gson.JsonObject
import com.smartapp.loadmore.BaseAdapterLoadMore
import com.smartapp.loadmore.HeaderViewHolder
import com.smartapp.loadmore.ItemViewHolder
import com.smartapp.loadmore.LoadingViewHolder
import kotlinx.android.synthetic.main.group_item_vertical.view.*
import kotlinx.android.synthetic.main.header_simple_1.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AllGroupAdapter(context: Context, list: ArrayList<GroupInfo?>) : BaseAdapterLoadMore<GroupInfo>(context, list) {

    override fun getItemViewType(position: Int): Int {
        return if (list[position] == null) {
            R.layout.progress_loading
        } else {
            if (list[position]?.isHeader!!) {
                R.layout.header_simple_1
            } else {
                R.layout.group_item_vertical
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(viewType, parent, false)

        return when (viewType) {
            R.layout.header_simple_1 -> HeaderViewHolder(view)
            R.layout.group_item_vertical -> ItemViewHolder(view)
            else -> LoadingViewHolder(view)
        }
    }

    override fun configure(item: GroupInfo, holder: RecyclerView.ViewHolder) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (item.isHeader) {
            holder.itemView.txtTitle.text = item.groupName
        } else {
            corners = context.resources.getDimensionPixelOffset(R.dimen.corners_group)
            val avatarSize = screenWidth / 5
            if (!TextUtils.isEmpty(item.coverUrl)) {
                Glide.with(context)
                        .load(item.coverUrl)
                        .placeholder(R.drawable.no_media_small)
                        .override(avatarSize, avatarSize)
                        .transform(CenterCrop(), RoundedCorners(corners))
                        .into(holder.itemView.imageView1)
            } else {
                Glide.with(context)
                        .load(R.drawable.no_media_small)
                        .override(avatarSize, avatarSize)
                        .transform(CenterCrop(), RoundedCorners(corners))
                        .into(holder.itemView.imageView1)
            }

            //ten
            holder.itemView.textView1.setText(item.groupName)
//            val text = TimeAgo.using(item.createDate * 1000, messages)
//            holder.itemView.textView2.setText(text.capitalize())
            //members
            holder.itemView.textView2.text = context.getString(R.string.number_members, item.totalMember)

            //description
            holder.itemView.textView3.text = item.description

            holder.itemView.constraintLayout.setOnClickListener(View.OnClickListener {
                GroupDetailActivity.openActivity(context, item)
            })

            if (item.isJoin == GroupInfo.JOINNED) {
                holder.itemView.btnJoin.visibility = View.GONE
            } else {
                holder.itemView.btnJoin.visibility = View.VISIBLE
                holder.itemView.btnJoin.setOnClickListener { whenRequestJoin(item.groupId, holder.itemView.btnJoin, item) }
            }
        }

    }

    fun whenRequestJoin(groupId: Long, btnJoin: Button, item: GroupInfo) {
        if (MyUtils.checkInternetConnection(context)) {
            MyApplication.apiManager.groupRequestJoin(groupId,
                    object : Callback<JsonObject?> {
                        override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                            val data = response.body()
                            if (data != null) {
                                val result = Webservices.parseJson(data.toString(), Boolean::class.java, false)
                                if (result != null) {
                                    if (result.errorCode == ReturnResult.SUCCESS) { //da ton tai thi vao
                                        MyUtils.showToast(context, R.string.join_to_group_success)
                                        //khi nhan join xong thi an button, set lai gia tri cho vi tri da join
                                        btnJoin.visibility = View.GONE
                                        item.isJoin = GroupInfo.JOINNED
                                        //load lai danh sach group
                                        context.sendBroadcast(Intent(Fragment_1_Home_SonTung.ACTION_REFRESH_GROUP))

                                        //khi join
                                        if (groupListener != null) {
                                            groupListener?.onJoinGroup(item)
                                        }
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                            MyUtils.log("error")
                        }
                    })
        } else {
            MyUtils.showThongBao(context)
        }
    }

    private var groupListener: GroupListener? = null
    fun setGroupListener(listener: GroupListener) {
        groupListener = listener
    }

    fun findPosition(groupId: Long): Int {
        var index: Int = -1
        for (position in 0 until list.size) {
            val item = list[position]
            if (item != null) {
                if (item?.groupId == groupId) {
                    index = position
                    break
                }
            }
        }
        return index
    }

    fun updateGroupJoin(groupId: Long) {
        val position = findPosition(groupId)
        if (position >= 0) {
            val item = list[position]
            if (item != null) {
                item.isJoin = GroupInfo.JOINNED
                notifyItemChanged(position)
            }
        }
    }

    fun deleteGroup(groupId: Long) {
        val position = findPosition(groupId)
        if (position >= 0) {
            removerPosition(position)
        }
    }

    fun decreaseMember(groupId: Long) {
        val position = findPosition(groupId)
        if (position >= 0) {
            if (list[position]?.totalMember!! > 0) {
                list[position]?.totalMember = list[position]?.totalMember!! - 1
                notifyItemChanged(position)
            }
        }
    }

    fun updateGroupBanner(group: GroupInfo) {
        val position = findPosition(group.groupId)
        if (position >= 0) {
            val item = list[position]
            if (item != null) {
                item.coverSmallUrl = group.coverSmallUrl
                item.coverUrl = group.coverUrl
                notifyItemChanged(position)
            }
        }
    }

    fun replace(group: GroupInfo) {
        val position = findPosition(group.groupId)
        if (position >= 0) {
            list.set(position, group)
            notifyItemChanged(position)
        }
    }
}