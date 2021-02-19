package com.workchat.core.notification.model

data class LoginToChatNotification(
        val data: LoginToChatData
): BaseNotification()

data class LoginToChatData(
        val actionType: String,
        val actionData:LoginToChatActionData
)

data class LoginToChatActionData(
        val userId: Long,
        val name: String,
        val avatar: String,
        val phone: String,
        val email: String,
        val createDate: Long,
        val isView: Boolean,
        val viewDate: Long?
)