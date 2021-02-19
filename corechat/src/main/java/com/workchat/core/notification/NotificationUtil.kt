package com.workchat.core.notification

import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.workchat.core.chat.ChatActivity
import com.workchat.core.mbn.models.UserChatCore
import com.workchat.core.models.realm.Room
import com.workchat.core.notification.items.AddContactNotification
import com.workchat.core.notification.items.AddToRoomNotification
import com.workchat.core.notification.model.*
import org.json.JSONObject

fun JSONObject.toNotification(): BaseNotification? {
    runCatching {
        return@runCatching when (getString("type")) {
            "action" -> {
                when (optJSONObject("data").optString("actionType")) {
                    "addToRoom" -> Gson().fromJson(toString(), AddToRoomNotification::class.java)
                    "addToContact" -> Gson().fromJson(toString(), AddContactNotification::class.java)
                    "removeFromRoom" -> Gson().fromJson(toString(), RemoveFromRoomNotification::class.java)
                    "newToChatNhanh" -> Gson().fromJson(toString(), NewToChatNotification::class.java)
                    "loginChatNhanh" -> Gson().fromJson(toString(), LoginToChatNotification::class.java)
                    "closePoll" -> Gson().fromJson(toString(), PollActionNotification::class.java)
                    "planReminder" -> Gson().fromJson(toString(), PlanReminderActionNotification::class.java)
                    "deleteRoom" -> Gson().fromJson(toString(), RemoveFromRoomNotification::class.java)
                    "deleteChannel" -> Gson().fromJson(toString(), RemoveFromRoomNotification::class.java)


                    else -> null
                }
            }
            else -> null
        }
    }.run {
        return when {
            isSuccess -> getOrNull()
            else -> {
                //exceptionOrNull()?.printStackTrace()
                Logger.d("toNotification() not success")
                null
            }
        }
    }
}

infix fun Context.navigation(notification: BaseNotification) {
    when (notification) {
        is AddContactNotification -> {
            if (ChatActivity.isExists) {
                sendBroadcast(Intent(ChatActivity.FINISH_ACTIVITY))
            }
            val data = UserChatCore().apply { _id = notification.createUserId }
            val intent = Intent(this, ChatActivity::class.java).apply { putExtra(UserChatCore.USER_MODEL, data) }
            startActivity(intent)
        }
        is AddToRoomNotification -> {
            if (ChatActivity.isExists) {
                sendBroadcast(Intent(ChatActivity.FINISH_ACTIVITY))
            }
            val intent = Intent(this, ChatActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(Room.ROOM_ID, notification.data.actionData.roomId)
            }
            startActivity(intent)
        }
        else -> {
        }
    }
}