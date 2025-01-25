package org.chewing.v1.facade

import org.chewing.v1.model.media.FileData
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.chat.ChatLogService
import org.chewing.v1.service.chat.RoomService
import org.chewing.v1.service.notification.NotificationService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ChatFacade(
    private val chatLogService: ChatLogService,
    private val roomService: RoomService,
    private val notificationService: NotificationService,
) {
    fun processFiles(fileDataList: List<FileData>, userId: UserId, chatRoomId: String) {
        val chatMessage = chatLogService.uploadFiles(fileDataList, userId, chatRoomId)
        notificationService.handleOwnedMessageNotification(chatMessage)
        val chatRoomInfo = roomService.activateChatRoom(chatRoomId, userId, chatMessage.number)
        roomService.getChatRoomFriends(chatRoomId, userId, chatRoomInfo).map { it.memberId }.let {
            notificationService.handleMessagesNotification(chatMessage, it, userId)
        }
    }

    fun processRead(chatRoomId: String, userId: UserId) {
        val chatMessage = chatLogService.readMessage(chatRoomId, userId)
        notificationService.handleOwnedMessageNotification(chatMessage)
        val chatRoomInfo = roomService.getChatRoom(chatRoomId)
        roomService.updateReadChatRoom(chatRoomId, userId, chatMessage.number)
        roomService.getChatRoomFriends(chatRoomId, userId, chatRoomInfo).map { it.memberId }.let {
            notificationService.handleMessagesNotification(chatMessage, it, userId)
        }
    }

    fun processDelete(chatRoomId: String, userId: UserId, messageId: String) {
        val chatMessage = chatLogService.deleteMessage(chatRoomId, userId, messageId)
        notificationService.handleOwnedMessageNotification(chatMessage)
        val chatRoomInfo = roomService.getChatRoom(chatRoomId)
        roomService.getChatRoomFriends(chatRoomId, userId, chatRoomInfo).map { it.memberId }.let {
            notificationService.handleMessagesNotification(chatMessage, it, userId)
        }
    }

    fun processReply(chatRoomId: String, userId: UserId, parentMessageId: String, text: String) {
        val chatMessage = chatLogService.replyMessage(chatRoomId, userId, parentMessageId, text)
        notificationService.handleOwnedMessageNotification(chatMessage)
        val chatRoomInfo = roomService.activateChatRoom(chatRoomId, userId, chatMessage.number)
        roomService.getChatRoomFriends(chatRoomId, userId, chatRoomInfo).map { it.memberId }.let {
            notificationService.handleMessagesNotification(chatMessage, it, userId)
        }
    }

    fun processCommon(chatRoomId: String, userId: UserId, text: String) {
        val chatMessage = chatLogService.chatNormalMessage(chatRoomId, userId, text)
        notificationService.handleOwnedMessageNotification(chatMessage)
        val chatRoomInfo = roomService.activateChatRoom(chatRoomId, userId, chatMessage.number)
        roomService.getChatRoomFriends(chatRoomId, userId, chatRoomInfo).map { it.memberId }.let {
            notificationService.handleMessagesNotification(chatMessage, it, userId)
        }
    }
}
