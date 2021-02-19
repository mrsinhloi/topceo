package com.topceo.group.members

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.topceo.R
import com.topceo.config.MyApplication
import com.topceo.fragments.Fragment_1_Home_SonTung
import com.topceo.fragments.GlideCircleTransform
import com.topceo.group.AllGroupActivity
import com.topceo.group.models.GroupInfo
import com.topceo.group.models.GroupMember
import com.topceo.services.ReturnResult
import com.topceo.services.Webservices
import com.topceo.utils.MyUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonObject
import com.smartapp.loadmore.BaseAdapterLoadMore
import com.smartapp.loadmore.HeaderViewHolder
import com.smartapp.loadmore.ItemViewHolder
import com.smartapp.loadmore.LoadingViewHolder
import kotlinx.android.synthetic.main.activity_like_row_expand.view.*
import kotlinx.android.synthetic.main.header_simple_1.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MemberGroupAdapter(context: Context, list: ArrayList<GroupMember?>, val ownerId: Long, val canDeleteUser: Boolean) : BaseAdapterLoadMore<GroupMember>(context, list) {

    override fun getItemViewType(position: Int): Int {
        return if (list[position] == null) {
            R.layout.progress_loading
        } else {
            if (list[position]?.isHeader!!) {
                R.layout.header_simple_1
            } else {
                R.layout.activity_like_row_expand
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(viewType, parent, false)

        return when (viewType) {
            R.layout.header_simple_1 -> HeaderViewHolder(view)
            R.layout.activity_like_row_expand -> ItemViewHolder(view)
            else -> LoadingViewHolder(view)
        }
    }

    override fun configure(item: GroupMember, holder: RecyclerView.ViewHolder) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (item.isHeader) {
            holder.itemView.txtTitle.text = item.userName
        } else {
            val avatarSize = context.resources.getDimensionPixelSize(R.dimen.avatar_size_list_item)
            if (!TextUtils.isEmpty(item.avatarSmall)) {
                Glide.with(context)
                        .load(item.avatarSmall)
                        .placeholder(R.drawable.ic_no_avatar)
                        .override(avatarSize, avatarSize)
                        .transform(GlideCircleTransform(context))
                        .into(holder.itemView.imageView1)
            }

            holder.itemView.textView1.text = item.userName
            if (!item.fullName.isNullOrEmpty()) {
                holder.itemView.textView2.text = item.fullName
                holder.itemView.textView2.visibility = View.VISIBLE
            } else {
                holder.itemView.textView2.visibility = View.GONE
            }


            holder.itemView.imageView1.setOnClickListener(View.OnClickListener { MyUtils.gotoProfile(item.userName, context) })
            holder.itemView.linear1.setOnClickListener(View.OnClickListener { holder.itemView.imageView1.performClick() })


            //thong tin user da like
            holder.itemView.button1.setOnCheckedChangeListener(null)
            setStateButton(item.userId, holder.itemView.button1)
            holder.itemView.button1.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
                //update server
                Webservices.updateUserFollowingState(item.userId, b).continueWith<Void> { task ->
                    if (task.error == null) {
                        if (task.result != null) {
                            val isOK = task.result as Boolean
                            if (isOK) {

                            }
                        }
                    } else {
                        MyUtils.showToast(context, task.error.message)
                    }
                    null
                }
            })

            //delete
            if (item.userId == ownerId) {
                holder.itemView.imgDelete.visibility = View.GONE
            }else {
                if (canDeleteUser) {
                    holder.itemView.imgDelete.visibility = View.VISIBLE
                    holder.itemView.imgDelete.setOnClickListener {
                        MaterialAlertDialogBuilder(context)
                                .setMessage(R.string.confirm_group_delete_member)
                                .setNegativeButton(R.string.cancel, null)
                                .setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialog, which ->
                                    dialog.dismiss()
                                    groupRemoveMember(item, item.userId)
                                })
                                .show()
                    }
                }
            }
        }

    }

    /**
     * So voi owner la co dang theo doi nguoi nay hay khong
     *
     * @param uLike
     * @param btn
     */
    private fun setStateButton(userId: Long, btn: ToggleButton) {
        //neu user like = owner thi an button
        if (userId == ownerId) {
            btn.visibility = View.INVISIBLE
        } else {
            btn.visibility = View.VISIBLE
            btn.isChecked = MyUtils.isFollowing(userId)
        }
    }

    fun groupRemoveMember(member:GroupMember, userId: Long) {
        if (MyUtils.checkInternetConnection(context)) {
            MyApplication.apiManager.groupRemoveMember(member.groupId, userId,
                    object : Callback<JsonObject?> {
                        override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                            val data = response.body()
                            if (data != null) {
                                val result = Webservices.parseJson(data.toString(), Boolean::class.java, false)
                                if (result != null) {
                                    if (result.errorCode == ReturnResult.SUCCESS) { //da ton tai thi vao
                                        //remove in adapter
                                        removerItem(member)

                                        //load lai danh sach group home
                                        context.sendBroadcast(Intent(Fragment_1_Home_SonTung.ACTION_REFRESH_GROUP))

                                        //thong bao giam member detail+allgroup
                                        val allGroup = Intent(AllGroupActivity.ACTION_REMOVE_MEMBER)
                                        allGroup.putExtra(GroupInfo.GROUP_ID, member.groupId)
                                        context.sendBroadcast(allGroup)

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

}