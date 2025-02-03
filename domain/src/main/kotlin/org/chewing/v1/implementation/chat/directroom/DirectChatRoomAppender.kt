package org.chewing.v1.implementation.chat.directroom

import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.DirectChatRoomRepository
import org.springframework.stereotype.Component

@Component
class DirectChatRoomAppender(
    private val directChatRoomRepository: DirectChatRoomRepository,
) {
    fun appendRoom(userId: UserId, friendId: UserId) = directChatRoomRepository.append(userId, friendId)
}
