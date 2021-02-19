package com.workchat.core.notification.items

import com.workchat.core.notification.model.BaseNotification

data class AddToRoomNotification (
        var data: AddToRoomData
) : BaseNotification()  /*{

   fun makeNotification(context: Context, ntfId: Int) {
        val view = RemoteViews(context.packageName, R.layout.ntf_add_contact).apply {
            setTextViewText(R.id.ntfContact_content, ("${data.actionData.name} ${context.getString(R.string.ntf_added_to_room)}"))
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
            GlideApp.with(context).asBitmap().load(context.textBitmap(data.actionData.name.first().toString())).circleCrop().into(object : NotificationTarget(context, R.id.ntfContact_img, view, notification, ntfId) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    super.onResourceReady(resource, transition)
                    NotificationManagerCompat.from(context).notify(ntfId, notification)
                }
            })
        } else {
            GlideApp.with(context).asBitmap().load(data.actionData.avatar).circleCrop().into(object : NotificationTarget(context, R.id.ntfContact_img, view, notification, ntfId) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    super.onResourceReady(resource, transition)
                    NotificationManagerCompat.from(context).notify(ntfId, notification)
                }
            })
        }
    }

}*/

data class AddToRoomData(
        val actionType: String,
        val actionData: AddToRoomActionData
)

data class AddToRoomActionData (
        var userId: String,
        var name: String,
        var avatar: String?,
        var roomId: String,
        var roomName: String,
        var roomAvatar : String
)
