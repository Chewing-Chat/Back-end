package org.chewing.v1.implementation.chat.sequence

import org.chewing.v1.model.chat.room.ChatLogSequence
import org.springframework.stereotype.Component

@Component
class ChatFinder(
    private val chatSequenceReader: ChatSequenceReader,
    private val chatSequenceUpdater: ChatSequenceUpdater,
) {
    companion object {
        const val PAGE_SIZE = 50
    }


    fun findCurrentNumbers(chatRoomIds: List<String>): List<ChatLogSequence> {
        val sequenceNumbers = chatSequenceReader.readSeqNumbers(chatRoomIds)
        return sequenceNumbers.map {
            ChatLogSequence.of(it.chatRoomId, it.sequenceNumber, (it.sequenceNumber / PAGE_SIZE))
        }
    }

    fun findNextNumbers(chatRoomIds: List<String>): List<ChatLogSequence> {
        val sequenceNumbers = chatSequenceUpdater.updateSequenceIncrements(chatRoomIds)
        return sequenceNumbers.map {
            ChatLogSequence.of(it.chatRoomId, it.sequenceNumber, (it.sequenceNumber / PAGE_SIZE))
        }
    }

    fun findNextNumber(chatRoomId: String): ChatLogSequence {
        val number = chatSequenceUpdater.updateSequenceIncrement(chatRoomId)
        return ChatLogSequence.of(number.chatRoomId, number.sequenceNumber, (number.sequenceNumber / PAGE_SIZE))
    }
    fun findCurrentNumber(chatRoomId: String): ChatLogSequence {
        val number = chatSequenceReader.readCurrent(chatRoomId)
        return ChatLogSequence.of(number.chatRoomId, number.sequenceNumber, (number.sequenceNumber / PAGE_SIZE))
    }
}
