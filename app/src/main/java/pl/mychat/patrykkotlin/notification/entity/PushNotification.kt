package pl.mychat.patrykkotlin.notification.entity

data class PushNotification(
    val data: NotificationData,
    val to: String
)