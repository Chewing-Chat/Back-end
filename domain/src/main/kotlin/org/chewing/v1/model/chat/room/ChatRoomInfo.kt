package org.chewing.v1.model.chat.room

class ChatRoomInfo private constructor(
    val chatRoomId: String,
    val isGroup: Boolean,
) {
    companion object {
        fun of(
            chatRoomId: String,
            isGroup: Boolean,
        ): ChatRoomInfo {
            return ChatRoomInfo(
                chatRoomId = chatRoomId,
                isGroup = isGroup,
            )
        }
    }
}
