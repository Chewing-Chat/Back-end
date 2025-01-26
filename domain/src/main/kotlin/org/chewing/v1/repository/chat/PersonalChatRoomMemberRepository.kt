package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.member.ChatRoomMemberInfo
import org.chewing.v1.model.chat.room.ChatLogSequence
import org.chewing.v1.model.user.UserId

interface PersonalChatRoomMemberRepository {
    fun readFriend(chatRoomId: String, userId: UserId): ChatRoomMemberInfo?
    fun appendIfNotExist(chatRoomId: String, userId: UserId, friendId: UserId, number: ChatLogSequence)
    fun updateRead(userId: UserId, number: ChatLogSequence)
    fun updateFavorite(chatRoomId: String, userId: UserId, isFavorite: Boolean)
    fun removes(chatRoomIds: List<String>, userId: UserId)
    fun reads(userId: UserId): List<ChatRoomMemberInfo>
    fun readIdIfExist(userId: UserId, friendId: UserId): String?
}
