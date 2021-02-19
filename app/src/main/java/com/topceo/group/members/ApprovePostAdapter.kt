package com.topceo.group.members

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.topceo.R
import com.topceo.config.MyApplication
import com.topceo.group.GroupDetailActivity
import com.topceo.objects.image.ImageItem
import com.topceo.services.ReturnResult
import com.topceo.services.Webservices
import com.topceo.utils.MyUtils
import com.topceo.viewholders.ViewHolder5_Facebook
import com.topceo.viewholders.ViewHolder6_Instagram_Image
import com.topceo.viewholders.ViewHolder6_Instagram_Video
import com.google.gson.JsonObject
import com.smartapp.loadmore.ItemViewHolder
import com.smartapp.post_like_facebook.BaseAdapter
import com.sonhvp.utilities.standard.isNotBlankAndNotEmpty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ApprovePostAdapter(context: Context, list: ArrayList<ImageItem>) : BaseAdapter<ImageItem>(context, list) {

    private var avatarSize = 0
    private var widthImage = 0

    override fun getItemViewType(position: Int): Int {
        avatarSize = context.resources.getDimensionPixelSize(R.dimen.avatar_size_small)
        widthImage = MyUtils.getScreenWidth(context) // - roundCorner * 2;


        val item = list[position]
        if (item.itemType.equals(ImageItem.ITEM_TYPE_FACEBOOK)) {
            return ImageItem.TYPE_FACEBOOK
        } else {//Instagram
            if (item.isVideo) {
                return ImageItem.TYPE_INSTAGRAM_VIDEO
            } else {
                return ImageItem.TYPE_INSTAGRAM_IMAGE
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.fragment_1_row_basic, parent, false)

        //an tuong tac like,comment,share,menu
        v.findViewById<LinearLayout>(R.id.linearLayout2).visibility = View.GONE
        v.findViewById<ImageView>(R.id.imgMenu2).visibility = View.GONE

        //views
        return when (viewType) {
            ImageItem.TYPE_FACEBOOK -> {
                ViewHolder5_Facebook(v, avatarSize)
            }
            ImageItem.TYPE_INSTAGRAM_VIDEO -> {
                ViewHolder6_Instagram_Video(v, avatarSize, widthImage)
            }
            ImageItem.TYPE_INSTAGRAM_IMAGE -> {
                ViewHolder6_Instagram_Image(v, avatarSize, widthImage)
            }
            else -> ItemViewHolder(v)
        }
    }

    override fun configure(position: Int, holder: RecyclerView.ViewHolder) {
        val item = list[position]
        when (holder) {
            is ViewHolder5_Facebook -> {
                val vHolder = holder as ViewHolder5_Facebook
                vHolder.bindData(item, position, null)
                initUIBottom(item, vHolder.linearAcceptRefuse, vHolder.btnAccept, vHolder.btnRefuse)
            }
            is ViewHolder6_Instagram_Video -> {
                val vHolder = holder as ViewHolder6_Instagram_Video
                vHolder.bindData(item, position, null)
                initUIBottom(item, vHolder.linearAcceptRefuse, vHolder.btnAccept, vHolder.btnRefuse)
            }
            is ViewHolder6_Instagram_Image -> {
                val vHolder = holder as ViewHolder6_Instagram_Image
                vHolder.bindData(item, position, null)
                initUIBottom(item, vHolder.linearAcceptRefuse, vHolder.btnAccept, vHolder.btnRefuse)
            }
            is ItemViewHolder -> {
            }
        }


    }

    fun initUIBottom(item: ImageItem, linearAcceptRefuse: LinearLayout, btnAccept: Button, btnRefuse: Button) {
        linearAcceptRefuse.visibility = View.VISIBLE
        btnAccept.setOnClickListener { approve(item.itemId, item, true) }
        btnRefuse.setOnClickListener { approve(item.itemId, item, false) }
    }

    fun approve(itemId: Long, item:ImageItem, isApprove:Boolean) {
        MyApplication.apiManager.groupPendingPostAccept(itemId, isApprove,
                object : Callback<JsonObject?> {
                    override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                        whenSuccess(response.body(), item)
                    }

                    override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                        MyUtils.log("error")
                    }
                })

    }

    private fun whenSuccess(data: JsonObject?, item:ImageItem) {
        if (data != null) {
            val result = Webservices.parseJson(data.toString(), Boolean::class.java, false)
            if (result != null) {
                if (result.errorCode == ReturnResult.SUCCESS) {
                    //duyet xong thi xoa khoi nhom
                    removerItem(item)
                    //refresh man hinh group
                    context.sendBroadcast(Intent(GroupDetailActivity.ACTION_REFRESH_DETAIL_GROUP))

                }else{
                    val message = result.errorMessage
                    if(message.isNotBlankAndNotEmpty()){
                        MyUtils.showAlertDialog(context, message)
                    }
                }
            }
        }
    }
}