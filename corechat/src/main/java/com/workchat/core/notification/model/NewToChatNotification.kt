package com.workchat.core.notification.model

data class NewToChatNotification(
        val data: NewToChatData
): BaseNotification()

data class NewToChatData(
        val actionType: String,
        val actionData: NewToChatActionData
)

data class NewToChatActionData(
        val userId: Long,
        val name: String,
        val avatar: String,
        val phone: String,
        val email: String,
        val createDate: Long,
        val isView: Boolean,
        val viewDate: Long?
)