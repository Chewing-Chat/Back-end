package org.chewing.v1.implementation.chat.sequence

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberSequence
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.ChatRoomMemberSequenceRepository
import org.chewing.v1.repository.chat.ChatRoomSequenceRepository
import org.springframework.stereotype.Component

@Component
class ChatSequenceFinder(
    private val chatRoomMemberSequenceRepository: ChatRoomMemberSequenceRepository,
    private val chatRoomSequenceRepository: ChatRoomSequenceRepository,
) {
    fun findCurrentRoomSequence(chatRoomId: ChatRoomId): ChatRoomSequence {
        return chatRoomSequenceRepository.readSequence(chatRoomId) ?: ChatRoomSequence.of(chatRoomId, 0)
    }

    fun findCurrentMemberSequences(chatRoomIds: List<ChatRoomId>, userId: UserId): List<ChatRoomMemberSequence> {
        return chatRoomMemberSequenceRepository.readsSequences(chatRoomIds, userId)
    }

    fun findCurrentMemberSequence(chatRoomId: ChatRoomId, userId: UserId): ChatRoomMemberSequence {
        return chatRoomMemberSequenceRepository.readSequence(chatRoomId, userId) ?: throw NotFoundException(
            ErrorCode.CHATROOM_FIND_FAILED,
        )
    }

    fun findCurrentRoomSequences(chatRoomIds: List<ChatRoomId>): List<ChatRoomSequence> {
        return chatRoomSequenceRepository.readsSequences(chatRoomIds)
    }
}
