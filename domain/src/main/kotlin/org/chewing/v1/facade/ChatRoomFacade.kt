package org.chewing.v1.facade

import org.chewing.v1.implementation.chat.room.ChatRoomAggregator
import org.chewing.v1.implementation.chat.room.ChatRoomSortEngine
import org.chewing.v1.model.chat.room.ChatRoom
import org.chewing.v1.model.chat.room.ChatRoomSortCriteria
import org.chewing.v1.service.chat.ChatLogService
import org.chewing.v1.service.chat.RoomService
import org.chewing.v1.service.notification.NotificationService
import org.springframework.stereotype.Service

@Service
class ChatRoomFacade(
    private val chatLogService: ChatLogService,
    private val roomService: RoomService,
    private val chatRoomAggregator: ChatRoomAggregator,
    private val notificationService: NotificationService,
) {
    fun leavesChatRoom(chatRoomIds: List<String>, userId: String) {
        roomService.deleteGroupChatRooms(chatRoomIds, userId)
        val chatMessages = chatLogService.leaveMessages(chatRoomIds, userId)
        chatMessages.forEach {
            val chatRoomInfo = roomService.activateChatRoom(it.chatRoomId, userId, it.number)
            val membersInfo = roomService.getChatRoomFriends(it.chatRoomId, userId, chatRoomInfo)
            notificationService.handleMessagesNotification(it, membersInfo.map { member -> member.memberId }, userId)
        }
    }

    fun createGroupChatRoom(userId: String, friendIds: List<String>): String {
        val newRoomId = roomService.createGroupChatRoom(userId, friendIds)
        val chatMessage = chatLogService.inviteMessages(friendIds, newRoomId, userId)
        notificationService.handleMessagesNotification(chatMessage, friendIds, userId)
        return newRoomId
    }

    fun getChatRooms(userId: String, sort: ChatRoomSortCriteria): List<ChatRoom> {
        val roomInfos = roomService.getChatRooms(userId)
        val latestChatLogs = chatLogService.getLatestChat(roomInfos.map { it.chatRoomId })
        val chatRooms = chatRoomAggregator.aggregateChatRoom(roomInfos, latestChatLogs)
        return ChatRoomSortEngine.sortChatRoom(chatRooms, sort)
    }

    fun inviteChatRoom(userId: String, chatRoomId: String, friendId: String) {
        roomService.inviteChatRoom(chatRoomId, friendId, userId)
        val chatMessage = chatLogService.inviteMessage(chatRoomId, friendId, userId)
        val chatRoomInfo = roomService.activateChatRoom(chatRoomId, userId, chatMessage.number)
        val membersInfo = roomService.getChatRoomFriends(chatRoomId, userId, chatRoomInfo)
        notificationService.handleMessagesNotification(chatMessage, membersInfo.map { it.memberId }, userId)
    }
}
