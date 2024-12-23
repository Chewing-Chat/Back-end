package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.member.ChatRoomMemberInfo
import org.chewing.v1.model.chat.room.ChatNumber

interface PersonalChatRoomMemberRepository {
    fun readFriend(chatRoomId: String, userId: String): ChatRoomMemberInfo?
    fun appendIfNotExist(chatRoomId: String, userId: String, friendId: String, number: ChatNumber)
    fun updateRead(userId: String, number: ChatNumber)
    fun updateFavorite(chatRoomId: String, userId: String, isFavorite: Boolean)
    fun removes(chatRoomIds: List<String>, userId: String)
    fun reads(userId: String): List<ChatRoomMemberInfo>
    fun readIdIfExist(userId: String, friendId: String): String?
}
