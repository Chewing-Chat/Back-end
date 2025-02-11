package org.chewing.v1

import org.chewing.v1.model.announcement.Announcement
import org.chewing.v1.model.announcement.AnnouncementId
import org.chewing.v1.model.auth.JwtToken
import org.chewing.v1.model.contact.PhoneNumber
import org.chewing.v1.model.contact.LocalPhoneNumber
import org.chewing.v1.model.feed.Feed
import org.chewing.v1.model.feed.FeedDetail
import org.chewing.v1.model.feed.FeedDetailId
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.feed.FeedInfo
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

    fun createFriend(userId: String): Friend {
        return Friend.of(
            createUser(userId, AccessStatus.ACCESS),
            createFriendShip(userId, "testFriendId", FriendShipStatus.FRIEND),
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
    ): ScheduleParticipant {
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

//    fun createNormalMessage(messageId: String, chatRoomId: ChatRoomId, ): ChatNormalMessage {
//        return ChatNormalMessage.of(
//            messageId = messageId,
//            chatRoomId = chatRoomId,
//            senderId = UserId.of("senderId"),
//            text = "text",
//            number = ChatSequence.of(chatRoomId, 1),
//            timestamp = LocalDateTime.now(),
//        )
//    }
//
//    fun createInviteMessage(messageId: String, chatRoomId: ChatRoomId): ChatInviteMessage {
//        return ChatInviteMessage.of(
//            messageId = messageId,
//            chatRoomId = chatRoomId,
//            senderId = UserId.of("senderId"),
//            number = ChatSequence.of(chatRoomId, 1),
//            timestamp = LocalDateTime.now(),
//            targetUserIds = listOf(UserId.of("targetUserId")),
//        )
//    }
//
//    fun createFileMessage(messageId: String, chatRoomId: ChatRoomId): ChatFileMessage {
//        return ChatFileMessage.of(
//            messageId = messageId,
//            chatRoomId = chatRoomId,
//            senderId = UserId.of("senderId"),
//            number = ChatSequence.of(chatRoomId, 1),
//            timestamp = LocalDateTime.now(),
//            medias = listOf(Media.of(FileCategory.CHAT, "www.example.com", 0, MediaType.IMAGE_PNG)),
//        )
//    }
//
//    fun createLeaveMessage(messageId: String, chatRoomId: ChatRoomId): ChatLeaveMessage {
//        return ChatLeaveMessage.of(
//            messageId = messageId,
//            chatRoomId = chatRoomId,
//            senderId = UserId.of("senderId"),
//            number = ChatSequence.of(chatRoomId, 1),
//            timestamp = LocalDateTime.now(),
//        )
//    }
//
//    fun createReadMessage(chatRoomId: ChatRoomId): ChatReadMessage {
//        return ChatReadMessage.of(
//            chatRoomId = chatRoomId,
//            senderId = UserId.of("senderId"),
//            number = ChatSequence.of(chatRoomId, 1),
//            timestamp = LocalDateTime.now(),
//        )
//    }
//
//    fun createReplyMessage(messageId: String, chatRoomId: ChatRoomId): ChatReplyMessage {
//        return ChatReplyMessage.of(
//            messageId = messageId,
//            chatRoomId = chatRoomId,
//            senderId = UserId.of("senderId"),
//            number = ChatSequence.of(chatRoomId, 1),
//            timestamp = LocalDateTime.now(),
//            parentMessageId = "parentMessageId",
//            parentMessageText = "parentMessageText",
//            parentMessageType = ChatLogType.REPLY,
//            parentSeqNumber = 1,
//            type = MessageType.REPLY,
//            text = "text",
//        )
//    }
//
//    fun createDeleteMessage(messageId: String, chatRoomId: ChatRoomId): ChatDeleteMessage {
//        return ChatDeleteMessage.of(
//            targetMessageId = messageId,
//            chatRoomId = chatRoomId,
//            senderId = UserId.of("senderId"),
//            number = ChatSequence.of(chatRoomId, 1),
//            timestamp = LocalDateTime.now(),
//        )
//    }

    fun createScheduleId(): ScheduleId {
        return ScheduleId.of("testScheduleId")
    }
}
