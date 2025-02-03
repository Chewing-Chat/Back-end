package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.room.ChatRoom
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.DirectChatSequence
import org.chewing.v1.model.user.UserId

interface ChatRoomMemberSequenceRepository {
    fun updateReadSequence(chatRoomId: ChatRoomId, userId: UserId, chatLogSequence: DirectChatSequence) :DirectChatSequence
    fun updateJoinSequence(chatRoomId: ChatRoomId, userId: UserId, chatLogSequence: DirectChatSequence) :DirectChatSequence
    fun readsSequences(chatRoomIds: List<ChatRoomId>, userId: UserId): List<DirectChatSequence>
}
