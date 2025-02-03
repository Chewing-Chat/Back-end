package org.chewing.v1.implementation.chat.room

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.repository.chat.ChatRoomRepository
import org.springframework.stereotype.Component

@Component
class ChatRoomValidator(
    private val chatRoomRepository: ChatRoomRepository,
) {
    fun validateGroupChatRoom(chatRoomId: String) {
        if (!chatRoomRepository.isGroupChatRoom(chatRoomId)) {
            throw ConflictException(ErrorCode.CHATROOM_IS_NOT_GROUP)
        }
    }
}
