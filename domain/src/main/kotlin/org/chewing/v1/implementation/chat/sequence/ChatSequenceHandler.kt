package org.chewing.v1.implementation.chat.sequence

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatSequence
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.ChatRoomMemberSequenceRepository
import org.chewing.v1.repository.chat.ChatRoomSequenceRepository
import org.springframework.stereotype.Component

@Component
class ChatSequenceHandler(
    private val chatRoomMemberSequenceRepository: ChatRoomMemberSequenceRepository,
    private val chatRoomSequenceRepository: ChatRoomSequenceRepository,
) {

    fun handleRoomIncreaseSequence(chatRoomId: ChatRoomId): ChatSequence {
        return chatRoomSequenceRepository.updateIncreaseSequence(chatRoomId)
    }

    fun handleJoinMemberSequence(chatRoomId: ChatRoomId, userId: UserId, chatLogSequence: ChatSequence): ChatSequence {
        return chatRoomMemberSequenceRepository.updateJoinSequence(chatRoomId, userId, chatLogSequence)
    }

    fun handleCreateMemberSequences(chatRoomId: ChatRoomId, memberIds : List<UserId>) {
        memberIds.forEach { userId ->
            chatRoomMemberSequenceRepository.appendSequence(chatRoomId, userId)
        }
    }

    fun handleCreateRoomSequence(chatRoomId: ChatRoomId): ChatSequence {
        return chatRoomSequenceRepository.appendSequence(chatRoomId)
    }

    fun handleMemberReadSequence(chatRoomId: ChatRoomId, userId: UserId, chatLogSequence: ChatSequence): ChatSequence {
        return chatRoomMemberSequenceRepository.updateReadSequence(chatRoomId, userId, chatLogSequence)
    }
}
