package org.chewing.v1.repository.jpa.chat

import org.chewing.v1.jpaentity.chat.AiChatRoomJpaEntity
import org.chewing.v1.jparepository.chat.AiChatRoomJpaRepository
import org.chewing.v1.model.chat.room.AiChatRoomInfo
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.AiChatRoomRepository
import org.springframework.stereotype.Repository

@Repository
internal class AiChatRoomRepositoryImpl(
    private val aiChatRoomJpaRepository: AiChatRoomJpaRepository,
) : AiChatRoomRepository {
    override fun append(userId: UserId): ChatRoomId {
        return aiChatRoomJpaRepository.save(AiChatRoomJpaEntity.generate(userId)).toChatRoomId()
    }

    override fun readInfo(
        chatRoomId: ChatRoomId,
        userId: UserId,
    ): AiChatRoomInfo? {
        return aiChatRoomJpaRepository.findByChatRoomIdAndUserId(chatRoomId.id, userId.id).map { it.toAiChatRoomInfo() }
            .orElse(null)
    }
}
