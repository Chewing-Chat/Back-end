package org.chewing.v1.jpaentity.chat

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class ChatRoomMemberId(
    @Column(name = "chat_room_id")
    val chatRoomId: String,
    @Column(name = "user_id")
    val userId: String,
) : Serializable
