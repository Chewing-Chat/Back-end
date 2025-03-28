package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.GroupChatRoomMemberInfo
import org.chewing.v1.model.user.UserId

interface GroupChatRoomMemberRepository {
    fun append(chatRoomId: ChatRoomId, userId: UserId)
    fun read(chatRoomId: ChatRoomId): List<GroupChatRoomMemberInfo>
    fun readUsers(userId: UserId): List<GroupChatRoomMemberInfo>
    fun remove(chatRoomId: ChatRoomId, userId: UserId)
    fun updateStatus(chatRoomId: ChatRoomId, userId: UserId, status: ChatRoomMemberStatus)
    fun checkParticipant(chatRoomId: ChatRoomId, userId: UserId): Boolean
    fun readsAllInfos(chatRoomIds: List<ChatRoomId>): List<GroupChatRoomMemberInfo>
}
