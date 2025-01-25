package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.ChatRoomMemberType
import org.chewing.v1.model.chat.room.DirectChatRoomId
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Repository

@Repository
interface DirectChatRoomMemberRepository {
    fun append(userId: UserId, chatRoomId: DirectChatRoomId)
    fun update(userId: UserId, chatRoomId: DirectChatRoomId, status: ChatRoomMemberStatus)
    fun remove(userId: UserId, chatRoomId: DirectChatRoomId)
    fun updateType(userId: UserId, chatRoomId: DirectChatRoomId, type: ChatRoomMemberType)
}
