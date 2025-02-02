package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.DirectChatLogSequence
import org.springframework.stereotype.Repository

@Repository
interface DirectChatRoomSequenceRepository {
    fun readSequence(chatRoomId: ChatRoomId): DirectChatLogSequence
    fun readsSequences(chatRoomIds: List<ChatRoomId>): List<DirectChatLogSequence>
    fun updateIncreaseSequence(chatRoomId: ChatRoomId): DirectChatLogSequence
}
