package org.chewing.v1.implementation.chat.airoom

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.AiChatRoomRepository
import org.springframework.stereotype.Component

@Component
class AiChatRoomReader(
    private val aiChatRoomRepository: AiChatRoomRepository
) {
}
