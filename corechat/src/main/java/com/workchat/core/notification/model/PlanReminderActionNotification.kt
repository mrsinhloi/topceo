package com.workchat.core.notification.model

data class PlanReminderActionNotification(
        val data: PlanReminderActionData
) : BaseNotification()

data class PlanReminderActionData(
        val actionType: String,
        val actionData: ReminderActionData
)

data class ReminderActionData(
        val roomId: String,
        val roomName: String,
        val roomAvatar: String = "",
        val chatLogId: String,
        val planName: String,
        val timeStamp: Long
)