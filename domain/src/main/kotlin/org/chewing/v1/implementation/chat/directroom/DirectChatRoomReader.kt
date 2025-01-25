package org.chewing.v1.implementation.chat.directroom

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.DirectChatRoomRepository
import org.springframework.stereotype.Component

@Component
class DirectChatRoomReader(
    private val directChatRoomRepository: DirectChatRoomRepository
) {
    fun readRoomInfo(userId: UserId, friendId: UserId) = directChatRoomRepository.readInfo(userId, friendId) ?: throw NotFoundException(
        ErrorCode.CHATROOM_NOT_FOUND)
}
