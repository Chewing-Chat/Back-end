package org.chewing.v1

import org.chewing.v1.model.announcement.Announcement
import org.chewing.v1.model.auth.*
import org.chewing.v1.model.chat.log.*
import org.chewing.v1.model.chat.member.ChatRoomMember
import org.chewing.v1.model.chat.member.ChatRoomMemberInfo
import org.chewing.v1.model.chat.message.*
import org.chewing.v1.model.chat.room.ChatNumber
import org.chewing.v1.model.chat.room.ChatRoomInfo
import org.chewing.v1.model.chat.room.ChatSequenceNumber
import org.chewing.v1.model.chat.room.Room
import org.chewing.v1.model.emoticon.EmoticonInfo
import org.chewing.v1.model.emoticon.EmoticonPackInfo
import org.chewing.v1.model.feed.Feed
import org.chewing.v1.model.feed.FeedDetail
import org.chewing.v1.model.feed.FeedInfo
import org.chewing.v1.model.friend.UserSearch
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.media.MediaType
import org.chewing.v1.model.notification.Notification
import org.chewing.v1.model.notification.NotificationType
import org.chewing.v1.model.token.RefreshToken
import org.chewing.v1.model.user.*
import org.chewing.v1.model.user.AccessStatus
import java.time.LocalDateTime

object TestDataFactory {

    fun createUserName(): String = "testUserName"

    fun createJwtToken(): JwtToken = JwtToken.of("accessToken", RefreshToken.of("refreshToken", LocalDateTime.now()))

    fun createUser(userId: UserId): User = User.of(
        userId,
        "name",
        "2000-00-00",
        Media.of(FileCategory.PROFILE, "www.example.com", 0, MediaType.IMAGE_PNG),
        AccessStatus.ACCESS,
        PhoneNumber.of("82", "010-0000-0000"),
        "password",
        "testStatusMessage",
    )

    fun createUserId(): UserId = UserId.of("userId")
    fun createPhoneNumber(): PhoneNumber = PhoneNumber.of("82", "010-0000-0000")
    fun createWrongPhoneNumber(): PhoneNumber = PhoneNumber.of("82", "010-0000-0001")
    fun createNormalMessage(messageId: String, chatRoomId: String): ChatNormalMessage = ChatNormalMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        text = "text",
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
    )

    fun createBombMessage(messageId: String, chatRoomId: String): ChatBombMessage = ChatBombMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        text = "text",
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
        expiredAt = LocalDateTime.now().plusMinutes(1),
    )

    fun createInviteMessage(messageId: String, chatRoomId: String): ChatInviteMessage = ChatInviteMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
        targetUserIds = listOf(UserId.of("target")),
    )

    fun createFileMessage(messageId: String, chatRoomId: String): ChatFileMessage = ChatFileMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
        medias = listOf(Media.of(FileCategory.CHAT, "www.example.com", 0, MediaType.IMAGE_PNG)),
    )

    fun createLeaveMessage(messageId: String, chatRoomId: String): ChatLeaveMessage = ChatLeaveMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
    )

    fun createReadMessage(chatRoomId: String): ChatReadMessage = ChatReadMessage.of(
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
    )

    fun createReplyMessage(messageId: String, chatRoomId: String): ChatReplyMessage = ChatReplyMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
        parentMessageId = "parentMessageId",
        parentMessageText = "parentMessageText",
        parentMessagePage = 1,
        parentMessageType = ChatLogType.REPLY,
        parentSeqNumber = 1,
        type = MessageType.REPLY,
        text = "text",
    )

    fun createDeleteMessage(targetMessageId: String, chatRoomId: String): ChatDeleteMessage = ChatDeleteMessage.of(
        targetMessageId = targetMessageId,
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
    )

    fun createNotification(): Notification {
        return Notification.of(
            createUser(
                UserId.of("userId"),
            ),
            PushToken.of(
                "pushToken",
                "platform",
                PushToken.Provider.ANDROID,
                "deviceId",
            ),
            NotificationType.COMMENT,
            "testId",
            "content",
        )
    }
}
