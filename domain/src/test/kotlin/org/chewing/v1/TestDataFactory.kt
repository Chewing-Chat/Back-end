package org.chewing.v1

import org.chewing.v1.model.announcement.Announcement
import org.chewing.v1.model.announcement.AnnouncementId
import org.chewing.v1.model.auth.*
import org.chewing.v1.model.chat.log.*
import org.chewing.v1.model.chat.member.ChatRoomMember
import org.chewing.v1.model.chat.message.*
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.contact.LocalPhoneNumber
import org.chewing.v1.model.contact.PhoneNumber
import org.chewing.v1.model.feed.Feed
import org.chewing.v1.model.feed.FeedDetail
import org.chewing.v1.model.feed.FeedDetailId
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.feed.FeedInfo
import org.chewing.v1.model.feed.FeedType
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.friend.FriendShipStatus
import org.chewing.v1.model.friend.UserSearch
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.FileData
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.media.MediaType
import org.chewing.v1.model.schedule.ScheduleAction
import org.chewing.v1.model.schedule.ScheduleInfo
import org.chewing.v1.model.schedule.ScheduleContent
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleLog
import org.chewing.v1.model.schedule.ScheduleParticipant
import org.chewing.v1.model.schedule.ScheduleParticipantRole
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.schedule.ScheduleStatus
import org.chewing.v1.model.schedule.ScheduleTime
import org.chewing.v1.model.token.RefreshToken
import org.chewing.v1.model.user.*
import org.chewing.v1.model.user.AccessStatus
import java.io.ByteArrayInputStream
import java.time.LocalDateTime

object TestDataFactory {

    fun createPhoneNumber(): PhoneNumber = PhoneNumber.of("+821012345678")

    fun createUserName(): String = "testUserName"
    fun createUserId(): UserId = UserId.of("testUserId")
    fun createChatRoomId() = ChatRoomId.of("testChatRoomId")
    fun createFeedId(): FeedId = FeedId.of("testFeedId")
    fun createSecondFeedId(): FeedId = FeedId.of("testSecondFeedId")
    fun createFeedDetailId(): FeedDetailId = FeedDetailId.of("testFeedDetailId")
    fun createSecondFeedDetailId(): FeedDetailId = FeedDetailId.of("testSecondFeedDetailId")
    fun createThirdFeedDetailId(): FeedDetailId = FeedDetailId.of("testThirdFeedDetailId")
    fun createFourthFeedDetailId(): FeedDetailId = FeedDetailId.of("testFourthFeedDetailId")
    fun createTargetUserId(): UserId = UserId.of("targetUserId")
    fun createFriendId(): UserId = UserId.of("testFriendId")
    fun createSecondFriendId(): UserId = UserId.of("testSecondFriendId")
    fun createScheduleId(): ScheduleId = ScheduleId.of("testScheduleId")
    fun createScheduleLog(): ScheduleLog =
        ScheduleLog.of(createScheduleId(), createUserId(), ScheduleAction.CREATED, LocalDateTime.now())

    fun createProfileMedia(): Media = Media.of(FileCategory.PROFILE, "www.example.com", 0, MediaType.IMAGE_PNG)

    fun createMedia(category: FileCategory, index: Int, mediaType: MediaType): Media =
        Media.of(category, "www.example.com", index, mediaType)

    fun createFileData(
        contentType: MediaType = MediaType.IMAGE_JPEG,
        fileName: String = "test_image.jpg",
    ): FileData {
        val content = "Test file content"
        val size = content.toByteArray().size.toLong()
        val inputStream = ByteArrayInputStream(content.toByteArray())
        return FileData.of(inputStream, contentType, fileName, size)
    }

    fun createDevice(): PushToken.Device = PushToken.Device.of("deviceId", PushToken.Provider.ANDROID)

    fun createJwtToken(): JwtToken = JwtToken.of("accessToken", RefreshToken.of("refreshToken", LocalDateTime.now()))

    fun createRefreshToken(): RefreshToken = RefreshToken.of("refreshToken", LocalDateTime.now())
    fun createOldRefreshToken(): RefreshToken = RefreshToken.of("oldRefreshToken", LocalDateTime.now().minusDays(1))

    fun createUserInfo(userId: UserId, accessStatus: AccessStatus): UserInfo {
        return UserInfo.of(
            userId,
            "testUserName",
            Media.of(FileCategory.PROFILE, "www.example.com", 0, MediaType.IMAGE_PNG),
            accessStatus,
            PhoneNumber.of("+821012345678"),
            "testPassword",
            "testStatusMessage",
        )
    }

    fun createLocalPhoneNumber(): LocalPhoneNumber = LocalPhoneNumber.of("01012345678", "82")

    fun createUser(userId: UserId, accessStatus: AccessStatus): User {
        return User.of(
            createUserInfo(userId, accessStatus),
            createLocalPhoneNumber(),
        )
    }

    fun createEncryptedUser(userId: UserId, password: String): UserInfo = UserInfo.of(
        userId,
        "testName",
        Media.of(FileCategory.PROFILE, "www.example.com", 0, MediaType.IMAGE_PNG),
        AccessStatus.ACCESS,
        PhoneNumber.of("testPhoneNumber"),
        password,
        "testStatusMessage",
    )

    fun createNotAccessUser(): UserInfo = UserInfo.of(
        UserId.of("testUserId"),
        "testName",
        Media.of(FileCategory.PROFILE, "www.example.com", 0, MediaType.IMAGE_PNG),
        AccessStatus.NOT_ACCESS,
        PhoneNumber.of("testPhoneNumber"),
        "password",
        "testStatusMessage",
    )

    fun createScheduledTime(): ScheduleTime =
        ScheduleTime.of(LocalDateTime.now(), false)

    fun createScheduleContent(): ScheduleContent = ScheduleContent.of("testTitle", "memo", "location")

    fun createScheduleInfo(scheduleId: ScheduleId, status: ScheduleStatus): ScheduleInfo = ScheduleInfo.of(
        scheduleId,
        "title",
        "memo",
        LocalDateTime.now(),
        "location",
        true,
        status,
    )

    fun createScheduleParticipant(
        userId: UserId,
        scheduleId: ScheduleId,
        status: ScheduleParticipantStatus,
        role: ScheduleParticipantRole,
    ): ScheduleParticipant = ScheduleParticipant.of(userId, scheduleId, status, role)

    fun createFriendShip(userId: UserId, friendId: UserId, status: FriendShipStatus): FriendShip =
        FriendShip.of(userId, friendId, createUserName(), true, status)

    fun createFeedInfo(feedId: FeedId, userId: UserId): FeedInfo =
        FeedInfo.of(feedId, "topic", LocalDateTime.now(), userId, FeedType.FILE)

    private fun createFeedMedia(index: Int): Media =
        Media.of(FileCategory.FEED, "www.example.com", index, MediaType.IMAGE_PNG)

    fun createFeedDetail(feedId: FeedId, feedDetailId: FeedDetailId, index: Int): FeedDetail =
        FeedDetail.of(feedDetailId, createFeedMedia(index), feedId)

    fun createAnnouncement(announcementId: AnnouncementId): Announcement =
        Announcement.of(announcementId, "title", LocalDateTime.now(), "content")

    fun createAnnouncementId(): AnnouncementId = AnnouncementId.of("testAnnouncementId")
    fun createUserSearch(userId: UserId): UserSearch = UserSearch.of(userId.id, LocalDateTime.now())

    fun createChatNormalLog(
        messageId: String,
        chatRoomId: ChatRoomId,
        userId: UserId,
    ): ChatNormalLog = ChatNormalLog.of(
        messageId,
        chatRoomId,
        userId,
        "text",
        ChatRoomSequence.of(chatRoomId, 1),
        LocalDateTime.now(),
        ChatLogType.NORMAL,
    )

    fun createChatInviteLog(
        messageId: String,
        chatRoomId: ChatRoomId,
        userId: UserId,
        chatRoomNumber: ChatRoomSequence,
    ): ChatInviteLog = ChatInviteLog.of(
        messageId,
        chatRoomId,
        userId,
        LocalDateTime.now(),
        chatRoomNumber,
        listOf(UserId.of("targetUserId")),
        ChatLogType.INVITE,
    )

    fun createChatReplyLog(
        messageId: String,
        chatRoomId: ChatRoomId,
        userId: UserId,
        chatRoomNumber: ChatRoomSequence,
    ): ChatReplyLog = ChatReplyLog.of(
        messageId,
        chatRoomId,
        userId,
        "parentMessageId",
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
        chatRoomId: ChatRoomId,
        userId: UserId,
        chatRoomNumber: ChatRoomSequence,
    ): ChatLeaveLog = ChatLeaveLog.of(
        messageId,
        chatRoomId,
        userId,
        LocalDateTime.now(),
        chatRoomNumber,
        ChatLogType.LEAVE,
    )

    fun createChatFileLog(
        messageId: String,
        chatRoomId: ChatRoomId,
        userId: UserId,
        chatRoomNumber: ChatRoomSequence,
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

    fun createChatSequenceNumber(chatRoomId: ChatRoomId): ChatRoomSequence = ChatRoomSequence.of(chatRoomId, 1)

    fun createChatNumber(chatRoomId: ChatRoomId): ChatRoomSequence = ChatRoomSequence.of(chatRoomId, 0)

    fun createPushToken(pushTokenId: String): PushToken =
        PushToken.of(pushTokenId, "testToken", PushToken.Provider.ANDROID, "deviceId")

    fun createNormalMessage(messageId: String, chatRoomId: ChatRoomId): ChatNormalMessage = ChatNormalMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        text = "text",
        roomSequence = ChatRoomSequence.of(chatRoomId, 1),
        timestamp = LocalDateTime.now(),
        chatRoomType = ChatRoomType.DIRECT,
    )

    fun createLeaveMessage(messageId: String, chatRoomId: ChatRoomId): ChatLeaveMessage = ChatLeaveMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        number = ChatRoomSequence.of(chatRoomId, 1),
        timestamp = LocalDateTime.now(),
        chatRoomType = ChatRoomType.DIRECT,
    )

    fun createInviteMessage(messageId: String, chatRoomId: ChatRoomId): ChatInviteMessage = ChatInviteMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        roomSequence = ChatRoomSequence.of(chatRoomId, 1),
        targetUserIds = listOf(UserId.of("target")),
        timestamp = LocalDateTime.now(),
        chatRoomType = ChatRoomType.DIRECT,
    )

    fun createFileMessage(messageId: String, chatRoomId: ChatRoomId): ChatFileMessage = ChatFileMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        roomSequence = ChatRoomSequence.of(chatRoomId, 1),
        timestamp = LocalDateTime.now(),
        medias = listOf(Media.of(FileCategory.CHAT, "www.example.com", 0, MediaType.IMAGE_PNG)),
        chatRoomType = ChatRoomType.DIRECT,
    )

    fun createReplyMessage(messageId: String, chatRoomId: ChatRoomId, normalLog: ChatNormalLog): ChatReplyMessage = ChatReplyMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        text = "text",
        roomSequence = ChatRoomSequence.of(chatRoomId, 1),
        timestamp = LocalDateTime.now(),
        parentMessageId = normalLog.messageId,
        parentMessageText = normalLog.text,
        parentSeqNumber = normalLog.roomSequence.sequenceNumber,
        type = MessageType.REPLY,
        parentMessageType = normalLog.type,
        chatRoomType = ChatRoomType.DIRECT,
    )

    fun createNormalLog(messageId: String, chatRoomId: ChatRoomId): ChatNormalLog = ChatNormalLog.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        text = "text",
        roomSequence = ChatRoomSequence.of(chatRoomId, 1),
        timestamp = LocalDateTime.now(),
        type = ChatLogType.NORMAL,
    )

    fun createReadMessage(chatRoomId: ChatRoomId): ChatReadMessage = ChatReadMessage.of(
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        number = ChatRoomSequence.of(chatRoomId, 1),
        timestamp = LocalDateTime.now(),
        chatRoomType = ChatRoomType.DIRECT,
    )

    fun createDeleteMessage(chatRoomId: ChatRoomId): ChatDeleteMessage = ChatDeleteMessage.of(
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        roomSequence = ChatRoomSequence.of(chatRoomId, 1),
        timestamp = LocalDateTime.now(),
        targetMessageId = "target",
        chatRoomType = ChatRoomType.DIRECT,
    )

    fun createFeed(
        feedId: FeedId,
        userId: UserId,
    ): Feed = Feed.of(
        createFeedInfo(feedId, userId),
        listOf(createFeedDetail(feedId, FeedDetailId.of("feedDetailId"), 0)),
    )

    fun createChatRoomMember(
        userId: UserId,
    ): ChatRoomMember = ChatRoomMember.of(userId, 0)
}
