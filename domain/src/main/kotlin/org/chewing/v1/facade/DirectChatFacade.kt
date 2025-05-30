package org.chewing.v1.facade

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomAggregator
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.UnReadTarget
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.chat.room.DirectChatRoom
import org.chewing.v1.model.chat.room.ThumbnailDirectChatRoom
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.media.FileData
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.chat.ChatLogService
import org.chewing.v1.service.chat.DirectChatRoomService
import org.chewing.v1.service.feed.FeedService
import org.chewing.v1.service.friend.FriendShipService
import org.chewing.v1.service.notification.NotificationService
import org.springframework.stereotype.Service
import kotlin.collections.first

@Service
class DirectChatFacade(
    private val chatLogService: ChatLogService,
    private val directChatRoomService: DirectChatRoomService,
    private val notificationService: NotificationService,
    private val friendShipService: FriendShipService,
    private val feedService: FeedService,
    private val directChatRoomAggregator: DirectChatRoomAggregator,
) {

    fun processDirectChatLogs(userId: UserId, chatRoomId: ChatRoomId, sequenceNumber: Int): List<ChatLog> {
        val joinSequence =
            directChatRoomService.getDirectChatRoom(userId, chatRoomId).ownSequence.joinSequenceNumber
        val chatLogs = chatLogService.getChatLogs(chatRoomId, sequenceNumber, joinSequence)
        return chatLogs
    }

    fun processDirectLatestChatLogs(userId: UserId, chatRoomId: ChatRoomId): List<ChatLog> {
        val directChatRoom =
            directChatRoomService.getDirectChatRoom(userId, chatRoomId)
        val chatLogs = chatLogService.getLatestChatLogs(chatRoomId, directChatRoom.ownSequence.joinSequenceNumber)
        directChatRoomService.readDirectChatRoom(userId, chatRoomId, directChatRoom.roomSequence.sequence)
        return chatLogs
    }

    fun processGetDirectChatRooms(userId: UserId): List<ThumbnailDirectChatRoom> {
        val chatRooms = directChatRoomService.getDirectChatRooms(userId)
        val chatMessages = chatLogService.getsLatestChatLog(chatRooms.map { it.roomInfo.chatRoomId })
        return directChatRoomAggregator.aggregatesThumbnail(chatRooms, chatMessages)
    }

    fun searchDirectChatRooms(userId: UserId, friendIds: List<UserId>): List<ThumbnailDirectChatRoom> {
        val chatRooms = directChatRoomService.searchDirectChatRooms(userId, friendIds)
        val chatMessages = chatLogService.getsLatestChatLog(chatRooms.map { it.roomInfo.chatRoomId })

        return directChatRoomAggregator.aggregatesThumbnail(chatRooms, chatMessages)
    }

    fun processUnreadDirectChatLog(userId: UserId): List<Pair<DirectChatRoom, List<ChatLog>>> {
        val directChatRooms = directChatRoomService.getUnReadDirectChatRooms(userId)

        val unReadDirectTargets = directChatRooms.map { chatRoom ->
            UnReadTarget.of(
                chatRoom.roomInfo.chatRoomId,
                chatRoom.roomSequence.sequence,
                chatRoom.ownSequence.readSequenceNumber,
            )
        }

        val chatLogsByRoomId = chatLogService.getUnreadChatLogs(unReadDirectTargets)
            .groupBy { it.chatRoomId }

        return directChatRooms.mapNotNull { chatRoom ->
            chatLogsByRoomId[chatRoom.roomInfo.chatRoomId]?.let { chatLogs ->
                chatRoom to chatLogs
            }
        }.sortedByDescending { it.second.first().timestamp }
    }

    fun searchChatLog(userId: UserId, chatRoomId: ChatRoomId, keyword: String): List<ChatLog> {
        directChatRoomService.validateIsParticipant(userId, chatRoomId)
        return chatLogService.getChatKeyWordMessages(chatRoomId, keyword)
    }

    fun processGetRelationDirectChatRoom(userId: UserId, friendId: UserId): DirectChatRoom {
        friendShipService.checkAccessibleFriendShip(userId, friendId)
        return directChatRoomService.getDirectChatRoom(userId, friendId)
    }

    fun processGetDirectChatRoom(userId: UserId, chatRoomId: ChatRoomId): ThumbnailDirectChatRoom {
        val chatRoom = directChatRoomService.getDirectChatRoom(userId, chatRoomId)
        val chatLog = chatLogService.getLatestChatLog(chatRoomId)
        return directChatRoomAggregator.aggregateThumbnail(chatRoom, chatLog)
    }

    fun processCreateDirectChatRoomCommonChat(userId: UserId, friendId: UserId, message: String): ThumbnailDirectChatRoom {
        friendShipService.checkAccessibleFriendShip(userId, friendId)
        val chatRoomId = directChatRoomService.createDirectChatRoom(userId, friendId)
        val directChatRoom = directChatRoomService.getDirectChatRoom(userId, chatRoomId)
        if (directChatRoom.roomInfo.friendStatus == ChatRoomMemberStatus.DELETED) {
            directChatRoomService.restoreDirectChatRoom(directChatRoom.roomInfo.friendId, chatRoomId)
        }
        val chatSequence = directChatRoomService.increaseDirectChatRoomSequence(chatRoomId)
        val chatMessage = chatLogService.chatNormalMessage(
            chatRoomId,
            userId,
            message,
            chatSequence,
            ChatRoomType.DIRECT,
        )

        directChatRoomService.readDirectChatRoom(userId, chatRoomId, chatMessage.roomSequence.sequence)

        notificationService.handleMessageNotification(chatMessage, friendId, userId)
        val chatLog = chatLogService.getChatLog(chatMessage.messageId)
        return directChatRoomAggregator.aggregateThumbnail(directChatRoom, chatLog)
    }

    fun processCreateDirectChatRoomFilesChat(userId: UserId, friendId: UserId, fileDataList: List<FileData>): ThumbnailDirectChatRoom {
        friendShipService.checkAccessibleFriendShip(userId, friendId)
        val medias = chatLogService.uploadFiles(fileDataList, userId)
        val chatRoomId = directChatRoomService.createDirectChatRoom(userId, friendId)

        val directChatRoom = directChatRoomService.getDirectChatRoom(userId, chatRoomId)
        if (directChatRoom.roomInfo.friendStatus == ChatRoomMemberStatus.DELETED) {
            directChatRoomService.restoreDirectChatRoom(directChatRoom.roomInfo.friendId, chatRoomId)
        }

        val chatSequence = directChatRoomService.increaseDirectChatRoomSequence(chatRoomId)
        val chatMessage = chatLogService.mediasMessage(chatRoomId, userId, chatSequence, medias, ChatRoomType.DIRECT)

        directChatRoomService.readDirectChatRoom(userId, chatRoomId, chatMessage.roomSequence.sequence)

        notificationService.handleMessageNotification(chatMessage, friendId, userId)
        val chatLog = chatLogService.getChatLog(chatMessage.messageId)

        return directChatRoomAggregator.aggregateThumbnail(directChatRoom, chatLog)
    }

    fun processDirectChatFiles(fileDataList: List<FileData>, userId: UserId, chatRoomId: ChatRoomId) {
        val directChatRoom = directChatRoomService.getDirectChatRoom(userId, chatRoomId)
        if (directChatRoom.roomInfo.friendStatus == ChatRoomMemberStatus.DELETED) {
            directChatRoomService.restoreDirectChatRoom(directChatRoom.roomInfo.friendId, chatRoomId)
        }
        val medias = chatLogService.uploadFiles(fileDataList, userId)
        val chatSequence = directChatRoomService.increaseDirectChatRoomSequence(chatRoomId)
        val chatMessage = chatLogService.mediasMessage(chatRoomId, userId, chatSequence, medias, ChatRoomType.DIRECT)

        directChatRoomService.readDirectChatRoom(userId, chatRoomId, chatMessage.roomSequence.sequence)
        notificationService.handleMessageNotification(chatMessage, userId, userId)
        notificationService.handleMessageNotification(chatMessage, directChatRoom.roomInfo.friendId, userId)
    }

    fun processDirectChatRead(chatRoomId: ChatRoomId, userId: UserId, sequenceNumber: Int) {
        try {
            val chatMessage =
                chatLogService.readMessage(
                    chatRoomId,
                    userId,
                    ChatRoomSequence.of(chatRoomId, sequenceNumber),
                    ChatRoomType.DIRECT,
                )
            directChatRoomService.readDirectChatRoom(userId, chatRoomId, sequenceNumber)
            notificationService.handleMessageNotification(chatMessage, userId, userId)
        } catch (e: ConflictException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.DIRECT)
            notificationService.handleMessageNotification(errorMessage, userId, userId)
        }
    }

    fun processDirectChatDelete(chatRoomId: ChatRoomId, userId: UserId, messageId: String) {
        try {
            val chatRoomInfo = directChatRoomService.getDirectChatRoom(userId, chatRoomId)
            val chatMessage = chatLogService.deleteMessage(chatRoomId, userId, messageId, ChatRoomType.DIRECT)
            notificationService.handleMessageNotification(chatMessage, userId, userId)
            notificationService.handleMessageNotification(chatMessage, chatRoomInfo.roomInfo.friendId, userId)
        } catch (e: NotFoundException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.DIRECT)
            notificationService.handleMessageNotification(errorMessage, userId, userId)
        } catch (e: ConflictException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.DIRECT)
            notificationService.handleMessageNotification(errorMessage, userId, userId)
        }
    }

    fun processDirectChatReply(chatRoomId: ChatRoomId, userId: UserId, parentMessageId: String, text: String) {
        try {
            val directChatRoom = directChatRoomService.getDirectChatRoom(userId, chatRoomId)
            val chatSequence = directChatRoomService.increaseDirectChatRoomSequence(chatRoomId)
            val chatMessage = chatLogService.replyMessage(
                chatRoomId,
                userId,
                parentMessageId,
                text,
                chatSequence,
                ChatRoomType.DIRECT,
            )
            if (directChatRoom.roomInfo.friendStatus == ChatRoomMemberStatus.DELETED) {
                directChatRoomService.restoreDirectChatRoom(directChatRoom.roomInfo.friendId, chatRoomId)
            }

            directChatRoomService.readDirectChatRoom(userId, chatRoomId, chatMessage.roomSequence.sequence)
            notificationService.handleMessageNotification(chatMessage, userId, userId)
            notificationService.handleMessageNotification(chatMessage, directChatRoom.roomInfo.friendId, userId)
        } catch (e: NotFoundException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.DIRECT)
            notificationService.handleMessageNotification(errorMessage, userId, userId)
        }
    }

    fun processDirectChatCommon(chatRoomId: ChatRoomId, userId: UserId, text: String) {
        try {
            val directChatRoom = directChatRoomService.getDirectChatRoom(userId, chatRoomId)
            if (directChatRoom.roomInfo.friendStatus == ChatRoomMemberStatus.DELETED) {
                directChatRoomService.restoreDirectChatRoom(directChatRoom.roomInfo.friendId, chatRoomId)
            }
            val chatSequence = directChatRoomService.increaseDirectChatRoomSequence(chatRoomId)
            val chatMessage =
                chatLogService.chatNormalMessage(chatRoomId, userId, text, chatSequence, ChatRoomType.DIRECT)

            directChatRoomService.readDirectChatRoom(userId, chatRoomId, chatMessage.roomSequence.sequence)
            notificationService.handleMessageNotification(chatMessage, userId, userId)
            notificationService.handleMessageNotification(chatMessage, directChatRoom.roomInfo.friendId, userId)
        } catch (e: NotFoundException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.DIRECT)
            notificationService.handleMessageNotification(errorMessage, userId, userId)
        }
    }

    fun processDirectChatComment(userId: UserId, friendId: UserId, feedId: FeedId, comment: String) {
        val feed = feedService.getFeed(feedId, userId)
        friendShipService.checkAccessibleFriendShip(userId, friendId)

        val chatRoomId = directChatRoomService.createDirectChatRoom(userId, friendId)

        val directChatRoom = directChatRoomService.getDirectChatRoom(userId, chatRoomId)
        if (directChatRoom.roomInfo.friendStatus == ChatRoomMemberStatus.DELETED) {
            directChatRoomService.restoreDirectChatRoom(directChatRoom.roomInfo.friendId, chatRoomId)
        }

        val chatSequence = directChatRoomService.increaseDirectChatRoomSequence(chatRoomId)
        val chatMessage = chatLogService.commentMessage(chatRoomId, userId, chatSequence, comment, ChatRoomType.DIRECT, feed)

        directChatRoomService.readDirectChatRoom(userId, chatRoomId, chatMessage.roomSequence.sequence)

        notificationService.handleMessageNotification(chatMessage, friendId, userId)
    }
}
