package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomSequenceRepository {
    fun readSequence(chatRoomId: ChatRoomId): ChatRoomSequence?
    fun readsSequences(chatRoomIds: List<ChatRoomId>): List<ChatRoomSequence>
    fun updateIncreaseSequence(chatRoomId: ChatRoomId): ChatRoomSequence
    fun appendSequence(chatRoomId: ChatRoomId)
}
