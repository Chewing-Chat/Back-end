package org.chewing.v1.implementation.chat.directroom

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.DirectChatRoomMemberRepository
import org.chewing.v1.repository.chat.DirectChatRoomMemberSequenceRepository
import org.chewing.v1.repository.chat.DirectChatRoomRepository
import org.springframework.stereotype.Component

@Component
class DirectChatRoomAppender(
    private val directChatRoomRepository: DirectChatRoomRepository,
    private val directChatRoomMemberRepository: DirectChatRoomMemberRepository,
    private val directChatRoomMemberSequenceRepository: DirectChatRoomMemberSequenceRepository
) {
    fun appendRoom(userId: UserId, friendId: UserId) = directChatRoomRepository.append(userId, friendId)
    fun appendMember(userId: UserId, chatRoomId: ChatRoomId) {
        directChatRoomMemberRepository.append(userId, chatRoomId)
    }
    fun appendSequence(chatRoomId: ChatRoomId, userId: UserId) {
        directChatRoomMemberSequenceRepository.appendSequence(chatRoomId, userId)
    }
}
