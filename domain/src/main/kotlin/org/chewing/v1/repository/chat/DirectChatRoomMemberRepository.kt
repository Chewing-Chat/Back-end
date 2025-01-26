package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.room.ChatRoom
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.ChatRoomMemberType
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.DirectChatRoomMemberInfo
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Repository

@Repository
interface DirectChatRoomMemberRepository {
    fun append(userId: UserId, chatRoomId: ChatRoomId)
    fun update(userId: UserId, chatRoomId: ChatRoomId, status: ChatRoomMemberStatus)
    fun remove(userId: UserId, chatRoomId: ChatRoomId)
    fun updateType(userId: UserId, chatRoomId: ChatRoomId, type: ChatRoomMemberType)
    fun readInfo(userId: UserId, chatRoomId: ChatRoomId): DirectChatRoomMemberInfo
    fun readsMemberInfos(chatRoomIds: List<ChatRoomId>, userId: UserId): List<DirectChatRoomMemberInfo>
}
