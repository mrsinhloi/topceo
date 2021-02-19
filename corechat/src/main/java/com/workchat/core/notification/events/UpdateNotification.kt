package com.workchat.core.notification.events

data class UpdateNotification (
        var id: MutableList<String>,
        var isViewed: Boolean
)