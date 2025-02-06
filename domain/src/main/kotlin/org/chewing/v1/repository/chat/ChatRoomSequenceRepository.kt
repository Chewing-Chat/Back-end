package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatSequence
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomSequenceRepository {
    fun readSequence(chatRoomId: ChatRoomId): ChatSequence?
    fun readsSequences(chatRoomIds: List<ChatRoomId>): List<ChatSequence>
    fun updateIncreaseSequence(chatRoomId: ChatRoomId): ChatSequence
    fun appendSequence(chatRoomId: ChatRoomId) : ChatSequence
}
