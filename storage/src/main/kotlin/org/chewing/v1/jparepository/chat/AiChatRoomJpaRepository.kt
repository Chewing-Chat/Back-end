package org.chewing.v1.jparepository.chat

import org.chewing.v1.jpaentity.chat.AiChatRoomJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

internal interface AiChatRoomJpaRepository : JpaRepository<AiChatRoomJpaEntity, String> {
    fun findByChatRoomIdAndUserId(chatRoomId: String, userId: String): Optional<AiChatRoomJpaEntity>
}
