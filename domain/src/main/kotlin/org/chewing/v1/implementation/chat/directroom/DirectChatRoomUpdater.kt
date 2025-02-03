package org.chewing.v1.implementation.chat.directroom

import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.ChatRoomMemberType
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.DirectChatRoomMemberRepository
import org.springframework.stereotype.Component

@Component
class DirectChatRoomUpdater(
    private val directChatRoomMemberRepository: DirectChatRoomMemberRepository,
) {
    fun updateMemberStatus(userId: UserId, chatRoomId: ChatRoomId, status: ChatRoomMemberStatus) {
        directChatRoomMemberRepository.update(userId, chatRoomId, status)
    }

    fun updateRoomType(userId: UserId, chatRoomId: ChatRoomId, type: ChatRoomMemberType) {
        directChatRoomMemberRepository.updateType(userId, chatRoomId, type)
    }
}
