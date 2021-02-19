package com.workchat.core.notification.items

import android.content.Context
import android.content.Intent
import android.os.Build
import com.bumptech.glide.Glide
import com.workchat.core.chat.ChatActivity
import com.workchat.core.mbn.models.UserChatCore
import com.workchat.core.notification.model.NewToChatNotification
import com.workchat.core.utils.toDateTimeStr
import com.workchat.corechat.R
import kotlinx.android.synthetic.main.ntf_base_item.view.*

class NewToChatItem (context: Context, private val ntf: NewToChatNotification) : BaseNotificationItem(context, ntf) {

    override val layoutRes: Int = R.layout.ntf_base_item
    override val type: Int = 6

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        holder.itemView.apply {
            ntfBase_title.apply {
                text = ntf.message
                when (notify.isView) {
                    true -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) setTextAppearance(R.style.PaymentTitleNormal) else setTextAppearance(context, R.style.PaymentTitleNormal)
                    false -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) setTextAppearance(R.style.PaymentTitleBold) else setTextAppearance(context, R.style.PaymentTitleBold)
                }
            }
            if (ntf.data.actionData.avatar.contains("no-avatar.png") || ntf.data.actionData.avatar.isNullOrEmpty()) {
                Glide.with(this).asBitmap().circleCrop().load(textBitmap(ntf.data.actionData.name.first().toString().capitalize())).into(ntfBase_avatar)
            } else {
                Glide.with(this).asBitmap().circleCrop().load(ntf.data.actionData.avatar).into(ntfBase_avatar)
            }
            ntfBase_time.text = ntf.createDate.toDateTimeStr()
        }
    }

    override fun navigationFrom(context: Context) {
        context.run {
            if (ChatActivity.isExists) {
                sendBroadcast(Intent(ChatActivity.FINISH_ACTIVITY))
            }
            val data = UserChatCore().apply { _id = ntf.createUserId }
            val intent = Intent(this, ChatActivity::class.java).apply { putExtra(UserChatCore.USER_MODEL, data) }
            startActivity(intent)
        }
    }

}