package org.chewing.v1.model.chat.room

import org.chewing.v1.model.chat.log.ChatLog

class ThumbnailDirectChatRoom private constructor(
    val chatRoom: DirectChatRoom,
    val chatLog: ChatLog,
) {
    companion object {
        fun of(directChatRoom: DirectChatRoom, chatLog: ChatLog): ThumbnailDirectChatRoom {
            return ThumbnailDirectChatRoom(directChatRoom, chatLog)
        }
    }
}
