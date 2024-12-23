package org.chewing.v1.implementation.chat.sequence

import org.chewing.v1.model.chat.room.ChatSequenceNumber
import org.chewing.v1.repository.chat.ChatSequenceRepository
import org.springframework.stereotype.Component

@Component
class ChatSequenceUpdater(private val chatSequenceRepository: ChatSequenceRepository) {

    fun updateSequenceIncrement(chatRoomId: String): ChatSequenceNumber {
        return chatSequenceRepository.updateSequenceIncrement(chatRoomId)
    }

    fun updateSequenceIncrements(chatRoomIds: List<String>): List<ChatSequenceNumber> {
        return chatSequenceRepository.updateSequenceIncrements(chatRoomIds)
    }
}
