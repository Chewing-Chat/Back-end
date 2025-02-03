package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.DirectChatSequence
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomSequenceRepository {
    fun readSequence(chatRoomId: ChatRoomId): DirectChatSequence?
    fun readsSequences(chatRoomIds: List<ChatRoomId>): List<DirectChatSequence>
    fun updateIncreaseSequence(chatRoomId: ChatRoomId): DirectChatSequence
}
