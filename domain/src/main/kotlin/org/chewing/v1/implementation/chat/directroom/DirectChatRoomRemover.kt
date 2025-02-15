package org.chewing.v1.implementation.chat.directroom

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.DirectChatRoomRepository
import org.springframework.stereotype.Component

@Component
class DirectChatRoomRemover(
    private val directChatRoomRepository: DirectChatRoomRepository,
) {
    fun removeMember(userId: UserId, chatRoomId: ChatRoomId) {
        directChatRoomRepository.remove(userId, chatRoomId)
    }
}
