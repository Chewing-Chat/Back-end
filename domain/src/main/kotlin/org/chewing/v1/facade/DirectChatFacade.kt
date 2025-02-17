package org.chewing.v1.facade

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.UnReadTarget
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.chat.room.DirectChatRoom
import org.chewing.v1.model.media.FileData
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.chat.ChatLogService
import org.chewing.v1.service.chat.DirectChatRoomService
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
) {

    fun processDirectChatLogs(userId: UserId, chatRoomId: ChatRoomId, sequenceNumber: Int): List<ChatLog> {
        val joinSequence =
            directChatRoomService.getDirectChatRoom(userId, chatRoomId).ownSequence.joinSequenceNumber
        val chatLogs = chatLogService.getChatLog(chatRoomId, sequenceNumber, joinSequence)
        return chatLogs
    }

    fun processGetDirectChatRooms(userId: UserId): List<Pair<DirectChatRoom, ChatLog>> {
        val chatRooms = directChatRoomService.getDirectChatRooms(userId)
        val chatRoomIds = chatRooms.map { it.roomInfo.chatRoomId }
        val chatMessages = chatLogService.getLatestChat(chatRoomIds)
            .associateBy { it.chatRoomId }

        return chatRooms.mapNotNull { chatRoom ->
            chatMessages[chatRoom.roomInfo.chatRoomId]?.let { latestMessage ->
                chatRoom to latestMessage
            }
        }.sortedByDescending { it.second.timestamp }
    }

    fun searchDirectChatRooms(userId: UserId, friendIds: List<UserId>): List<Pair<DirectChatRoom, ChatLog>> {
        val chatRooms = directChatRoomService.searchDirectChatRooms(userId, friendIds)
        val chatRoomIds = chatRooms.map { it.roomInfo.chatRoomId }
        val chatMessages = chatLogService.getLatestChat(chatRoomIds)
            .associateBy { it.chatRoomId }

        return chatRooms.mapNotNull { chatRoom ->
            chatMessages[chatRoom.roomInfo.chatRoomId]?.let { latestMessage ->
                chatRoom to latestMessage
            }
        }.sortedByDescending { it.second.timestamp }
    }

    fun processUnreadDirectChatLog(userId: UserId): List<Pair<DirectChatRoom, List<ChatLog>>> {
        val directChatRooms = directChatRoomService.getUnReadDirectChatRooms(userId)

        val unReadDirectTargets = directChatRooms.map { chatRoom ->
            UnReadTarget.of(
                chatRoom.roomInfo.chatRoomId,
                chatRoom.roomSequence.sequenceNumber,
                chatRoom.ownSequence.readSequenceNumber,
            )
        }

        val chatLogsByRoomId = chatLogService.getUnreadChatLogs(unReadDirectTargets)
            .groupBy { it.chatRoomId }

        return directChatRooms.mapNotNull { chatRoom ->
            chatLogsByRoomId[chatRoom.roomInfo.chatRoomId]?.let { chatLogs ->
                chatRoom to chatLogs.sortedByDescending { it.timestamp }
            }
        }.sortedByDescending { it.second.first().timestamp }
    }

    fun searchChatLog(userId: UserId, chatRoomId: ChatRoomId, keyword: String): List<ChatLog> {
        directChatRoomService.validateIsParticipant(userId, chatRoomId)
        return chatLogService.getChatKeyWordMessages(chatRoomId, keyword)
            .sortedByDescending { it.timestamp }
    }

    fun processGetDirectChatRoom(userId: UserId, friendId: UserId): DirectChatRoom {
        friendShipService.checkAccessibleFriendShip(userId, friendId)
        return directChatRoomService.getDirectChatRoom(userId, friendId)
    }

    fun processCreateDirectChatRoomCommonChat(userId: UserId, friendId: UserId, message: String): DirectChatRoom {
        friendShipService.checkAccessibleFriendShip(userId, friendId)
        val chatRoomId = directChatRoomService.createDirectChatRoom(userId, friendId)
        val chatSequence = directChatRoomService.increaseDirectChatRoomSequence(chatRoomId)
        val chatMessage = chatLogService.chatNormalMessage(
            chatRoomId,
            userId,
            message,
            chatSequence,
            ChatRoomType.DIRECT,
        )
        val directChatRoom = directChatRoomService.getDirectChatRoom(userId, chatRoomId)
        if (directChatRoom.roomInfo.friendStatus == ChatRoomMemberStatus.DELETED) {
            directChatRoomService.restoreDirectChatRoom(userId, chatRoomId)
        }
        notificationService.handleMessageNotification(chatMessage, userId, userId)
        notificationService.handleMessageNotification(chatMessage, friendId, userId)
        return directChatRoom
    }

    fun processCreateDirectChatRoomFilesChat(userId: UserId, friendId: UserId, fileDataList: List<FileData>): DirectChatRoom {
        friendShipService.checkAccessibleFriendShip(userId, friendId)
        val medias = chatLogService.uploadFiles(fileDataList, userId)
        val chatRoomId = directChatRoomService.createDirectChatRoom(userId, friendId)
        val chatSequence = directChatRoomService.increaseDirectChatRoomSequence(chatRoomId)
        val chatMessage = chatLogService.mediasMessage(chatRoomId, userId, chatSequence, medias, ChatRoomType.DIRECT)
        val directChatRoom = directChatRoomService.getDirectChatRoom(userId, chatRoomId)
        if (directChatRoom.roomInfo.friendStatus == ChatRoomMemberStatus.DELETED) {
            directChatRoomService.restoreDirectChatRoom(userId, chatRoomId)
        }
        notificationService.handleMessageNotification(chatMessage, userId, userId)
        notificationService.handleMessageNotification(chatMessage, friendId, userId)
        return directChatRoom
    }

    fun processDirectChatFiles(fileDataList: List<FileData>, userId: UserId, chatRoomId: ChatRoomId) {
        val medias = chatLogService.uploadFiles(fileDataList, userId)
        val chatSequence = directChatRoomService.increaseDirectChatRoomSequence(chatRoomId)
        val chatMessage = chatLogService.mediasMessage(chatRoomId, userId, chatSequence, medias, ChatRoomType.DIRECT)

        val directChatRoom = directChatRoomService.getDirectChatRoom(userId, chatRoomId)

        if (directChatRoom.roomInfo.friendStatus == ChatRoomMemberStatus.DELETED) {
            directChatRoomService.restoreDirectChatRoom(userId, chatRoomId)
        }

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
            val chatMessage = chatLogService.deleteMessage(chatRoomId, userId, messageId, ChatRoomType.DIRECT)
            val chatRoomInfo = directChatRoomService.getDirectChatRoom(userId, chatRoomId)
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
            val chatSequence = directChatRoomService.increaseDirectChatRoomSequence(chatRoomId)
            val chatMessage = chatLogService.replyMessage(
                chatRoomId,
                userId,
                parentMessageId,
                text,
                chatSequence,
                ChatRoomType.DIRECT,
            )
            val directChatRoom = directChatRoomService.getDirectChatRoom(userId, chatRoomId)
            if (directChatRoom.roomInfo.friendStatus == ChatRoomMemberStatus.DELETED) {
                directChatRoomService.restoreDirectChatRoom(userId, chatRoomId)
            }
            notificationService.handleMessageNotification(chatMessage, userId, userId)
            notificationService.handleMessageNotification(chatMessage, directChatRoom.roomInfo.friendId, userId)
        } catch (e: NotFoundException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.DIRECT)
            notificationService.handleMessageNotification(errorMessage, userId, userId)
        }
    }

    fun processDirectChatCommon(chatRoomId: ChatRoomId, userId: UserId, text: String) {
        try {
            val chatSequence = directChatRoomService.increaseDirectChatRoomSequence(chatRoomId)
            val chatMessage =
                chatLogService.chatNormalMessage(chatRoomId, userId, text, chatSequence, ChatRoomType.DIRECT)
            notificationService.handleMessageNotification(chatMessage, userId, userId)
            val directChatRoom = directChatRoomService.getDirectChatRoom(userId, chatRoomId)
            if (directChatRoom.roomInfo.friendStatus == ChatRoomMemberStatus.DELETED) {
                directChatRoomService.restoreDirectChatRoom(userId, chatRoomId)
            }
            notificationService.handleMessageNotification(chatMessage, userId, userId)
            notificationService.handleMessageNotification(chatMessage, directChatRoom.roomInfo.friendId, userId)
        } catch (e: NotFoundException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.DIRECT)
            notificationService.handleMessageNotification(errorMessage, userId, userId)
        }
    }
}
