package org.chewing.v1.implementation.chat.sequence

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatSequence
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.ChatRoomMemberSequenceRepository
import org.chewing.v1.repository.chat.ChatRoomSequenceRepository
import org.springframework.stereotype.Component

@Component
class ChatSequenceFinder(
    private val chatRoomMemberSequenceRepository: ChatRoomMemberSequenceRepository,
    private val chatRoomSequenceRepository: ChatRoomSequenceRepository,
) {
    fun findCurrentRoomSequence(chatRoomId: ChatRoomId): ChatSequence {
        return chatRoomSequenceRepository.readSequence(chatRoomId) ?: ChatSequence.of(chatRoomId, 0)
    }

    fun findCurrentMemberSequences(chatRoomIds: List<ChatRoomId>, userId: UserId): List<ChatSequence> {
        val chatRoomSequences = chatRoomMemberSequenceRepository.readsSequences(chatRoomIds, userId)
        val sequenceMap = chatRoomSequences.associateBy { it.chatRoomId }

        return chatRoomIds.map { chatRoomId ->
            val sequence = sequenceMap[chatRoomId]
            ChatSequence.of(chatRoomId, sequence?.sequenceNumber ?: 0)
        }
    }

    fun findCurrentMemberSequence(chatRoomId: ChatRoomId, userId: UserId): ChatSequence {
        return chatRoomMemberSequenceRepository.readSequences(chatRoomId, userId) ?: ChatSequence.of(chatRoomId, 0)
    }

    fun findCurrentRoomSequences(chatRoomIds: List<ChatRoomId>): List<ChatSequence> {
        val chatRoomSequences = chatRoomSequenceRepository.readsSequences(chatRoomIds)
        val sequenceMap = chatRoomSequences.associateBy { it.chatRoomId }

        return chatRoomIds.map { chatRoomId ->
            val sequence = sequenceMap[chatRoomId]
            ChatSequence.of(chatRoomId, sequence?.sequenceNumber ?: 0)
        }
    }
}
