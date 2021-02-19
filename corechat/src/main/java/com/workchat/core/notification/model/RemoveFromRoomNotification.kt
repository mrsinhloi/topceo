package com.workchat.core.notification.model

data class RemoveFromRoomNotification(
        var data: RemoveFromRoomData
) : BaseNotification()

data class RemoveFromRoomData(
        var actionType: String,
        var actionData : RemoveFromRoomActionData
)

data class RemoveFromRoomActionData(
        var userId: String,
        var name: String,
        var avatar: String,
        var roomId: String,
        var roomName: String,
        var roomAvatar : String,
        var logId: String
)