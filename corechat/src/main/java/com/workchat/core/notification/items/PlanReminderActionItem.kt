package com.workchat.core.notification.items

import android.content.Context
import android.content.Intent
import android.os.Build
import com.bumptech.glide.Glide
import com.workchat.core.chat.ChatActivity
import com.workchat.core.models.chat.RoomLog
import com.workchat.core.models.realm.Room
import com.workchat.core.notification.model.PlanReminderActionNotification
import com.workchat.core.utils.toDateTimeStr
import com.workchat.corechat.R
import kotlinx.android.synthetic.main.ntf_base_item.view.*

class PlanReminderActionItem(context: Context, private val ntf: PlanReminderActionNotification) : BaseNotificationItem(context, ntf) {

    override val layoutRes: Int = R.layout.ntf_base_item
    override val type: Int = 8

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        try {
            holder.itemView.apply {
                ntfBase_title.apply {
                    text = ntf.message
                    when (notify.isView) {
                        true -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) setTextAppearance(R.style.PaymentTitleNormal) else setTextAppearance(context, R.style.PaymentTitleNormal)
                        false -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) setTextAppearance(R.style.PaymentTitleBold) else setTextAppearance(context, R.style.PaymentTitleBold)
                    }
                }
                if (ntf.data.actionData.roomAvatar==null || ntf.data.actionData.roomAvatar.contains("no-avatar.png")) {
                    Glide.with(this)
                            .asBitmap()
                            .circleCrop()
                            .load(textBitmap(ntf.data.actionData.roomName.first().toString().capitalize()))
                            .into(ntfBase_avatar)
                } else {
                    Glide.with(this)
                            .asBitmap()
                            .circleCrop()
                            .load(ntf.data.actionData.roomAvatar)
                            .into(ntfBase_avatar)
                }
                ntfBase_time.text = ntf.createDate.toDateTimeStr()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.itemView.apply {
            ntfBase_title.text = null
            ntfBase_time.text = null
            if (context != null) Glide.with(context).clear(ntfBase_avatar)
        }
    }

    override fun navigationFrom(context: Context) {
        context.run {
            if (ChatActivity.isExists) {
                sendBroadcast(Intent(ChatActivity.FINISH_ACTIVITY))
            }
            val intent = Intent(this, ChatActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(Room.ROOM_ID, ntf.data.actionData.roomId)
                putExtra(RoomLog.ROOM_LOG_ID, ntf.data.actionData.chatLogId)
            }
            startActivity(intent)
        }
    }

}