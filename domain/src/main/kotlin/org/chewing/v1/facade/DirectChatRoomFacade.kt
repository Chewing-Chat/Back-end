package org.chewing.v1.facade

import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.room.ChatRoom
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.chat.ChatLogService
import org.chewing.v1.service.chat.DirectChatRoomService
import org.springframework.stereotype.Service

@Service
class DirectChatRoomFacade(
    private val chatRoomService: DirectChatRoomService,
    private val chatLogService: ChatLogService,
) {
    fun createChatRoom(userId: UserId, friendId: UserId): Pair<ChatRoomId, List<ChatLog>> {
        val chatSequence = chatRoomService.produceDirectChatRoom(userId, friendId)
        val chatLogs = chatLogService.getLatestChatLog(chatSequence.chatRoomId.id, chatSequence.sequenceNumber)
        return Pair(chatSequence.chatRoomId, chatLogs)
    }

    fun getChatRooms(userId: UserId, sort: ChatRoomSortCriteria): List<ChatRoom> {
        val roomInfos = chatRoomService.getChatRooms(userId)
        val latestChatLogs = chatLogService.getLatestChat(roomInfos.map { it.chatRoomId })
        val chatRooms = chatRoomAggregator.aggregateChatRoom(roomInfos, latestChatLogs)
        return ChatRoomSortEngine.sortChatRoom(chatRooms, sort)
    }
}
