package org.chewing.v1.implementation.chat.sequence

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberSequence
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.ChatRoomMemberSequenceRepository
import org.chewing.v1.repository.chat.ChatRoomSequenceRepository
import org.springframework.stereotype.Component

@Component
class ChatSequenceHandler(
    private val chatRoomMemberSequenceRepository: ChatRoomMemberSequenceRepository,
    private val chatRoomSequenceRepository: ChatRoomSequenceRepository,
) {

    fun handleRoomIncreaseSequence(chatRoomId: ChatRoomId): ChatRoomSequence {
        return chatRoomSequenceRepository.updateIncreaseSequence(chatRoomId)
    }

    fun handleJoinMemberSequence(chatRoomId: ChatRoomId, userId: UserId, chatLogSequence: ChatRoomSequence): ChatRoomMemberSequence {
        return chatRoomMemberSequenceRepository.updateJoinSequence(chatRoomId, userId, chatLogSequence) ?: throw ConflictException(
            ErrorCode.CHATROOM_JOIN_FAILED,
        )
    }

    fun handleCreateMemberSequences(chatRoomId: ChatRoomId, memberIds: List<UserId>) {
        memberIds.forEach { userId ->
            chatRoomMemberSequenceRepository.appendSequence(chatRoomId, userId)
        }
    }

    fun handleCreateRoomSequence(chatRoomId: ChatRoomId) {
        chatRoomSequenceRepository.appendSequence(chatRoomId)
    }

    fun handleMemberReadSequence(chatRoomId: ChatRoomId, userId: UserId, sequenceNumber: Int): ChatRoomMemberSequence {
        return chatRoomMemberSequenceRepository.updateReadSequence(chatRoomId, userId, sequenceNumber) ?: throw ConflictException(
            ErrorCode.CHATROOM_READ_FAILED,
        )
    }
}
