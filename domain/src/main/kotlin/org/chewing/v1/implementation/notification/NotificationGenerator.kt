package org.chewing.v1.implementation.notification

import org.chewing.v1.model.auth.PushToken
import org.chewing.v1.model.chat.message.*
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.notification.Notification
import org.chewing.v1.model.notification.NotificationType
import org.chewing.v1.model.schedule.ScheduleAction
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.user.UserInfo
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class NotificationGenerator {

    private val logger = LoggerFactory.getLogger(NotificationGenerator::class.java)

    fun generateMessageNotification(
        friendShip: FriendShip,
        pushTokens: List<PushToken>,
        message: ChatMessage,
        user: UserInfo,
    ): List<Notification> {
        val (type, targetId, content) = when (message) {
            is ChatFileMessage -> {
                val mediaUrl = message.medias.first().url
                when (message.chatRoomType) {
                    ChatRoomType.DIRECT -> Triple(NotificationType.DIRECT_CHAT_FILE, message.chatRoomId, mediaUrl)
                    ChatRoomType.GROUP -> Triple(NotificationType.GROUP_CHAT_FILE, message.chatRoomId, mediaUrl)
                }
            }

            is ChatNormalMessage -> {
                when (message.chatRoomType) {
                    ChatRoomType.DIRECT -> Triple(NotificationType.DIRECT_CHAT_NORMAL, message.chatRoomId, message.text)
                    ChatRoomType.GROUP -> Triple(NotificationType.GROUP_CHAT_NORMAL, message.chatRoomId, message.text)
                }
            }

            is ChatInviteMessage -> {
                Triple(NotificationType.GROUP_CHAT_INVITE, message.chatRoomId, "${friendShip.friendName}님이 초대했습니다.")
            }

            is ChatLeaveMessage -> {
                Triple(NotificationType.GROUP_CHAT_LEAVE, message.chatRoomId, "${friendShip.friendName}님이 나갔습니다.")
            }

            is ChatReplyMessage -> {
                when (message.chatRoomType) {
                    ChatRoomType.DIRECT -> Triple(NotificationType.DIRECT_CHAT_REPLY, message.chatRoomId, message.text)
                    ChatRoomType.GROUP -> Triple(NotificationType.GROUP_CHAT_REPLY, message.chatRoomId, message.text)
                }
            }

            is ChatCommentMessage -> {
                Triple(NotificationType.DIRECT_CHAT_COMMENT, message.chatRoomId, message.comment)
            }

            else -> {
                logger.warn("지원하지 않는 메시지 타입입니다. message: $message")
                return emptyList()
            }
        }

        return createNotifications(
            friendShip = friendShip,
            pushTokens = pushTokens,
            type = type,
            targetId = targetId.id,
            content = content,
            profileImage = user.image.url,
        )
    }

    fun generateScheduleNotification(
        friendShip: FriendShip,
        pushTokens: List<PushToken>,
        scheduleId: ScheduleId,
        user: UserInfo,
        scheduleAction: ScheduleAction,
    ): List<Notification> {
        val (type, targetId, content) = when (scheduleAction) {
            ScheduleAction.CREATED -> Triple(
                NotificationType.SCHEDULE_CREATE,
                scheduleId,
                "${friendShip.friendName}님이 일정을 생성했습니다.",
            )

            ScheduleAction.CANCELED -> Triple(
                NotificationType.SCHEDULE_CANCEL,
                scheduleId,
                "${friendShip.friendName}님이 일정을 취소했습니다.",
            )

            ScheduleAction.DELETED -> Triple(
                NotificationType.SCHEDULE_DELETE,
                scheduleId,
                "${friendShip.friendName}님이 일정을 삭제했습니다.",
            )

            ScheduleAction.UPDATED -> Triple(
                NotificationType.SCHEDULE_UPDATE,
                scheduleId,
                "${friendShip.friendName}님이 일정을 변경했습니다.",
            )
        }

        return createNotifications(
            friendShip = friendShip,
            pushTokens = pushTokens,
            type = type,
            targetId = targetId.id,
            content = content,
            profileImage = user.image.url,
        )
    }

    private fun createNotifications(
        friendShip: FriendShip,
        pushTokens: List<PushToken>,
        type: NotificationType,
        targetId: String,
        content: String,
        profileImage: String,
    ): List<Notification> = pushTokens.map { pushToken ->
        Notification.of(
            friendShip = friendShip,
            pushToken = pushToken,
            type = type,
            targetId = targetId,
            content = content,
            profileImage = profileImage,
        )
    }
}
