package org.chewing.v1.model.chat.log

import org.chewing.v1.model.chat.room.ChatRoomId

class UnReadTarget private constructor(
    val chatRoomId: ChatRoomId,
    val chatRoomSequence: Int,
    val readSequence: Int,
) {
    companion object {
        fun of(
            chatRoomId: ChatRoomId,
            chatRoomSequence: Int,
            readSequence: Int,
        ): UnReadTarget {
            return UnReadTarget(
                chatRoomId = chatRoomId,
                chatRoomSequence = chatRoomSequence,
                readSequence = readSequence,
            )
        }
    }
}
