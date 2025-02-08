package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberSequence
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.user.UserId

interface ChatRoomMemberSequenceRepository {
    fun updateReadSequence(chatRoomId: ChatRoomId, userId: UserId, sequenceNumber: Int): ChatRoomMemberSequence?
    fun updateJoinSequence(chatRoomId: ChatRoomId, userId: UserId, chatLogSequence: ChatRoomSequence): ChatRoomMemberSequence?
    fun readsSequences(chatRoomIds: List<ChatRoomId>, userId: UserId): List<ChatRoomMemberSequence>
    fun readSequence(chatRoomId: ChatRoomId, userId: UserId): ChatRoomMemberSequence?
    fun appendSequence(chatRoomId: ChatRoomId, userId: UserId)
}
