package org.chewing.v1

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.announcement.Announcement
import org.chewing.v1.model.announcement.AnnouncementId
import org.chewing.v1.model.auth.JwtToken
import org.chewing.v1.model.chat.log.ChatCommentLog
import org.chewing.v1.model.chat.log.ChatFileLog
import org.chewing.v1.model.chat.log.ChatInviteLog
import org.chewing.v1.model.chat.log.ChatLeaveLog
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.ChatLogType
import org.chewing.v1.model.chat.log.ChatNormalLog
import org.chewing.v1.model.chat.log.ChatReplyLog
import org.chewing.v1.model.chat.message.ChatCommentMessage
import org.chewing.v1.model.chat.message.ChatDeleteMessage
import org.chewing.v1.model.chat.message.ChatErrorMessage
import org.chewing.v1.model.chat.message.ChatFileMessage
import org.chewing.v1.model.chat.message.ChatInviteMessage
import org.chewing.v1.model.chat.message.ChatLeaveMessage
import org.chewing.v1.model.chat.message.ChatNormalMessage
import org.chewing.v1.model.chat.message.ChatReplyMessage
import org.chewing.v1.model.chat.message.MessageType
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberSequence
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.chat.room.DirectChatRoom
import org.chewing.v1.model.chat.room.DirectChatRoomInfo
import org.chewing.v1.model.chat.room.GroupChatRoom
import org.chewing.v1.model.chat.room.GroupChatRoomInfo
import org.chewing.v1.model.chat.room.GroupChatRoomMemberInfo
import org.chewing.v1.model.contact.PhoneNumber
import org.chewing.v1.model.contact.LocalPhoneNumber
import org.chewing.v1.model.feed.Feed
import org.chewing.v1.model.feed.FeedDetail
import org.chewing.v1.model.feed.FeedDetailId
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.feed.FeedInfo
import org.chewing.v1.model.feed.FeedType
import org.chewing.v1.model.friend.Friend
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.friend.FriendShipStatus
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.media.MediaType
import org.chewing.v1.model.schedule.Schedule
import org.chewing.v1.model.schedule.ScheduleAction
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleInfo
import org.chewing.v1.model.schedule.ScheduleLog
import org.chewing.v1.model.schedule.ScheduleParticipant
import org.chewing.v1.model.schedule.ScheduleParticipantReadStatus
import org.chewing.v1.model.schedule.ScheduleParticipantRole
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.schedule.ScheduleStatus
import org.chewing.v1.model.token.RefreshToken
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.User
import org.chewing.v1.model.user.UserInfo
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

object TestDataFactory {

    fun createJwtToken(): JwtToken {
        return JwtToken.of("accessToken", RefreshToken.of("refreshToken", LocalDateTime.now()))
    }

    fun createFriendName(): String {
        return "testFriendName"
    }

    fun createDirectChatRoomInfo(chatRoomId: ChatRoomId, status: ChatRoomMemberStatus): DirectChatRoomInfo {
        return DirectChatRoomInfo.of(
            chatRoomId,
            UserId.of("testUserId"),
            UserId.of("testFriendId"),
            status,
            ChatRoomMemberStatus.NORMAL,
        )
    }

    fun createChatRoomSequence(chatRoomId: ChatRoomId): ChatRoomSequence {
        return ChatRoomSequence.of(
            chatRoomId,
            1,
        )
    }

    fun createChatRoomMemberSequence(chatRoomId: ChatRoomId): ChatRoomMemberSequence {
        return ChatRoomMemberSequence.of(
            chatRoomId,
            0,
            0,
        )
    }

    fun createDirectChatRoom(chatRoomId: ChatRoomId): DirectChatRoom {
        return DirectChatRoom.of(
            createDirectChatRoomInfo(chatRoomId, ChatRoomMemberStatus.NORMAL),
            createChatRoomSequence(chatRoomId),
            createChatRoomMemberSequence(chatRoomId),
        )
    }

    fun createGroupChatRoomInfo(chatRoomId: ChatRoomId): GroupChatRoomInfo {
        return GroupChatRoomInfo.of(
            chatRoomId,
            "testGroupName",
        )
    }

    fun createGroupChatMemberInfo(chatRoomId: ChatRoomId, status: ChatRoomMemberStatus): GroupChatRoomMemberInfo {
        return GroupChatRoomMemberInfo.of(
            chatRoomId,
            UserId.of("testFriendId"),
            status,
        )
    }

    fun createGroupChatOwnMemberInfo(chatRoomId: ChatRoomId, status: ChatRoomMemberStatus): GroupChatRoomMemberInfo {
        return GroupChatRoomMemberInfo.of(
            chatRoomId,
            UserId.of("testUserId"),
            status,
        )
    }

    fun createGroupChatRoom(chatRoomId: ChatRoomId): GroupChatRoom {
        return GroupChatRoom.of(
            createGroupChatRoomInfo(chatRoomId),
            listOf(
                createGroupChatMemberInfo(chatRoomId, ChatRoomMemberStatus.NORMAL),
                createGroupChatOwnMemberInfo(chatRoomId, ChatRoomMemberStatus.NORMAL),
            ),
            createChatRoomSequence(chatRoomId),
            createChatRoomMemberSequence(chatRoomId),
        )
    }

    fun createScheduleLogs(): List<ScheduleLog> {
        return listOf(
            ScheduleLog.of(
                ScheduleId.of("testScheduleId"),
                UserId.of("testUserId"),
                ScheduleAction.CREATED,
                LocalDateTime.now(),
            ),
            ScheduleLog.of(
                ScheduleId.of("testScheduleId"),
                UserId.of("testUserId"),
                ScheduleAction.UPDATED,
                LocalDateTime.now(),
            ),
            ScheduleLog.of(
                ScheduleId.of("testScheduleId"),
                UserId.of("testUserId"),
                ScheduleAction.DELETED,
                LocalDateTime.now(),
            ),
            ScheduleLog.of(
                ScheduleId.of("testScheduleId"),
                UserId.of("testUserId"),
                ScheduleAction.CANCELED,
                LocalDateTime.now(),
            ),
        )
    }

    fun createUserId(): UserId {
        return UserId.of("testUserId")
    }

    fun createUserInfo(userId: String, accessStatus: AccessStatus): UserInfo {
        return UserInfo.of(
            UserId.of(userId),
            "testUserName",
            Media.of(FileCategory.PROFILE, "www.example.com", 0, MediaType.IMAGE_PNG),
            accessStatus,
            PhoneNumber.of("testPhoneNumber"),
            "testPassword",
            "testStatusMessage",
        )
    }

    fun createUser(userId: String, accessStatus: AccessStatus): User {
        return User.of(
            createUserInfo(userId, accessStatus),
            LocalPhoneNumber.of("82", "01012345678"),
        )
    }

    fun createFriendShip(userId: String, friendId: String, friendShipStatus: FriendShipStatus): FriendShip {
        return FriendShip.of(
            UserId.of(userId),
            UserId.of(friendId),
            "testFriendName",
            true,
            friendShipStatus,
        )
    }

    fun createFriend(userId: String, friendShipStatus: FriendShipStatus): Friend {
        return Friend.of(
            createUser(userId, AccessStatus.ACCESS),
            createFriendShip(userId, "testFriendId", friendShipStatus),
        )
    }

    fun createScheduleInfo(status: ScheduleStatus): ScheduleInfo {
        return ScheduleInfo.of(
            ScheduleId.of("testScheduleId"),
            "testScheduleTitle",
            "testScheduleMemo",
            LocalDateTime.now(),
            "testLocation",
            true,
            status,
        )
    }

    fun createScheduleParticipant(
        status: ScheduleParticipantStatus,
        role: ScheduleParticipantRole,
        readStatus: ScheduleParticipantReadStatus,
    ): ScheduleParticipant {
        return ScheduleParticipant.of(
            UserId.of("testUserId"),
            ScheduleId.of("testScheduleId"),
            status,
            role,
            readStatus,
        )
    }

    fun createSchedule(
        scheduleInfo: ScheduleInfo,
        scheduleParticipants: List<ScheduleParticipant>,
    ): Schedule {
        return Schedule.of(
            scheduleInfo,
            scheduleParticipants,
            true,
            false,
        )
    }

    fun createFeedDetail1(): FeedDetail {
        return FeedDetail.of(
            FeedDetailId.of("testFeedDetailId"),
            Media.of(FileCategory.FEED, "www.example.com", 0, MediaType.IMAGE_PNG),
            FeedId.of("testFeedId"),
        )
    }

    fun createFeedInfo(feedId: FeedId, feedType: FeedType): FeedInfo {
        return FeedInfo.of(
            feedId,
            "testContent",
            LocalDateTime.now(),
            UserId.of("testUserId"),
            feedType,
        )
    }

    fun createFeedDetail2(): FeedDetail {
        return FeedDetail.of(
            FeedDetailId.of("feedDetailId"),
            Media.of(FileCategory.FEED, "www.example.com", 0, MediaType.IMAGE_PNG),
            FeedId.of("testFeedId"),
        )
    }

    fun createFeed(feedId: FeedId, feedType: FeedType): Feed {
        return when (feedType) {
            FeedType.FILE -> {
                return Feed.of(
                    createFeedInfo(feedId, feedType),
                    listOf(createFeedDetail1(), createFeedDetail2()),
                )
            }
            FeedType.TEXT_BLUE -> {
                return Feed.of(
                    createFeedInfo(feedId, feedType),
                    listOf(),
                )
            }
            FeedType.TEXT_SKY -> {
                return Feed.of(
                    createFeedInfo(feedId, feedType),
                    listOf(),
                )
            }
        }
    }

    fun createAnnouncement(): Announcement {
        return Announcement.of(AnnouncementId.of("announcementId"), "title", LocalDateTime.now(), "content")
    }

    fun createNormalLog(chatRoomId: ChatRoomId): ChatNormalLog {
        return ChatNormalLog.of(
            messageId = "messageId",
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            text = "text",
            roomSequence = createChatRoomSequence(chatRoomId),
            timestamp = LocalDateTime.now(),
            type = ChatLogType.NORMAL,
        )
    }

    fun createInviteLog(chatRoomId: ChatRoomId): ChatInviteLog {
        return ChatInviteLog.of(
            messageId = "messageId",
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            roomSequence = createChatRoomSequence(chatRoomId),
            timestamp = LocalDateTime.now(),
            type = ChatLogType.INVITE,
            targetUserIds = listOf(UserId.of("targetUserId")),
        )
    }

    fun createFileLog(chatRoomId: ChatRoomId): ChatFileLog {
        return ChatFileLog.of(
            messageId = "messageId",
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            roomSequence = createChatRoomSequence(chatRoomId),
            timestamp = LocalDateTime.now(),
            type = ChatLogType.FILE,
            medias = listOf(Media.of(FileCategory.CHAT, "www.example.com", 0, MediaType.IMAGE_PNG)),
        )
    }

    fun createNormalDeleteLog(chatRoomId: ChatRoomId): ChatNormalLog {
        return ChatNormalLog.of(
            messageId = "messageId",
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            text = "text",
            roomSequence = createChatRoomSequence(chatRoomId),
            timestamp = LocalDateTime.now(),
            type = ChatLogType.DELETE,
        )
    }

    fun createFileDeleteLog(chatRoomId: ChatRoomId): ChatFileLog {
        return ChatFileLog.of(
            messageId = "messageId",
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            roomSequence = createChatRoomSequence(chatRoomId),
            timestamp = LocalDateTime.now(),
            type = ChatLogType.DELETE,
            medias = listOf(Media.of(FileCategory.CHAT, "www.example.com", 0, MediaType.IMAGE_PNG)),
        )
    }

    fun createReplyLog(chatRoomId: ChatRoomId): ChatReplyLog {
        return ChatReplyLog.of(
            messageId = "messageId",
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            text = "text",
            roomSequence = createChatRoomSequence(chatRoomId),
            timestamp = LocalDateTime.now(),
            type = ChatLogType.REPLY,
            parentMessageId = "parentMessageId",
            parentMessageText = "parentMessageText",
            parentMessageType = ChatLogType.REPLY,
            parentSeqNumber = 0,
        )
    }

    fun createReplyDeleteLog(chatRoomId: ChatRoomId): ChatReplyLog {
        return ChatReplyLog.of(
            messageId = "messageId",
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            text = "text",
            roomSequence = createChatRoomSequence(chatRoomId),
            timestamp = LocalDateTime.now(),
            type = ChatLogType.DELETE,
            parentMessageId = "parentMessageId",
            parentMessageText = "parentMessageText",
            parentMessageType = ChatLogType.REPLY,
            parentSeqNumber = 0,
        )
    }
    fun createLeaveLog(chatRoomId: ChatRoomId): ChatLeaveLog {
        return ChatLeaveLog.of(
            messageId = "messageId",
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            roomSequence = createChatRoomSequence(chatRoomId),
            timestamp = LocalDateTime.now(),
            type = ChatLogType.LEAVE,
        )
    }

    fun createCommentLog(chatRoomId: ChatRoomId): ChatCommentLog {
        return ChatCommentLog.of(
            messageId = "messageId",
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            roomSequence = createChatRoomSequence(chatRoomId),
            timestamp = LocalDateTime.now(),
            type = ChatLogType.COMMENT,
            medias = listOf(Media.of(FileCategory.CHAT, "www.example.com", 0, MediaType.IMAGE_PNG)),
            comment = "comment",
            feedId = FeedId.of("feedId"),
            feedType = FeedType.FILE,
            content = "content",
        )
    }

    fun createCommentMessage(messageId: String, chatRoomId: ChatRoomId, chatRoomType: ChatRoomType): ChatCommentMessage {
        return ChatCommentMessage.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            roomSequence = createChatRoomSequence(chatRoomId),
            timestamp = LocalDateTime.now(),
            medias = listOf(Media.of(FileCategory.CHAT, "www.example.com", 0, MediaType.IMAGE_PNG)),
            comment = "comment",
            feedId = FeedId.of("feedId"),
            feedType = FeedType.FILE,
            content = "content",
            chatRoomType = chatRoomType,
        )
    }

    fun createNormalMessage(messageId: String, chatRoomId: ChatRoomId, chatRoomType: ChatRoomType): ChatNormalMessage {
        return ChatNormalMessage.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            text = "text",
            roomSequence = createChatRoomSequence(chatRoomId),
            chatRoomType = chatRoomType,
            timestamp = LocalDateTime.now(),
        )
    }

    fun createInviteMessage(messageId: String, chatRoomId: ChatRoomId, chatRoomType: ChatRoomType): ChatInviteMessage {
        return ChatInviteMessage.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            roomSequence = createChatRoomSequence(chatRoomId),
            timestamp = LocalDateTime.now(),
            targetUserIds = listOf(UserId.of("targetUserId")),
            chatRoomType = chatRoomType,
        )
    }

    fun createFileMessage(messageId: String, chatRoomId: ChatRoomId, chatRoomType: ChatRoomType): ChatFileMessage {
        return ChatFileMessage.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            roomSequence = createChatRoomSequence(chatRoomId),
            timestamp = LocalDateTime.now(),
            medias = listOf(Media.of(FileCategory.CHAT, "www.example.com", 0, MediaType.IMAGE_PNG)),
            chatRoomType = chatRoomType,
        )
    }

    fun createLeaveMessage(messageId: String, chatRoomId: ChatRoomId, chatRoomType: ChatRoomType): ChatLeaveMessage {
        return ChatLeaveMessage.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            number = createChatRoomSequence(chatRoomId),
            timestamp = LocalDateTime.now(),
            chatRoomType = chatRoomType,
        )
    }

    fun createDirectChatLogs(chatRoomId: ChatRoomId): List<ChatLog> {
        return listOf(
            createNormalLog(chatRoomId),
            createFileLog(chatRoomId),
            createNormalDeleteLog(chatRoomId),
            createFileDeleteLog(chatRoomId),
            createReplyLog(chatRoomId),
            createReplyDeleteLog(chatRoomId),
        )
    }

    fun createGroupChatLogs(chatRoomId: ChatRoomId): List<ChatLog> {
        return listOf(
            createNormalLog(chatRoomId),
            createFileLog(chatRoomId),
            createNormalDeleteLog(chatRoomId),
            createFileDeleteLog(chatRoomId),
            createReplyLog(chatRoomId),
            createReplyDeleteLog(chatRoomId),
            createLeaveLog(chatRoomId),
            createInviteLog(chatRoomId),
        )
    }

//    fun createReadMessage(chatRoomId: ChatRoomId): ChatReadMessage {
//        return ChatReadMessage.of(
//            chatRoomId = chatRoomId,
//            senderId = UserId.of("senderId"),
//            number = ChatSequence.of(chatRoomId, 1),
//            timestamp = LocalDateTime.now(),
//        )
//    }

    fun createReplyMessage(messageId: String, chatRoomId: ChatRoomId, chatRoomType: ChatRoomType): ChatReplyMessage {
        return ChatReplyMessage.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            roomSequence = createChatRoomSequence(chatRoomId),
            timestamp = LocalDateTime.now(),
            parentMessageId = "parentMessageId",
            parentMessageText = "parentMessageText",
            parentMessageType = ChatLogType.REPLY,
            parentSeqNumber = 1,
            type = MessageType.REPLY,
            text = "text",
            chatRoomType = chatRoomType,
        )
    }

    fun createErrorMessage(chatRoomId: ChatRoomId, chatRoomType: ChatRoomType): ChatErrorMessage {
        return ChatErrorMessage.of(
            chatRoomId = chatRoomId,
            userId = UserId.of("testUserId"),
            errorCode = ErrorCode.CHATROOM_READ_FAILED,
            chatRoomType = chatRoomType,
        )
    }

    fun createDeleteMessage(messageId: String, chatRoomId: ChatRoomId, chatRoomType: ChatRoomType): ChatDeleteMessage {
        return ChatDeleteMessage.of(
            targetMessageId = messageId,
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            roomSequence = createChatRoomSequence(chatRoomId),
            timestamp = LocalDateTime.now(),
            chatRoomType = chatRoomType,
        )
    }

    fun createScheduleId(): ScheduleId {
        return ScheduleId.of("testScheduleId")
    }
}
