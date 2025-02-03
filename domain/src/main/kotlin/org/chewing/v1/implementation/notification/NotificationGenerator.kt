package org.chewing.v1.implementation.notification

import org.chewing.v1.model.auth.PushToken
import org.chewing.v1.model.chat.message.*
import org.chewing.v1.model.notification.Notification
import org.chewing.v1.model.notification.NotificationType
import org.chewing.v1.model.user.UserInfo
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class NotificationGenerator {

    private val logger = LoggerFactory.getLogger(NotificationGenerator::class.java)

    fun generateCommentNotification(
        sourceUserInfo: UserInfo,
        pushTokens: List<PushToken>,
        feedId: String,
        comment: String,
    ): List<Notification> = createNotifications(
        sourceUserInfo = sourceUserInfo,
        pushTokens = pushTokens,
        type = NotificationType.COMMENT,
        targetId = feedId,
        content = comment,
    )

    fun generateMessageNotification(
        sourceUserInfo: UserInfo,
        pushTokens: List<PushToken>,
        message: ChatMessage,
    ): List<Notification> {
        val (type, targetId, content) = when (message) {
            is ChatFileMessage -> {
                val mediaUrl = message.medias.first().url
                Triple(NotificationType.CHAT_FILE, message.chatRoomId, mediaUrl)
            }
            is ChatNormalMessage -> {
                Triple(NotificationType.CHAT_NORMAL, message.chatRoomId, message.text)
            }
            is ChatInviteMessage -> {
                Triple(NotificationType.CHAT_INVITE, message.chatRoomId, null)
            }
            is ChatLeaveMessage -> {
                Triple(NotificationType.CHAT_LEAVE, message.chatRoomId, null)
            }
            is ChatReplyMessage -> {
                Triple(NotificationType.CHAT_REPLY, message.chatRoomId, message.text)
            }
            else -> {
                logger.warn("지원하지 않는 메시지 타입입니다. message: $message")
                return emptyList()
            }
        }

        return createNotifications(
            sourceUserInfo = sourceUserInfo,
            pushTokens = pushTokens,
            type = type,
            targetId = targetId.id,
            content = content,
        )
    }

    private fun createNotifications(
        sourceUserInfo: UserInfo,
        pushTokens: List<PushToken>,
        type: NotificationType,
        targetId: String,
        content: String?,
    ): List<Notification> = pushTokens.map { pushToken ->
        Notification.of(
            userInfo = sourceUserInfo,
            pushToken = pushToken,
            type = type,
            targetId = targetId,
            content = content ?: "",
        )
    }
}
