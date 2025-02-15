package org.chewing.v1.implementation.chat.directroom

import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.DirectChatRoomRepository
import org.springframework.stereotype.Component

@Component
class DirectChatRoomUpdater(
    private val directChatRoomRepository: DirectChatRoomRepository,
) {
    fun updateMemberStatus(userId: UserId, chatRoomId: ChatRoomId, status: ChatRoomMemberStatus) {
        directChatRoomRepository.updateStatus(userId, chatRoomId, status)
    }
}
