package org.chewing.v1.repository.jpa.chat

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.AiChatRoomRepository
import org.springframework.stereotype.Repository


@Repository
internal class AiChatRoomRepositoryImpl(): AiChatRoomRepository {
    override fun append(userId: UserId): ChatRoomId {
        TODO("Not yet implemented")
    }
}
