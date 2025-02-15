package org.chewing.v1.model.chat.room

@JvmInline
value class ChatRoomId private constructor(
    val id: String,
) {
    companion object {
        fun of(
            id: String,
        ): ChatRoomId {
            return ChatRoomId(id)
        }
    }
}
