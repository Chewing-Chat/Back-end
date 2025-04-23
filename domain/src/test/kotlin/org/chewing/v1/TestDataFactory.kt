package org.chewing.v1

import org.chewing.v1.model.announcement.Announcement
import org.chewing.v1.model.announcement.AnnouncementId
import org.chewing.v1.model.auth.*
import org.chewing.v1.model.chat.log.*
import org.chewing.v1.model.chat.message.*
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberSequence
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.chat.room.DirectChatRoomInfo
import org.chewing.v1.model.chat.room.GroupChatRoomInfo
import org.chewing.v1.model.chat.room.GroupChatRoomMemberInfo
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
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.FileData
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.media.MediaType
import org.chewing.v1.model.notification.NotificationStatus
import org.chewing.v1.model.notification.PushInfo
import org.chewing.v1.model.schedule.ScheduleAction
import org.chewing.v1.model.schedule.ScheduleInfo
import org.chewing.v1.model.schedule.ScheduleContent
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleLog
import org.chewing.v1.model.schedule.ScheduleParticipant
import org.chewing.v1.model.schedule.ScheduleParticipantReadStatus
import org.chewing.v1.model.schedule.ScheduleParticipantRole
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.schedule.ScheduleStatus
import org.chewing.v1.model.schedule.ScheduleTime
import org.chewing.v1.model.token.RefreshToken
import org.chewing.v1.model.user.*
import org.chewing.v1.model.user.AccessStatus
import java.io.ByteArrayInputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

object TestDataFactory {

    fun createPhoneNumber(): PhoneNumber = PhoneNumber.of("+821012345678")

    fun createUserName(): String = "testUserName"
    fun createUserId(): UserId = UserId.of(UUID.randomUUID().toString())
    fun createChatRoomId() = ChatRoomId.of(UUID.randomUUID().toString())
    fun createFeedId(): FeedId = FeedId.of(UUID.randomUUID().toString())
    fun createSecondFeedId(): FeedId = FeedId.of(UUID.randomUUID().toString())
    fun createFeedDetailId(): FeedDetailId = FeedDetailId.of(UUID.randomUUID().toString())
    fun createFriendId(): UserId = UserId.of(UUID.randomUUID().toString())
    fun createScheduleId(): ScheduleId = ScheduleId.of(UUID.randomUUID().toString())
    fun createScheduleLog(): ScheduleLog =
        ScheduleLog.of(createScheduleId(), createUserId(), ScheduleAction.CREATED, LocalDateTime.now())

    fun createFriendIds(): List<UserId> = listOf(createFriendId(), createFriendId())
    fun createProfileMedia(): Media = Media.of(FileCategory.PROFILE, "www.example.com", 0, MediaType.IMAGE_PNG)
    fun createChatRoomIds(): List<ChatRoomId> = listOf(createChatRoomId(), createChatRoomId())
    fun createMedia(category: FileCategory, index: Int, mediaType: MediaType): Media =
        Media.of(category, "www.example.com", index, mediaType)

    fun createGroupChatRoomInfos(
        chatRoomIds: List<ChatRoomId>,
    ): List<GroupChatRoomInfo> {
        return chatRoomIds.map {
            GroupChatRoomInfo.of(it, "testName")
        }
    }

    fun createGroupChatRoomInfo(
        chatRoomId: ChatRoomId,
    ): GroupChatRoomInfo {
        return GroupChatRoomInfo.of(chatRoomId, "testName")
    }

    fun createGroupChatRoomUserInfos(
        chatRoomIds: List<ChatRoomId>,
        userId: UserId,
    ): List<GroupChatRoomMemberInfo> {
        return chatRoomIds.map {
            GroupChatRoomMemberInfo.of(it, userId, ChatRoomMemberStatus.NORMAL)
        }
    }

    fun createDirectChatRoomInfo(
        chatRoomId: ChatRoomId,
        userId: UserId,
        friendId: UserId,
        status: ChatRoomMemberStatus,
        friendStatus: ChatRoomMemberStatus,
    ): DirectChatRoomInfo {
        return DirectChatRoomInfo.of(chatRoomId, userId, friendId, status, friendStatus)
    }

    fun createChatRoomSequences(chatRoomIds: List<ChatRoomId>): List<ChatRoomSequence> {
        return chatRoomIds.map {
            ChatRoomSequence.of(it, 5)
        }
    }

    fun createChatRoomUnReadSequences(chatRoomIds: List<ChatRoomId>): List<ChatRoomSequence> {
        return chatRoomIds.map {
            ChatRoomSequence.of(it, 0)
        }
    }

    fun createChatRoomMemberSequences(
        chatRoomIds: List<ChatRoomId>,
    ): List<ChatRoomMemberSequence> {
        return chatRoomIds.map {
            ChatRoomMemberSequence.of(it, 0, 0)
        }
    }

    fun createGroupChatRoomMembersInfos(
        chatRoomIds: List<ChatRoomId>,
        memberIds: List<UserId>,
    ): List<GroupChatRoomMemberInfo> {
        return chatRoomIds.flatMap { chatRoomId ->
            memberIds.map { userId ->
                GroupChatRoomMemberInfo.of(chatRoomId, userId, ChatRoomMemberStatus.NORMAL)
            }
        }
    }

    fun createGroupChatRoomMemberInfos(
        chatRoomId: ChatRoomId,
        memberIds: List<UserId>,
    ): List<GroupChatRoomMemberInfo> {
        return memberIds.map { userId ->
            GroupChatRoomMemberInfo.of(chatRoomId, userId, ChatRoomMemberStatus.NORMAL)
        }
    }

    fun createFileData(
        contentType: MediaType = MediaType.IMAGE_JPEG,
        fileName: String = "test_image.jpg",
    ): FileData {
        val content = "Test file content"
        val size = content.toByteArray().size.toLong()
        val inputStream = ByteArrayInputStream(content.toByteArray())
        return FileData.of(inputStream, contentType, fileName, size)
    }

    fun createDevice(): PushInfo.Device = PushInfo.Device.of("deviceId", PushInfo.Provider.ANDROID)

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
            LocalDate.now(),
            UserRole.USER,
        )
    }

    fun createLocalPhoneNumber(): LocalPhoneNumber = LocalPhoneNumber.of(
        number = "01012345678",
        countryCode = "82",
    )

    fun createEncryptedUser(userId: UserId, password: String): UserInfo = UserInfo.of(
        userId,
        "testName",
        Media.of(FileCategory.PROFILE, "www.example.com", 0, MediaType.IMAGE_PNG),
        AccessStatus.ACCESS,
        PhoneNumber.of("testPhoneNumber"),
        password,
        "testStatusMessage",
        LocalDate.now(),
        UserRole.USER,
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
        readStatus: ScheduleParticipantReadStatus,
    ): ScheduleParticipant = ScheduleParticipant.of(userId, scheduleId, status, role, readStatus)

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

    fun createChatCommentLog(
        messageId: String,
        chatRoomId: ChatRoomId,
        userId: UserId,
        chatRoomNumber: ChatRoomSequence,
    ): ChatCommentLog = ChatCommentLog.of(
        messageId,
        chatRoomId,
        userId,
        listOf(
            Media.of(FileCategory.CHAT, "www.example.com", 0, MediaType.IMAGE_PNG),
        ),
        LocalDateTime.now(),
        chatRoomNumber,
        ChatLogType.COMMENT,
        "comment",
        FeedId.of("feedId"),
        FeedType.FILE,
        "content",
    )

    fun createChatRoomSequence(chatRoomId: ChatRoomId): ChatRoomSequence = ChatRoomSequence.of(chatRoomId, 1)
    fun createChatRoomMemberSequence(chatRoomId: ChatRoomId): ChatRoomMemberSequence =
        ChatRoomMemberSequence.of(chatRoomId, 0, 0)

    fun createChatSequenceNumber(chatRoomId: ChatRoomId): ChatRoomSequence = ChatRoomSequence.of(chatRoomId, 1)

    fun createChatNumber(chatRoomId: ChatRoomId): ChatRoomSequence = ChatRoomSequence.of(chatRoomId, 0)

    fun createPushToken(pushTokenId: String, userId: UserId): PushInfo =
        PushInfo.of(pushTokenId, "testToken", PushInfo.Provider.ANDROID, "deviceId", userId, NotificationStatus.ALLOWED, NotificationStatus.ALLOWED)

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
        chatRoomType = ChatRoomType.GROUP,
    )

    fun createInviteMessage(messageId: String, chatRoomId: ChatRoomId): ChatInviteMessage = ChatInviteMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        roomSequence = ChatRoomSequence.of(chatRoomId, 1),
        targetUserIds = listOf(UserId.of("target")),
        timestamp = LocalDateTime.now(),
        chatRoomType = ChatRoomType.GROUP,
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

    fun createReplyMessage(messageId: String, chatRoomId: ChatRoomId, normalLog: ChatNormalLog): ChatReplyMessage =
        ChatReplyMessage.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = UserId.of("sender"),
            text = "text",
            roomSequence = ChatRoomSequence.of(chatRoomId, 1),
            timestamp = LocalDateTime.now(),
            parentMessageId = normalLog.messageId,
            parentMessageText = normalLog.text,
            parentSeqNumber = normalLog.roomSequence.sequence,
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
}
