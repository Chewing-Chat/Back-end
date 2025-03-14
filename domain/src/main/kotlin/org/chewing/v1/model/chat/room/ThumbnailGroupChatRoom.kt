package org.chewing.v1.model.chat.room

import org.chewing.v1.model.chat.log.ChatLog

class ThumbnailGroupChatRoom private constructor(
    val chatRoom: GroupChatRoom,
    val chatLog: ChatLog,
) {
    companion object {
        fun of(groupChatRoom: GroupChatRoom, chatLog: ChatLog): ThumbnailGroupChatRoom {
            return ThumbnailGroupChatRoom(groupChatRoom, chatLog)
        }
    }
}
