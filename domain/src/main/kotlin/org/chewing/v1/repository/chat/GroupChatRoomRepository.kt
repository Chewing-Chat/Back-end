package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.GroupChatRoomInfo

interface GroupChatRoomRepository {
    fun append(groupName: String): GroupChatRoomInfo
    fun readRoomInfos(chatRoomIds: List<ChatRoomId>): List<GroupChatRoomInfo>
    fun readRoomInfo(chatRoomId: ChatRoomId): GroupChatRoomInfo?
}
