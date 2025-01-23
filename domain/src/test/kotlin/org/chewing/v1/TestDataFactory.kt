package org.chewing.v1

import org.chewing.v1.model.announcement.Announcement
import org.chewing.v1.model.announcement.AnnouncementId
import org.chewing.v1.model.auth.*
import org.chewing.v1.model.chat.log.*
import org.chewing.v1.model.chat.member.ChatRoomMember
import org.chewing.v1.model.chat.member.ChatRoomMemberInfo
import org.chewing.v1.model.chat.message.*
import org.chewing.v1.model.chat.room.ChatNumber
import org.chewing.v1.model.chat.room.ChatRoomInfo
import org.chewing.v1.model.chat.room.ChatSequenceNumber
import org.chewing.v1.model.chat.room.Room
import org.chewing.v1.model.contact.LocalPhoneNumber
import org.chewing.v1.model.contact.PhoneNumber
import org.chewing.v1.model.emoticon.EmoticonInfo
import org.chewing.v1.model.emoticon.EmoticonPackInfo
import org.chewing.v1.model.feed.Feed
import org.chewing.v1.model.feed.FeedDetail
import org.chewing.v1.model.feed.FeedDetailId
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.feed.FeedInfo
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

    fun createPhoneNumber(): PhoneNumber = PhoneNumber.of("testPhoneNumber")

    fun createUserName(): String = "testUserName"
    fun createUserId(): UserId = UserId.of("testUserId")
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
            "20000101",
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
        "2000-00-00",
        Media.of(FileCategory.PROFILE, "www.example.com", 0, MediaType.IMAGE_PNG),
        AccessStatus.ACCESS,
        PhoneNumber.of("testPhoneNumber"),
        password,
        "testStatusMessage",
    )

    fun createNotAccessUser(): UserInfo = UserInfo.of(
        UserId.of("testUserId"),
        "testName",
        "2000-00-00",
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
        FeedInfo.of(feedId, "topic", LocalDateTime.now(), userId)

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
        chatRoomId: String,
        userId: UserId,
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
        userId: UserId,
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
        userId: UserId,
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
        userId: UserId,
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
        userId: UserId,
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
        userId: UserId,
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
        userId: UserId,
        readNumber: Int,
        favorite: Boolean,
    ): ChatRoomMemberInfo = ChatRoomMemberInfo.of(userId, chatRoomId, readNumber, readNumber, favorite)

    fun createPushToken(pushTokenId: String): PushToken =
        PushToken.of(pushTokenId, "testToken", PushToken.Provider.ANDROID, "deviceId")

    fun createChatNormalMessage(
        messageId: String,
        chatRoomId: String,
        userId: UserId,
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
        targetUserIds = listOf(UserId.of("targetUserId")),
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

    fun createDeleteMessage(messageId: String, chatRoomId: String): ChatDeleteMessage = ChatDeleteMessage.of(
        targetMessageId = messageId,
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
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
        userId: UserId,
    ): UserEmoticonPackInfo = UserEmoticonPackInfo.of(userId, emoticonPackId, LocalDateTime.now())

    fun createFeed(
        feedId: FeedId,
        userId: UserId,
    ): Feed = Feed.of(
        createFeedInfo(feedId, userId),
        listOf(createFeedDetail(feedId, FeedDetailId.of("feedDetailId"), 0)),
    )

    fun createChatRoomMember(
        userId: UserId,
    ): ChatRoomMember = ChatRoomMember.of(userId, 0, false)

    fun createRoom(
        chatRoomId: String,
        userId: UserId,
        friendId: UserId,
        favorite: Boolean,
    ): Room = Room.of(
        chatRoomInfo = createChatRoomInfo(chatRoomId),
        userChatRoom = createChatRoomMemberInfo(chatRoomId, userId, 0, favorite),
        chatRoomMembers = listOf(
            createChatRoomMember(userId),
            createChatRoomMember(friendId),
        ),
    )
}
