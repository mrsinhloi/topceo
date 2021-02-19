package com.topceo.group.members

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.topceo.R
import com.topceo.config.MyApplication
import com.topceo.fragments.GlideCircleTransform
import com.topceo.group.GroupDetailActivity
import com.topceo.group.models.GroupMember
import com.topceo.services.ReturnResult
import com.topceo.services.Webservices
import com.topceo.utils.MyUtils
import com.google.gson.JsonObject
import com.smartapp.loadmore.ItemViewHolder
import com.smartapp.post_like_facebook.BaseAdapter
import kotlinx.android.synthetic.main.activity_like_row_expand.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class InviteMemberAdapter(context: Context, list: ArrayList<GroupMember>, val ownerId: Long, val groupId: Long) : BaseAdapter<GroupMember>(context, list) {

    override fun getItemViewType(position: Int): Int {
        return R.layout.activity_like_row_expand
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
                .into(holder.itemView.imageView1)

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
        setStateButton(item, holder.itemView.button1)
        if (isInvited(item)) {
            holder.itemView.button1.setOnClickListener { holder.itemView.linear1.performClick() }
            holder.itemView.button1.setOnCheckedChangeListener { buttonView, isChecked ->
                holder.itemView.button1.isChecked = true
            }
        } else {
            holder.itemView.button1.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
                item.isInvited = b
                list[position].isInvited = b

                //neu chua moi thi moi goi moi, da moi thi ko the cancel
                if (!listInvited.contains(item.userId)) {
                    listInvited.add(item.userId)

                    //update server
                    inviteUser(groupId, item.userId)
                }

                //set lai listener
                notifyItemChanged(position)

            })
        }


    }

    /**
     * So voi owner la co dang theo doi nguoi nay hay khong
     *
     * @param uLike
     * @param btn
     */
    private fun setStateButton(user: GroupMember, btn: ToggleButton) {
        btn.textOn = context.getText(R.string.invited)
        btn.textOff = context.getText(R.string.invite)

        //neu user like = owner thi an button
        if (user.userId == ownerId) {
            btn.visibility = View.INVISIBLE
        } else {
            btn.visibility = View.VISIBLE
            btn.isChecked = isInvited(user)
        }


    }

    private fun isInvited(user: GroupMember): Boolean {
        return user.isInvited || listInvited.contains(user.userId)
    }

    var listInvited = ArrayList<Long>()
    fun inviteUser(groupId: Long, userId: Long) {
        MyApplication.apiManager.groupMemberInvite(groupId, userId,
                object : Callback<JsonObject?> {
                    override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                        val data = response.body()
                        if (data != null) {
                            val result = Webservices.parseJson(data.toString(), Boolean::class.java, false)
                            if (result != null) {
                                if (result.errorCode == ReturnResult.SUCCESS) { //da ton tai thi vao
//                                    MyUtils.showAlertDialog(context, "OK")

                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                        MyUtils.log("error")
                    }
                })

    }

    //luu tam khi destroy man hinh search
    fun saveListInvited() {
        if (listInvited.size > 0) {
            GroupDetailActivity.mapInvited[groupId] = listInvited
        }
    }

    //restore khi vao man hinh search
    fun restoreListInvited() {
        val list = GroupDetailActivity.mapInvited[groupId]
        if (list != null && list.size > 0) {
            listInvited = list
        }

    }

}