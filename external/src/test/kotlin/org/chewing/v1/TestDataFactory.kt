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

    fun createUser(userId: String): User = User.of(
        userId,
        "name",
        "2000-00-00",
        Media.of(FileCategory.PROFILE, "www.example.com", 0, MediaType.IMAGE_PNG),
        AccessStatus.ACCESS,
        PhoneNumber.of("82", "010-0000-0000"),
        "password",
        "testStatusMessage",
    )
    fun createPhoneNumber(): PhoneNumber = PhoneNumber.of("82", "010-0000-0000")
    fun createWrongPhoneNumber(): PhoneNumber = PhoneNumber.of("82", "010-0000-0001")
    fun createFeedInfo(feedId: String, userId: String): FeedInfo =
        FeedInfo.of(feedId, "topic", LocalDateTime.now(), userId)

    private fun createFeedMedia(index: Int): Media =
        Media.of(FileCategory.FEED, "www.example.com", index, MediaType.IMAGE_PNG)

    fun createFeedDetail(feedId: String, feedDetailId: String, index: Int): FeedDetail =
        FeedDetail.of(feedDetailId, createFeedMedia(index), feedId)

    fun createAnnouncement(announcementId: String): Announcement =
        Announcement.of(announcementId, "title", LocalDateTime.now(), "content")

    fun createUserSearch(userId: String): UserSearch = UserSearch.of(userId, LocalDateTime.now())

    fun createChatNormalLog(
        messageId: String,
        chatRoomId: String,
        userId: String,
        chatRoomNumber: ChatNumber,
        time: LocalDateTime,
    ): ChatNormalLog = ChatNormalLog.of(
        messageId,
        chatRoomId,
        userId,
        "text",
        chatRoomNumber,
        time,
        ChatLogType.NORMAL,
    )

    fun createChatInviteLog(
        messageId: String,
        chatRoomId: String,
        userId: String,
        chatRoomNumber: ChatNumber,
    ): ChatInviteLog = ChatInviteLog.of(
        messageId,
        chatRoomId,
        userId,
        LocalDateTime.now(),
        chatRoomNumber,
        listOf("targetUserId"),
        ChatLogType.INVITE,
    )

    fun createChatReplyLog(
        messageId: String,
        chatRoomId: String,
        userId: String,
        chatRoomNumber: ChatNumber,
    ): ChatReplyLog = ChatReplyLog.of(
        messageId,
        chatRoomId,
        userId,
        "parentMessageId",
        0,
        0,
        LocalDateTime.now(),
        chatRoomNumber,
        "text",
        "parentMessageText",
        ChatLogType.REPLY,
        ChatLogType.NORMAL,
    )

    fun createChatLeaveLog(
        messageId: String,
        chatRoomId: String,
        userId: String,
        chatRoomNumber: ChatNumber,
    ): ChatLeaveLog = ChatLeaveLog.of(
        messageId,
        chatRoomId,
        userId,
        LocalDateTime.now(),
        chatRoomNumber,
        ChatLogType.LEAVE,
    )

    fun createChatBombLog(
        messageId: String,
        chatRoomId: String,
        userId: String,
        chatRoomNumber: ChatNumber,
    ): ChatBombLog = ChatBombLog.of(
        messageId,
        chatRoomId,
        userId,
        "text",
        chatRoomNumber,
        LocalDateTime.now(),
        LocalDateTime.now().plusMinutes(1),
        ChatLogType.BOMB,
    )

    fun createChatFileLog(
        messageId: String,
        chatRoomId: String,
        userId: String,
        chatRoomNumber: ChatNumber,
    ): ChatFileLog = ChatFileLog.of(
        messageId,
        chatRoomId,
        userId,
        listOf(
            Media.of(FileCategory.CHAT, "www.example.com", 0, MediaType.IMAGE_PNG),
        ),
        LocalDateTime.now(),
        chatRoomNumber,
        ChatLogType.FILE,
    )

    fun createChatSequenceNumber(chatRoomId: String): ChatSequenceNumber = ChatSequenceNumber.of(1, chatRoomId)

    fun createChatNumber(chatRoomId: String): ChatNumber = ChatNumber.of(chatRoomId, 0, 0)

    fun create100SeqChatNumber(chatRoomId: String): ChatNumber = ChatNumber.of(chatRoomId, 100, 2)

    fun createChatRoomInfo(chatRoomId: String): ChatRoomInfo = ChatRoomInfo.of(chatRoomId, false)

    fun createGroupChatRoomInfo(chatRoomId: String): ChatRoomInfo = ChatRoomInfo.of(chatRoomId, true)

    fun createChatRoomMemberInfo(
        chatRoomId: String,
        userId: String,
        readNumber: Int,
        favorite: Boolean,
    ): ChatRoomMemberInfo = ChatRoomMemberInfo.of(userId, chatRoomId, readNumber, readNumber, favorite)

    fun createPushToken(pushTokenId: String): PushToken =
        PushToken.of(pushTokenId, "testToken", PushToken.Provider.ANDROID, "deviceId")

    fun createChatNormalMessage(
        messageId: String,
        chatRoomId: String,
        userId: String,
    ): ChatNormalMessage = ChatNormalMessage.of(
        messageId,
        chatRoomId,
        userId,
        "text",
        createChatNumber(chatRoomId),
        LocalDateTime.now(),
    )

    fun createNormalMessage(messageId: String, chatRoomId: String): ChatNormalMessage = ChatNormalMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = "sender",
        text = "text",
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
    )

    fun createBombMessage(messageId: String, chatRoomId: String): ChatBombMessage = ChatBombMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = "sender",
        text = "text",
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
        expiredAt = LocalDateTime.now().plusMinutes(1),
    )

    fun createInviteMessage(messageId: String, chatRoomId: String): ChatInviteMessage = ChatInviteMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = "sender",
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
        targetUserIds = listOf("targetUserId"),
    )

    fun createFileMessage(messageId: String, chatRoomId: String): ChatFileMessage = ChatFileMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = "sender",
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
        medias = listOf(Media.of(FileCategory.CHAT, "www.example.com", 0, MediaType.IMAGE_PNG)),
    )

    fun createLeaveMessage(messageId: String, chatRoomId: String): ChatLeaveMessage = ChatLeaveMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = "sender",
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
    )

    fun createReadMessage(chatRoomId: String): ChatReadMessage = ChatReadMessage.of(
        chatRoomId = chatRoomId,
        senderId = "sender",
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
    )

    fun createReplyMessage(messageId: String, chatRoomId: String): ChatReplyMessage = ChatReplyMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = "sender",
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
        senderId = "sender",
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
    )

    fun createEmoticonInfo(emoticonId: String, emoticonPackId: String): EmoticonInfo =
        EmoticonInfo.of(emoticonId, "name", "url", emoticonPackId)

    fun createEmoticonPackInfo(
        emoticonPackId: String,
    ): EmoticonPackInfo = EmoticonPackInfo.of(emoticonPackId, "name", "url")

    fun createUserEmoticonPackInfo(
        emoticonPackId: String,
        userId: String,
    ): UserEmoticonPackInfo = UserEmoticonPackInfo.of(emoticonPackId, userId, LocalDateTime.now())

    fun createFeed(
        feedId: String,
        userId: String,
    ): Feed = Feed.of(
        createFeedInfo(feedId, userId),
        listOf(createFeedDetail(feedId, "feedDetailId", 0)),
    )

    fun createChatRoomMember(
        userId: String,
    ): ChatRoomMember = ChatRoomMember.of(userId, 0, false)

    fun createRoom(
        chatRoomId: String,
        userId: String,
        friendId: String,
        favorite: Boolean,
    ): Room = Room.of(
        chatRoomInfo = createChatRoomInfo(chatRoomId),
        userChatRoom = createChatRoomMemberInfo(chatRoomId, userId, 0, favorite),
        chatRoomMembers = listOf(
            createChatRoomMember(userId),
            createChatRoomMember(friendId),
        ),
    )

    fun createNotification(): Notification {
        return Notification.of(
            createUser(
                "userId",
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
