package org.chewing.v1.jpaentity.chat

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import java.io.Serializable

@Embeddable
data class ChatRoomMemberId(
    @Column(name = "chat_room_id")
    val chatRoomId: String,
    @Column(name = "user_id")
    val userId: String,
) : Serializable {
    companion object {
        fun of(chatRoomId: ChatRoomId, userId: UserId): ChatRoomMemberId {
            return ChatRoomMemberId(chatRoomId.id, userId.id)
        }
    }
}
