package org.chewing.v1.model.chat.room

@JvmInline
value class DirectChatRoomId private constructor(
    val id: String,
) {
    companion object {
        fun of(
            id: String,
        ): DirectChatRoomId {
            return DirectChatRoomId(id)
        }
    }
}
