package org.chewing.v1.jpaentity.chat

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class PersonalChatRoomMemberId(
    @Column(name = "chat_room_id")
    val chatRoomId: String,

    @Column(name = "user_id")
    val userId: String,

    @Column(name = "friend_id")
    val friendId: String,
) : Serializable
