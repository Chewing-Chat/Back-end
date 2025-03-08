package org.chewing.v1.dto

import org.chewing.v1.model.notification.Notification

data class ExpoMessageDto(
    val to: String,
    val title: String,
    val body: String,
    val data: Map<String, Any> = emptyMap(),
) {

    companion object {
        fun from(notification: Notification): ExpoMessageDto =
            ExpoMessageDto(
                to = notification.pushToken.fcmToken,
                title = notification.friendShip.friendName,
                body = notification.content,
                data = mapOf(
                    "senderId" to notification.friendShip.friendId.id,
                    "senderName" to notification.friendShip.friendName,
                    "type" to notification.type.toLowerCase(),
                    "targetId" to notification.targetId,
                    "profileImage" to notification.profileImage,
                ),
            )
    }
}
