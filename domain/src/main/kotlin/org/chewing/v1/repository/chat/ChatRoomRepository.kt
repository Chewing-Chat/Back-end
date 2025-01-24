package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.room.ChatRoomInfo

interface ChatRoomRepository {
    fun appendChatRoom(isGroup: Boolean): String

    fun readChatRooms(chatRoomIds: List<String>): List<ChatRoomInfo>
    fun readChatRoom(chatRoomId: String): ChatRoomInfo?

    fun isGroupChatRoom(chatRoomId: String): Boolean
}
