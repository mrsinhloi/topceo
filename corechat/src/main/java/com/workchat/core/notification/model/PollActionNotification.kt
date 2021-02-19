package com.workchat.core.notification.model

data class PollActionNotification(
        val data: PollActionData
): BaseNotification()

data class PollActionData(
        val actionType: String,
        val actionData:ActionData
)

data class ActionData(
        val roomId: String,
        val roomName: String,
        val roomAvatar: String,
        val chatLogId: String,
        val pollTitle: String,
        val timeStamp: Long
)