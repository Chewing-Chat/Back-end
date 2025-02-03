package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.member.ChatRoomMemberInfo
import org.chewing.v1.model.chat.room.ChatNumber
import org.chewing.v1.model.user.UserId

interface GroupChatRoomMemberRepository {
    fun updateFavorite(chatRoomId: String, userId: UserId, isFavorite: Boolean)
    fun reads(userId: UserId): List<ChatRoomMemberInfo>
    fun removes(chatRoomIds: List<String>, userId: UserId)
    fun updateRead(userId: UserId, number: ChatNumber)
    fun readFriends(chatRoomId: String, userId: UserId): List<ChatRoomMemberInfo>
    fun appends(chatRoomId: String, userIds: List<UserId>, number: ChatNumber)
    fun append(chatRoomId: String, userId: UserId, number: ChatNumber)
}
