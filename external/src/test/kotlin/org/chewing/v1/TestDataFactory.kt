package org.chewing.v1

import org.chewing.v1.model.auth.*
import org.chewing.v1.model.chat.log.*
import org.chewing.v1.model.chat.message.*
import org.chewing.v1.model.contact.LocalPhoneNumber
import org.chewing.v1.model.contact.PhoneNumber
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.media.MediaType
import org.chewing.v1.model.token.RefreshToken
import org.chewing.v1.model.user.*
import org.chewing.v1.model.user.AccessStatus
import java.time.LocalDateTime

object TestDataFactory {

    fun createUserName(): String = "testUserName"

    fun createJwtToken(): JwtToken = JwtToken.of("accessToken", RefreshToken.of("refreshToken", LocalDateTime.now()))

    fun createUser(userId: UserId): UserInfo = UserInfo.of(
        userId,
        "name",
        Media.of(FileCategory.PROFILE, "www.example.com", 0, MediaType.IMAGE_PNG),
        AccessStatus.ACCESS,
        PhoneNumber.of("testPhoneNumber"),
        "password",
        "testStatusMessage",
    )

    fun createUserInfo(accessStatus: AccessStatus): UserInfo {
        return UserInfo.of(
            UserId.of("testUserId"),
            "testUserName",
            Media.of(FileCategory.PROFILE, "www.example.com", 0, MediaType.IMAGE_PNG),
            accessStatus,
            PhoneNumber.of("testPhoneNumber"),
            "testPassword",
            "testStatusMessage",
        )
    }

    fun createUser(accessStatus: AccessStatus): User {
        return User.of(
            createUserInfo(accessStatus),
            LocalPhoneNumber.of(
                "82",
                "01012345678",
            ),
        )
    }

    fun createUserId(): UserId = UserId.of("userId")
    fun createPhoneNumber(): PhoneNumber = PhoneNumber.of("testPhoneNumber")
    fun createWrongPhoneNumber(): PhoneNumber = PhoneNumber.of("testWrongPhoneNumber")
//    fun createNormalMessage(messageId: String, chatRoomId: ChatRoomId): ChatNormalMessage = ChatNormalMessage.of(
//        messageId = messageId,
//        chatRoomId = chatRoomId,
//        senderId = UserId.of("sender"),
//        text = "text",
//        number = ChatRoomSequence.of(chatRoomId, 1),
//        timestamp = LocalDateTime.now(),
//    )
//
//    fun createInviteMessage(messageId: String, chatRoomId: ChatRoomId): ChatInviteMessage = ChatInviteMessage.of(
//        messageId = messageId,
//        chatRoomId = chatRoomId,
//        senderId = UserId.of("sender"),
//        number = ChatRoomSequence.of(chatRoomId, 1),
//        timestamp = LocalDateTime.now(),
//        targetUserIds = listOf(UserId.of("target")),
//    )
//
//    fun createFileMessage(messageId: String, chatRoomId: ChatRoomId): ChatFileMessage = ChatFileMessage.of(
//        messageId = messageId,
//        chatRoomId = chatRoomId,
//        senderId = UserId.of("sender"),
//        number = ChatRoomSequence.of(chatRoomId, 1),
//        timestamp = LocalDateTime.now(),
//        medias = listOf(Media.of(FileCategory.CHAT, "www.example.com", 0, MediaType.IMAGE_PNG)),
//    )
//
//    fun createLeaveMessage(messageId: String, chatRoomId: ChatRoomId): ChatLeaveMessage = ChatLeaveMessage.of(
//        messageId = messageId,
//        chatRoomId = chatRoomId,
//        senderId = UserId.of("sender"),
//        number = ChatRoomSequence.of(chatRoomId, 1),
//        timestamp = LocalDateTime.now(),
//    )
//
//    fun createReadMessage(chatRoomId: ChatRoomId): ChatReadMessage = ChatReadMessage.of(
//        chatRoomId = chatRoomId,
//        senderId = UserId.of("sender"),
//        number = ChatRoomSequence.of(chatRoomId, 1),
//        timestamp = LocalDateTime.now(),
//    )
//
//    fun createReplyMessage(messageId: String, chatRoomId: ChatRoomId): ChatReplyMessage = ChatReplyMessage.of(
//        messageId = messageId,
//        chatRoomId = chatRoomId,
//        senderId = UserId.of("sender"),
//        number = ChatRoomSequence.of(chatRoomId, 1),
//        timestamp = LocalDateTime.now(),
//        parentMessageId = "parentMessageId",
//        parentMessageText = "parentMessageText",
//        parentMessageType = ChatLogType.REPLY,
//        parentSeqNumber = 1,
//        type = MessageType.REPLY,
//        text = "text",
//    )
//
//    fun createDeleteMessage(targetMessageId: String, chatRoomId: ChatRoomId): ChatDeleteMessage = ChatDeleteMessage.of(
//        targetMessageId = targetMessageId,
//        chatRoomId = chatRoomId,
//        senderId = UserId.of("sender"),
//        number = ChatRoomSequence.of(chatRoomId, 1),
//        timestamp = LocalDateTime.now(),
//    )
//
//    fun createNotification(): Notification {
//        return Notification.of(
//            createUserInfo(AccessStatus.ACCESS),
//            PushToken.of(
//                "pushToken",
//                "platform",
//                PushToken.Provider.ANDROID,
//                "deviceId",
//            ),
//            NotificationType.COMMENT,
//            "testId",
//            "content",
//        )
//    }
}
