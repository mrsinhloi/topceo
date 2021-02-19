package com.workchat.core.notification.items

import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.NotificationTarget
import com.bumptech.glide.request.transition.Transition
import com.sonhvp.utilities.content.notification
import com.sonhvp.utilities.content.notificationAttr
import com.sonhvp.utilities.content.notificationChannel
import com.workchat.core.notification.model.BaseNotification
import com.workchat.core.utils.toDateTimeStr
import com.workchat.corechat.R

data class AddContactNotification(var data: AddContactData) : BaseNotification() {

    fun makeNotification(context: Context, ntfId: Int) {
        val view = RemoteViews(context.packageName, R.layout.ntf_add_contact).apply {
            setTextViewText(R.id.ntfContact_content, ("${data.actionData.name} ${context.getString(R.string.ntf_added_to_contact)}"))
            setTextViewText(R.id.ntfContact_time, createDate.toDateTimeStr())
        }
        val ntfAttr = notificationAttr {
            id = ntfId
            priority = NotificationCompat.PRIORITY_HIGH
            smallIcon = R.mipmap.ic_launcher_round
            content = view
            autoCancel = true
        }

        context.notificationChannel {
            id = "LUVAPAY_PAYMENTS"
            name = "LuvaPay Payments"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                importance = NotificationManager.IMPORTANCE_HIGH
            }
        }
        val notification = context.notification(
                "LUVAPAY_PAYMENTS",
                ntfAttr
        )

        if (data.actionData.avatar == null) {
            Glide.with(context).asBitmap().load(context.textBitmap(data.actionData.name.first().toString())).circleCrop().into(object : NotificationTarget(context, R.id.ntfContact_img, view, notification, ntfId) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    super.onResourceReady(resource, transition)
                    NotificationManagerCompat.from(context).notify(ntfId, notification)
                }
            })
        } else {
            Glide.with(context).asBitmap().load(data.actionData.avatar).circleCrop().into(object : NotificationTarget(context, R.id.ntfContact_img, view, notification, ntfId) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    super.onResourceReady(resource, transition)
                    NotificationManagerCompat.from(context).notify(ntfId, notification)
                }
            })
        }
    }

}


data class AddContactData(
        var actionType: String,
        var actionData: AddContactActionData
)

data class AddContactActionData(
        var userId: String,
        var name: String,
        var avatar: String,
        var phone: String,
        var email: String,
        var url: String
)