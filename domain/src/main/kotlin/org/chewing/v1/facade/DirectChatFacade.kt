package org.chewing.v1.facade

import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.room.ChatRoomId
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

@Service
class DirectChatFacade(
    private val chatLogService: ChatLogService,
    private val directChatRoomService: DirectChatRoomService,
    private val notificationService: NotificationService,
    private val friendShipService: FriendShipService
) {

    fun processDirectChatLogs(userId: UserId, chatRoomId: ChatRoomId, sequenceNumber: Int): List<ChatLog> {
        val joinSequence =
            directChatRoomService.getDirectChatRoom(userId, chatRoomId).chatRoomOwnSequence.joinSequenceNumber
        val chatLogs = chatLogService.getChatLog(chatRoomId, sequenceNumber, joinSequence)
        return chatLogs
    }

    fun processDirectChatProduce(userId: UserId, friendId : UserId): DirectChatRoom {
        friendShipService.checkAccessibleFriendShip(userId, friendId)
        return directChatRoomService.produceDirectChatRoom(userId,friendId)
    }

    fun processDirectChatFiles(fileDataList: List<FileData>, userId: UserId, chatRoomId: ChatRoomId) {
        val medias = chatLogService.uploadFiles(fileDataList, userId)
        val chatSequence = directChatRoomService.increaseDirectChatRoomSequence(chatRoomId)
        val chatMessage = chatLogService.mediasMessage(chatRoomId, userId, chatSequence, medias, ChatRoomType.DIRECT)

        val directChatRoom = directChatRoomService.getDirectChatRoom(userId, chatRoomId)

        notificationService.handleOwnedMessageNotification(chatMessage)
        notificationService.handleMessageNotification(chatMessage, directChatRoom.chatRoomInfo.friendId, userId)
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
            notificationService.handleOwnedMessageNotification(chatMessage)
        } catch (e: NotFoundException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.DIRECT)
            notificationService.handleOwnedMessageNotification(errorMessage)
        }
    }

    fun processDirectChatDelete(chatRoomId: ChatRoomId, userId: UserId, messageId: String) {
        try {
            val chatMessage = chatLogService.deleteMessage(chatRoomId, userId, messageId, ChatRoomType.DIRECT)
            val chatRoomInfo = directChatRoomService.getDirectChatRoom(userId, chatRoomId)
            notificationService.handleOwnedMessageNotification(chatMessage)
            notificationService.handleMessageNotification(chatMessage, chatRoomInfo.chatRoomInfo.friendId, userId)
        } catch (e: NotFoundException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.DIRECT)
            notificationService.handleOwnedMessageNotification(errorMessage)
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
            notificationService.handleOwnedMessageNotification(chatMessage)
            notificationService.handleMessageNotification(chatMessage, directChatRoom.chatRoomInfo.friendId, userId)
        } catch (e: NotFoundException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.DIRECT)
            notificationService.handleOwnedMessageNotification(errorMessage)
        }
    }

    fun processDirectChatCommon(chatRoomId: ChatRoomId, userId: UserId, text: String) {
        try {
            val chatSequence = directChatRoomService.increaseDirectChatRoomSequence(chatRoomId)
            val chatMessage =
                chatLogService.chatNormalMessage(chatRoomId, userId, text, chatSequence, ChatRoomType.DIRECT)
            notificationService.handleOwnedMessageNotification(chatMessage)

            val directChatRoom = directChatRoomService.getDirectChatRoom(userId, chatRoomId)
            notificationService.handleOwnedMessageNotification(chatMessage)
            notificationService.handleMessageNotification(chatMessage, directChatRoom.chatRoomInfo.friendId, userId)
        } catch (e: NotFoundException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.DIRECT)
            notificationService.handleOwnedMessageNotification(errorMessage)
        }
    }
}
