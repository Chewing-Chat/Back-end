package org.chewing.v1.facade

import org.chewing.v1.service.chat.ChatLogService
import org.chewing.v1.service.notification.NotificationService
import org.springframework.stereotype.Service

@Service
class ChatFacade(
    private val chatLogService: ChatLogService,
    private val notificationService: NotificationService,
) {
//    fun processFiles(fileDataList: List<FileData>, userId: UserId, chatRoomId: ChatRoomId) {
//        val medias = chatLogService.uploadFiles(fileDataList, userId)
//        val chatMessage = chatLogService.mediasMessage(chatRoomId, userId, medias)
//        notificationService.handleOwnedMessageNotification(chatMessage)
//        val chatRoomInfo = roomService.activateChatRoom(chatRoomId, userId, chatMessage.number)
//        roomService.getChatRoomFriends(chatRoomId, userId, chatRoomInfo).map { it.memberId }.let {
//            notificationService.handleMessagesNotification(chatMessage, it, userId)
//        }
//    }
//
//    fun processRead(chatRoomId: ChatRoomId, userId: UserId) {
//        val chatMessage = chatLogService.readMessage(chatRoomId, userId)
//        notificationService.handleOwnedMessageNotification(chatMessage)
//        val chatRoomInfo = roomService.getChatRoom(chatRoomId)
//        roomService.updateReadChatRoom(chatRoomId, userId, chatMessage.number)
//        roomService.getChatRoomFriends(chatRoomId, userId, chatRoomInfo).map { it.memberId }.let {
//            notificationService.handleMessagesNotification(chatMessage, it, userId)
//        }
//    }
//
//    fun processDelete(chatRoomId: ChatRoomId, userId: UserId, messageId: String) {
//        val chatMessage = chatLogService.deleteMessage(chatRoomId, userId, messageId)
//        notificationService.handleOwnedMessageNotification(chatMessage)
//        val chatRoomInfo = roomService.getChatRoom(chatRoomId)
//        roomService.getChatRoomFriends(chatRoomId, userId, chatRoomInfo).map { it.memberId }.let {
//            notificationService.handleMessagesNotification(chatMessage, it, userId)
//        }
//    }
//
//    fun processReply(chatRoomId: ChatRoomId, userId: UserId, parentMessageId: String, text: String) {
//        val chatMessage = chatLogService.replyMessage(chatRoomId, userId, parentMessageId, text)
//        notificationService.handleOwnedMessageNotification(chatMessage)
//        val chatRoomInfo = roomService.activateChatRoom(chatRoomId, userId, chatMessage.number)
//        roomService.getChatRoomFriends(chatRoomId, userId, chatRoomInfo).map { it.memberId }.let {
//            notificationService.handleMessagesNotification(chatMessage, it, userId)
//        }
//    }
//
//    fun processCommon(chatRoomId: ChatRoomId, userId: UserId, text: String) {
//        val chatMessage = chatLogService.chatNormalMessage(chatRoomId, userId, text)
//        notificationService.handleOwnedMessageNotification(chatMessage)
//        val chatRoomInfo = roomService.activateChatRoom(chatRoomId, userId, chatMessage.number)
//        roomService.getChatRoomFriends(chatRoomId, userId, chatRoomInfo).map { it.memberId }.let {
//            notificationService.handleMessagesNotification(chatMessage, it, userId)
//        }
//    }
}
