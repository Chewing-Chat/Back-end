package org.chewing.v1

import org.chewing.v1.model.announcement.Announcement
import org.chewing.v1.model.auth.JwtToken
import org.chewing.v1.model.auth.PushToken
import org.chewing.v1.model.chat.log.*
import org.chewing.v1.model.chat.member.ChatRoomMember
import org.chewing.v1.model.chat.member.ChatRoomMemberInfo
import org.chewing.v1.model.chat.message.*
import org.chewing.v1.model.chat.room.ChatNumber
import org.chewing.v1.model.chat.room.ChatRoom
import org.chewing.v1.model.chat.room.ChatRoomInfo
import org.chewing.v1.model.chat.room.Room
import org.chewing.v1.model.comment.Comment
import org.chewing.v1.model.comment.CommentInfo
import org.chewing.v1.model.comment.UserCommentedInfo
import org.chewing.v1.model.emoticon.Emoticon
import org.chewing.v1.model.emoticon.EmoticonPack
import org.chewing.v1.model.feed.Feed
import org.chewing.v1.model.feed.FeedDetail
import org.chewing.v1.model.feed.FeedInfo
import org.chewing.v1.model.friend.Friend
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.media.MediaType
import org.chewing.v1.model.notification.Notification
import org.chewing.v1.model.notification.NotificationType
import org.chewing.v1.model.schedule.Schedule
import org.chewing.v1.model.search.Search
import org.chewing.v1.model.token.RefreshToken
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.User
import org.chewing.v1.model.user.UserName
import org.chewing.v1.model.user.UserStatus
import java.time.LocalDateTime

object TestDataFactory {

    fun createJwtToken(): JwtToken {
        return JwtToken.of("accessToken", RefreshToken.of("refreshToken", LocalDateTime.now()))
    }

    fun createFriendName(): UserName {
        return UserName.of("testFriendFirstName", "testFriendLastName")
    }

    fun createUser(): User {
        return User.of(
            "testUserId",
            "testFirstName",
            "testLastName",
            "2000-00-00",
            Media.of(FileCategory.PROFILE, "www.example.com", 0, MediaType.IMAGE_PNG),
            Media.of(FileCategory.BACKGROUND, "www.example.com", 0, MediaType.IMAGE_PNG),
            AccessStatus.ACCESS,
        )
    }

    fun createUserStatus(): UserStatus {
        return UserStatus.of("testStatusId", "testMessage", "testEmoji", "testUserId", true)
    }

    fun createFriend(): Friend {
        return Friend.of(createUser(), true, createFriendName(), createUserStatus(), AccessStatus.ACCESS)
    }

    fun createPrivateSchedule(): Schedule {
        return Schedule.of(
            "testScheduleId",
            "testScheduleTitle",
            "testScheduleMemo",
            LocalDateTime.now(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            "testLocation",
            true,
        )
    }

    fun createPublicSchedule(): Schedule {
        return Schedule.of(
            "testScheduleId",
            "testScheduleTitle",
            "testScheduleMemo",
            LocalDateTime.now(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            "testLocation",
            false,
        )
    }

    fun createFeedDetail1(): FeedDetail {
        return FeedDetail.of(
            "testFeedDetailId",
            Media.of(FileCategory.FEED, "www.example.com", 0, MediaType.IMAGE_PNG),
            "feedId",
        )
    }

    fun createFeedInfo(): FeedInfo {
        return FeedInfo.of(
            "feedId",
            "testTopic",
            1,
            1,
            LocalDateTime.now(),
            "testUserId",
        )
    }

    fun createFeedDetail2(): FeedDetail {
        return FeedDetail.of(
            "feedDetailId",
            Media.of(FileCategory.FEED, "www.example.com", 0, MediaType.IMAGE_PNG),
            "feedId",
        )
    }

    fun createFeed(): Feed {
        return Feed.of(
            createFeedInfo(),
            listOf(createFeedDetail1(), createFeedDetail2()),
        )
    }

    fun createComment(): Comment {
        return Comment.of("commentId", "comment", LocalDateTime.now(), createUser())
    }

    fun createAnnouncement(): Announcement {
        return Announcement.of("announcementId", "title", LocalDateTime.now(), "content")
    }

    fun createNormalMessage(messageId: String, chatRoomId: String): ChatNormalMessage {
        return ChatNormalMessage.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = "sender",
            text = "text",
            number = ChatNumber.of(chatRoomId, 1, 1),
            timestamp = LocalDateTime.now(),
        )
    }

    fun createBombMessage(messageId: String, chatRoomId: String): ChatBombMessage {
        return ChatBombMessage.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = "sender",
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
            senderId = "sender",
            number = ChatNumber.of(chatRoomId, 1, 1),
            timestamp = LocalDateTime.now(),
            targetUserIds = listOf("targetUserId"),
        )
    }

    fun createFileMessage(messageId: String, chatRoomId: String): ChatFileMessage {
        return ChatFileMessage.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = "sender",
            number = ChatNumber.of(chatRoomId, 1, 1),
            timestamp = LocalDateTime.now(),
            medias = listOf(Media.of(FileCategory.CHAT, "www.example.com", 0, MediaType.IMAGE_PNG)),
        )
    }

    fun createLeaveMessage(messageId: String, chatRoomId: String): ChatLeaveMessage {
        return ChatLeaveMessage.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = "sender",
            number = ChatNumber.of(chatRoomId, 1, 1),
            timestamp = LocalDateTime.now(),
        )
    }

    fun createReadMessage(chatRoomId: String): ChatReadMessage {
        return ChatReadMessage.of(
            chatRoomId = chatRoomId,
            senderId = "sender",
            number = ChatNumber.of(chatRoomId, 1, 1),
            timestamp = LocalDateTime.now(),
        )
    }

    fun createReplyMessage(messageId: String, chatRoomId: String): ChatReplyMessage {
        return ChatReplyMessage.of(
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
    }

    fun createDeleteMessage(messageId: String, chatRoomId: String): ChatDeleteMessage {
        return ChatDeleteMessage.of(
            targetMessageId = messageId,
            chatRoomId = chatRoomId,
            senderId = "sender",
            number = ChatNumber.of(chatRoomId, 1, 1),
            timestamp = LocalDateTime.now(),
        )
    }

    fun createReplyLog(
        messageId: String,
        chatRoomId: String,
        userId: String,
    ): ChatReplyLog {
        return ChatReplyLog.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = userId,
            number = ChatNumber.of(chatRoomId, 1, 1),
            timestamp = LocalDateTime.now(),
            parentMessageId = "parentMessageId",
            parentMessageText = "parentMessageText",
            parentMessagePage = 1,
            parentMessageType = ChatLogType.NORMAL,
            parentSeqNumber = 1,
            text = "text",
            type = ChatLogType.REPLY,
        )
    }

    fun createNormalLog(
        messageId: String,
        chatRoomId: String,
        userId: String,
    ): ChatNormalLog {
        return ChatNormalLog.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = userId,
            number = ChatNumber.of(chatRoomId, 1, 1),
            timestamp = LocalDateTime.now(),
            text = "text",
            type = ChatLogType.NORMAL,
        )
    }

    fun createFileLog(
        messageId: String,
        chatRoomId: String,
        userId: String,
    ): ChatFileLog {
        return ChatFileLog.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = userId,
            number = ChatNumber.of(chatRoomId, 1, 1),
            timestamp = LocalDateTime.now(),
            medias = listOf(Media.of(FileCategory.CHAT, "www.example.com", 0, MediaType.IMAGE_PNG)),
            type = ChatLogType.FILE,
        )
    }

    fun createLeaveLog(
        messageId: String,
        chatRoomId: String,
        userId: String,
    ): ChatLeaveLog {
        return ChatLeaveLog.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = userId,
            number = ChatNumber.of(chatRoomId, 1, 1),
            timestamp = LocalDateTime.now(),
            type = ChatLogType.LEAVE,
        )
    }

    fun createInviteLog(
        messageId: String,
        chatRoomId: String,
        userId: String,
    ): ChatInviteLog {
        return ChatInviteLog.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = userId,
            number = ChatNumber.of(chatRoomId, 1, 1),
            timestamp = LocalDateTime.now(),
            targetUserIds = listOf("targetUserId"),
            type = ChatLogType.INVITE,
        )
    }

    fun createBombLog(
        messageId: String,
        chatRoomId: String,
        userId: String,
    ): ChatBombLog {
        return ChatBombLog.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = userId,
            number = ChatNumber.of(chatRoomId, 1, 1),
            timestamp = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusMinutes(1),
            text = "text",
            type = ChatLogType.BOMB,
        )
    }

    fun createEmoticon(
        emoticonId: String,
    ): Emoticon {
        return Emoticon.of(
            id = emoticonId,
            name = "emoticonName",
            url = "www.example.com",
        )
    }

    fun createEmoticonPack(
        emoticonPackId: String,
        emoticons: List<Emoticon>,
    ): EmoticonPack {
        return EmoticonPack.of(
            id = emoticonPackId,
            name = "emoticonPackName",
            url = "www.example.com",
            emoticons = emoticons,
        )
    }

    fun createFriendShip(): FriendShip {
        return FriendShip.of(
            "testFriendId",
            createFriendName(),
            isFavorite = true,
            AccessStatus.ACCESS,
        )
    }

    fun createRoomInfo(): ChatRoomInfo {
        return ChatRoomInfo.of(
            "chatRoomId",
            true,
        )
    }

    fun createChatRoomMemberInfo(
        userId: String,
    ): ChatRoomMemberInfo {
        return ChatRoomMemberInfo.of(
            userId,
            "chatRoomId",
            1,
            1,
            true,
        )
    }

    fun createChatRoomMember(
        userId: String,
        isOwned: Boolean,
    ): ChatRoomMember {
        return ChatRoomMember.of(
            userId,
            1,
            isOwned,
        )
    }

    fun createRoom(): Room {
        return Room.of(
            createRoomInfo(),
            createChatRoomMemberInfo("userId"),
            listOf(createChatRoomMember("userId", true), createChatRoomMember("friendId", false)),
        )
    }

    fun createChatRoom(): ChatRoom {
        return ChatRoom.of(
            room = createRoom(),
            chatLog = createNormalLog("messageId", "chatRoomId", "userId"),
        )
    }

    fun createSearch(
        chatRooms: List<ChatRoom>,
        friends: List<FriendShip>,
    ): Search {
        return Search.of(
            chatRooms,
            friends,
        )
    }

    fun createCommentInfo(
        commentId: String,
    ): CommentInfo {
        return CommentInfo.of(
            commentId,
            "comment",
            LocalDateTime.now(),
            "userId",
            "feedId",
        )
    }

    fun createUserCommentedInfo(
        commentId: String,
    ): UserCommentedInfo {
        return UserCommentedInfo.of(
            createCommentInfo(commentId),
            createFriendShip(),
            createUser(),
            createFeed(),
        )
    }

    fun createNotification(): Notification {
        return Notification.of(
            createUser(),
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
