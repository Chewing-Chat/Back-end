package org.chewing.v1.jpaentity.chat

import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.chewing.v1.model.chat.room.AiChatRoomInfo
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.user.UserId
import org.hibernate.annotations.DynamicInsert
import java.util.UUID

@DynamicInsert
@Entity
@Table(name = "ai_chat_room", schema = "chewing")
class AiChatRoomJpaEntity(
    @Id
    private val chatRoomId: String = UUID.randomUUID().toString(),

    private val userId: String,

    @Enumerated
    var status: ChatRoomMemberStatus,
) {
    companion object {
        fun generate(userId: UserId): AiChatRoomJpaEntity {
            return AiChatRoomJpaEntity(
                userId = userId.id,
                status = ChatRoomMemberStatus.NORMAL,
            )
        }
    }

    fun toChatRoomId(): ChatRoomId {
        return ChatRoomId.of(chatRoomId)
    }

    fun toAiChatRoomInfo(): AiChatRoomInfo {
        return AiChatRoomInfo.of(
            chatRoomId = ChatRoomId.of(chatRoomId),
            userId = UserId.of(userId),
            status = status,
        )
    }
}
