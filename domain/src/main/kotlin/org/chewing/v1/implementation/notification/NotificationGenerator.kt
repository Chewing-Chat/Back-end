package org.chewing.v1.implementation.notification

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.notification.PushInfo
import org.chewing.v1.model.chat.message.*
import org.chewing.v1.model.chat.room.ChatRoomType.*
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.notification.Notification
import org.chewing.v1.model.notification.NotificationInfo
import org.chewing.v1.model.notification.NotificationType
import org.chewing.v1.model.schedule.ScheduleAction
import org.chewing.v1.model.schedule.ScheduleId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class NotificationGenerator {

    private val logger = LoggerFactory.getLogger(NotificationGenerator::class.java)

    fun generateMessageNotifications(
        notificationInfos: List<NotificationInfo>,
        message: ChatMessage,
    ): List<Notification> {
        return notificationInfos.flatMap { generateMessageNotification(it, message) }
    }
    fun generateMessageNotification(
        notificationInfo: NotificationInfo,
        message: ChatMessage,
    ): List<Notification> {
        val (type, targetId, content) = when (message) {
            is ChatFileMessage -> {
                val content = "사진을 보냈습니다." // val mediaUrl = message.medias.first().url
                when (message.chatRoomType) {
                    DIRECT -> Triple(NotificationType.DIRECT_CHAT_FILE, message.chatRoomId, content)
                    GROUP -> Triple(NotificationType.GROUP_CHAT_FILE, message.chatRoomId, content)
                    AI -> throw ConflictException(ErrorCode.AI_NOTIFICATION_NOT_SUPPORTED)
                }
            }

            is ChatNormalMessage -> {
                when (message.chatRoomType) {
                    DIRECT -> Triple(NotificationType.DIRECT_CHAT_NORMAL, message.chatRoomId, message.text)
                    GROUP -> Triple(NotificationType.GROUP_CHAT_NORMAL, message.chatRoomId, message.text)
                    AI -> throw ConflictException(ErrorCode.AI_NOTIFICATION_NOT_SUPPORTED)
                }
            }

            is ChatInviteMessage -> {
                Triple(NotificationType.GROUP_CHAT_INVITE, message.chatRoomId, "${notificationInfo.friendShip.friendName}님이 초대했습니다.")
            }

            is ChatLeaveMessage -> {
                Triple(NotificationType.GROUP_CHAT_LEAVE, message.chatRoomId, "${notificationInfo.friendShip.friendName}님이 나갔습니다.")
            }

            is ChatReplyMessage -> {
                when (message.chatRoomType) {
                    DIRECT -> Triple(NotificationType.DIRECT_CHAT_REPLY, message.chatRoomId, message.text)
                    GROUP -> Triple(NotificationType.GROUP_CHAT_REPLY, message.chatRoomId, message.text)
                    AI -> throw ConflictException(ErrorCode.AI_NOTIFICATION_NOT_SUPPORTED)
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
            friendShip = notificationInfo.friendShip,
            pushInfos = notificationInfo.pushInfos,
            type = type,
            targetId = targetId.id,
            content = content,
            profileImage = notificationInfo.user.image.url,
        )
    }

    fun generateScheduleNotifications(
        notificationInfos: List<NotificationInfo>,
        scheduleId: ScheduleId,
        scheduleAction: ScheduleAction,
    ): List<Notification> {
        return notificationInfos.flatMap { generateScheduleNotification(it, scheduleId, scheduleAction) }
    }

    fun generateScheduleNotification(
        notificationInfo: NotificationInfo,
        scheduleId: ScheduleId,
        scheduleAction: ScheduleAction,
    ): List<Notification> {
        val (type, targetId, content) = when (scheduleAction) {
            ScheduleAction.CREATED -> Triple(
                NotificationType.SCHEDULE_CREATE,
                scheduleId,
                "${notificationInfo.friendShip.friendName}님이 일정을 생성했습니다.",
            )

            ScheduleAction.CANCELED -> Triple(
                NotificationType.SCHEDULE_CANCEL,
                scheduleId,
                "${notificationInfo.friendShip.friendName}님이 일정을 취소했습니다.",
            )

            ScheduleAction.DELETED -> Triple(
                NotificationType.SCHEDULE_DELETE,
                scheduleId,
                "${notificationInfo.friendShip.friendName}님이 일정을 삭제했습니다.",
            )

            ScheduleAction.UPDATED -> Triple(
                NotificationType.SCHEDULE_UPDATE,
                scheduleId,
                "${notificationInfo.friendShip.friendName}님이 일정을 변경했습니다.",
            )
        }

        return createNotifications(
            friendShip = notificationInfo.friendShip,
            pushInfos = notificationInfo.pushInfos,
            type = type,
            targetId = targetId.id,
            content = content,
            profileImage = notificationInfo.user.image.url,
        )
    }

    private fun createNotifications(
        friendShip: FriendShip,
        pushInfos: List<PushInfo>,
        type: NotificationType,
        targetId: String,
        content: String,
        profileImage: String,
    ): List<Notification> = pushInfos.map { pushToken ->
        Notification.of(
            friendShip = friendShip,
            pushInfo = pushToken,
            type = type,
            targetId = targetId,
            content = content,
            profileImage = profileImage,
        )
    }
}
