package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.DirectChatRoomInfo
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Repository

@Repository
interface DirectChatRoomRepository {
    fun readInfo(chatRoomId: ChatRoomId, userId: UserId): DirectChatRoomInfo?
    fun append(userId: UserId, friendId: UserId): DirectChatRoomInfo
    fun readWithRelation(userId: UserId, friendId: UserId): DirectChatRoomInfo?
    fun readUsers(userId: UserId): List<DirectChatRoomInfo>
    fun remove(userId: UserId, chatRoomId: ChatRoomId)
    fun updateStatus(userId: UserId, chatRoomId: ChatRoomId, status: ChatRoomMemberStatus)
}
