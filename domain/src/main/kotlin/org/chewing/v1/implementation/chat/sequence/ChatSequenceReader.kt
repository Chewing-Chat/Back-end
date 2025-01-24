package org.chewing.v1.implementation.chat.sequence

import org.chewing.v1.model.chat.room.ChatSequenceNumber
import org.chewing.v1.repository.chat.ChatSequenceRepository
import org.springframework.stereotype.Component

@Component
class ChatSequenceReader(
    private val chatSequenceRepository: ChatSequenceRepository,
) {
    fun readCurrent(chatRoomId: String): ChatSequenceNumber {
        return chatSequenceRepository.readCurrent(chatRoomId)
    }
    fun readSeqNumbers(chatRoomIds: List<String>): List<ChatSequenceNumber> {
        return chatSequenceRepository.readCurrentSeqNumbers(chatRoomIds)
    }
}
