package org.chewing.v1.implementation.chat.sequence

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.DirectChatSequence
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.ChatRoomMemberSequenceRepository
import org.chewing.v1.repository.chat.ChatRoomSequenceRepository
import org.springframework.stereotype.Component

@Component
class ChatSequenceHandler (
    private val chatRoomMemberSequenceRepository: ChatRoomMemberSequenceRepository,
    private val chatRoomSequenceRepository: ChatRoomSequenceRepository
){

    fun handleRoomIncreaseSequence(chatRoomId: ChatRoomId): DirectChatSequence {
        return chatRoomSequenceRepository.updateIncreaseSequence(chatRoomId)
    }

    fun handleMemberJoinSequence(chatRoomId: ChatRoomId, userId: UserId, chatLogSequence: DirectChatSequence): DirectChatSequence {
        return chatRoomMemberSequenceRepository.updateJoinSequence(chatRoomId, userId, chatLogSequence)
    }

    fun handleMemberReadSequence(chatRoomId: ChatRoomId, userId: UserId, chatLogSequence: DirectChatSequence): DirectChatSequence {
        return chatRoomMemberSequenceRepository.updateReadSequence(chatRoomId, userId, chatLogSequence)
    }
}
