package org.chewing.v1

import org.chewing.v1.model.announcement.Announcement
import org.chewing.v1.model.announcement.AnnouncementId
import org.chewing.v1.model.auth.JwtToken
import org.chewing.v1.model.auth.PhoneNumber
import org.chewing.v1.model.chat.log.*
import org.chewing.v1.model.chat.message.*
import org.chewing.v1.model.chat.room.ChatNumber
import org.chewing.v1.model.feed.Feed
import org.chewing.v1.model.feed.FeedDetail
import org.chewing.v1.model.feed.FeedDetailId
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.feed.FeedInfo
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.media.MediaType
import org.chewing.v1.model.schedule.Schedule
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleInfo
import org.chewing.v1.model.schedule.ScheduleParticipant
import org.chewing.v1.model.schedule.ScheduleParticipantRole
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.schedule.ScheduleStatus
import org.chewing.v1.model.token.RefreshToken
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.User
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

object TestDataFactory {

    fun createJwtToken(): JwtToken {
        return JwtToken.of("accessToken", RefreshToken.of("refreshToken", LocalDateTime.now()))
    }

    fun createFriendName(): String {
        return "testFriendName"
    }

    fun createUserId(): UserId {
        return UserId.of("testUserId")
    }

    fun createUser(accessStatus: AccessStatus): User {
        return User.of(
            UserId.of("testUserId"),
            "testUserName",
            "20000101",
            Media.of(FileCategory.PROFILE, "www.example.com", 0, MediaType.IMAGE_PNG),
            accessStatus,
            PhoneNumber.of("82", "01000000000"),
            "testPassword",
            "testStatusMessage",
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

    fun createScheduleParticipant(status: ScheduleParticipantStatus, role: ScheduleParticipantRole): ScheduleParticipant {
        return ScheduleParticipant.of(
            UserId.of("testUserId"),
            ScheduleId.of("testScheduleId"),
            status,
            role,
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

    fun createFeedInfo(): FeedInfo {
        return FeedInfo.of(
            FeedId.of("testFeedId"),
            "testContent",
            LocalDateTime.now(),
            UserId.of("testUserId"),
        )
    }

    fun createFeedDetail2(): FeedDetail {
        return FeedDetail.of(
            FeedDetailId.of("feedDetailId"),
            Media.of(FileCategory.FEED, "www.example.com", 0, MediaType.IMAGE_PNG),
            FeedId.of("testFeedId"),
        )
    }

    fun createFeed(): Feed {
        return Feed.of(
            createFeedInfo(),
            listOf(createFeedDetail1(), createFeedDetail2()),
        )
    }

    fun createAnnouncement(): Announcement {
        return Announcement.of(AnnouncementId.of("announcementId"), "title", LocalDateTime.now(), "content")
    }

    fun createNormalMessage(messageId: String, chatRoomId: String): ChatNormalMessage {
        return ChatNormalMessage.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            text = "text",
            number = ChatNumber.of(chatRoomId, 1, 1),
            timestamp = LocalDateTime.now(),
        )
    }

    fun createBombMessage(messageId: String, chatRoomId: String): ChatBombMessage {
        return ChatBombMessage.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            text = "text",
            number = ChatNumber.of(chatRoomId, 1, 1),
            timestamp = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusMinutes(1),
        )
    }

    fun createInviteMessage(messageId: String, chatRoomId: String): ChatInviteMessage {
        return ChatInviteMessage.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            number = ChatNumber.of(chatRoomId, 1, 1),
            timestamp = LocalDateTime.now(),
            targetUserIds = listOf(UserId.of("targetUserId")),
        )
    }

    fun createFileMessage(messageId: String, chatRoomId: String): ChatFileMessage {
        return ChatFileMessage.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            number = ChatNumber.of(chatRoomId, 1, 1),
            timestamp = LocalDateTime.now(),
            medias = listOf(Media.of(FileCategory.CHAT, "www.example.com", 0, MediaType.IMAGE_PNG)),
        )
    }

    fun createLeaveMessage(messageId: String, chatRoomId: String): ChatLeaveMessage {
        return ChatLeaveMessage.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            number = ChatNumber.of(chatRoomId, 1, 1),
            timestamp = LocalDateTime.now(),
        )
    }

    fun createReadMessage(chatRoomId: String): ChatReadMessage {
        return ChatReadMessage.of(
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            number = ChatNumber.of(chatRoomId, 1, 1),
            timestamp = LocalDateTime.now(),
        )
    }

    fun createReplyMessage(messageId: String, chatRoomId: String): ChatReplyMessage {
        return ChatReplyMessage.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
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
    }

    fun createDeleteMessage(messageId: String, chatRoomId: String): ChatDeleteMessage {
        return ChatDeleteMessage.of(
            targetMessageId = messageId,
            chatRoomId = chatRoomId,
            senderId = UserId.of("senderId"),
            number = ChatNumber.of(chatRoomId, 1, 1),
            timestamp = LocalDateTime.now(),
        )
    }

    fun createScheduleId(): ScheduleId {
        return ScheduleId.of("testScheduleId")
    }
}
