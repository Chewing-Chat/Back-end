package org.chewing.v1.implementation.chat.sequence

import org.chewing.v1.model.chat.room.ChatNumber
import org.springframework.stereotype.Component

@Component
class ChatFinder(
    private val chatSequenceReader: ChatSequenceReader,
    private val chatSequenceUpdater: ChatSequenceUpdater,
) {
    companion object {
        const val PAGE_SIZE = 50
    }

//    fun findLastPage(chatRoomId: String): Int {
//        return (chatSequenceReader.readCurrent(chatRoomId).sequenceNumber / PAGE_SIZE)
//    }

    fun findCurrentNumbers(chatRoomIds: List<String>): List<ChatNumber> {
        val sequenceNumbers = chatSequenceReader.readSeqNumbers(chatRoomIds)
        return sequenceNumbers.map {
            ChatNumber.of(it.chatRoomId, it.sequenceNumber, (it.sequenceNumber / PAGE_SIZE))
        }
    }

    fun findNextNumbers(chatRoomIds: List<String>): List<ChatNumber> {
        val sequenceNumbers = chatSequenceUpdater.updateSequenceIncrements(chatRoomIds)
        return sequenceNumbers.map {
            ChatNumber.of(it.chatRoomId, it.sequenceNumber, (it.sequenceNumber / PAGE_SIZE))
        }
    }

    fun findNextNumber(chatRoomId: String): ChatNumber {
        val number = chatSequenceUpdater.updateSequenceIncrement(chatRoomId)
        return ChatNumber.of(number.chatRoomId, number.sequenceNumber, (number.sequenceNumber / PAGE_SIZE))
    }
    fun findCurrentNumber(chatRoomId: String): ChatNumber {
        val number = chatSequenceReader.readCurrent(chatRoomId)
        return ChatNumber.of(number.chatRoomId, number.sequenceNumber, (number.sequenceNumber / PAGE_SIZE))
    }
}
