package org.chewing.v1.implementation.chat.directroom

import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.ChatRoomMemberType
import org.chewing.v1.model.chat.room.DirectChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.DirectChatRoomMemberRepository
import org.springframework.stereotype.Component

@Component
class DirectChatRoomUpdater(
    private val directChatRoomMemberRepository: DirectChatRoomMemberRepository
) {
    fun updateStatus(userId: UserId, chatRoomId: DirectChatRoomId, status: ChatRoomMemberStatus) {
        directChatRoomMemberRepository.update(userId, chatRoomId,status)
    }
    fun updateType(userId: UserId, chatRoomId: DirectChatRoomId, type: ChatRoomMemberType) {
        directChatRoomMemberRepository.updateType(userId, chatRoomId,type)
    }
}
