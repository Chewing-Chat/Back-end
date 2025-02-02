package org.chewing.v1.implementation.chat.directroom

import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.ChatRoomMemberType
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.DirectChatLogSequence
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.DirectChatRoomMemberRepository
import org.chewing.v1.repository.chat.DirectChatRoomMemberSequenceRepository
import org.chewing.v1.repository.chat.DirectChatRoomSequenceRepository
import org.springframework.stereotype.Component

@Component
class DirectChatRoomUpdater(
    private val directChatRoomMemberRepository: DirectChatRoomMemberRepository,
    private val directChatRoomSequenceRepository: DirectChatRoomSequenceRepository,
    private val directChatRoomMemberSequenceRepository: DirectChatRoomMemberSequenceRepository
) {
    fun updateMemberStatus(userId: UserId, chatRoomId: ChatRoomId, status: ChatRoomMemberStatus) {
        directChatRoomMemberRepository.update(userId, chatRoomId, status)
    }

    fun updateRoomType(userId: UserId, chatRoomId: ChatRoomId, type: ChatRoomMemberType) {
        directChatRoomMemberRepository.updateType(userId, chatRoomId, type)
    }

    fun updateMemberJoinSequence(userId: UserId, chatRoomId: ChatRoomId, chatLogSequence: DirectChatLogSequence):DirectChatLogSequence {
        return directChatRoomMemberSequenceRepository.updateJoinSequence(chatRoomId, userId, chatLogSequence)
    }

    fun updateMemberReadSequence(userId: UserId, chatRoomId: ChatRoomId, chatLogSequence: DirectChatLogSequence) {
        directChatRoomMemberSequenceRepository.updateReadSequence(chatRoomId, userId, chatLogSequence)
    }

    fun updateIncreaseRoomSequence(chatRoomId: ChatRoomId): DirectChatLogSequence {
        return directChatRoomSequenceRepository.updateIncreaseSequence(chatRoomId)
    }
}
