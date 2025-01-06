package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.member.ChatRoomMemberInfo
import org.chewing.v1.model.chat.room.ChatNumber

interface GroupChatRoomMemberRepository {
    fun updateFavorite(chatRoomId: String, userId: String, isFavorite: Boolean)
    fun reads(userId: String): List<ChatRoomMemberInfo>
    fun removes(chatRoomIds: List<String>, userId: String)
    fun updateRead(userId: String, number: ChatNumber)
    fun readFriends(chatRoomId: String, userId: String): List<ChatRoomMemberInfo>
    fun appends(chatRoomId: String, userIds: List<String>, number: ChatNumber)
    fun append(chatRoomId: String, userId: String, number: ChatNumber)
}
