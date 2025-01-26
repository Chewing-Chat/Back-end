package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.room.ChatLogSequence
import org.chewing.v1.model.chat.room.ChatRoom
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.DirectChatLogSequence
import org.chewing.v1.model.chat.room.DirectChatRoomMemberInfo
import org.chewing.v1.model.user.UserId

interface DirectChatRoomMemberSequenceRepository {
    fun updateReadSequence(chatRoomId: ChatRoomId, userId: UserId, chatLogSequence: ChatLogSequence)
    fun updateStartSequence(chatRoomId: ChatRoomId, userId: UserId, chatLogSequence: ChatLogSequence)
    fun appendSequence(chatRoomId: ChatRoomId, userId: UserId)
    fun readSequence(chatRoomId: ChatRoomId, userId: UserId): DirectChatLogSequence
    fun readUserRooms(userId: UserId): List<ChatRoom>
}
