package org.chewing.v1.implementation.chat.directroom

import org.chewing.v1.model.chat.room.ChatLogSequence
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.ChatRoomMemberType
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.DirectChatRoomMemberRepository
import org.chewing.v1.repository.chat.DirectChatRoomMemberSequenceRepository
import org.springframework.stereotype.Component

@Component
class DirectChatRoomUpdater(
    private val directChatRoomMemberRepository: DirectChatRoomMemberRepository,
    private val directChatRoomSequenceRepository: DirectChatRoomMemberSequenceRepository,
) {
    fun updateMemberStatus(userId: UserId, chatRoomId: ChatRoomId, status: ChatRoomMemberStatus) {
        directChatRoomMemberRepository.update(userId, chatRoomId, status)
    }

    fun updateType(userId: UserId, chatRoomId: ChatRoomId, type: ChatRoomMemberType) {
        directChatRoomMemberRepository.updateType(userId, chatRoomId, type)
    }

    fun updateStartSequence(userId: UserId, chatRoomId: ChatRoomId, chatLogSequence: ChatLogSequence) {
        directChatRoomSequenceRepository.updateStartSequence(chatRoomId, userId, chatLogSequence)
    }

    fun updateReadSequence(userId: UserId, chatRoomId: ChatRoomId, chatLogSequence: ChatLogSequence) {
        directChatRoomSequenceRepository.updateReadSequence(chatRoomId, userId, chatLogSequence)
    }
}
