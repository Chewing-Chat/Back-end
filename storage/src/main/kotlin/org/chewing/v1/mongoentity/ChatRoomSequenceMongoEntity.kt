package org.chewing.v1.mongoentity

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class ChatRoomSequenceMongoEntity(
    @Id
    val chatRoomId: String,
    var sequence: Int,
) {
    companion object {
        fun generate(chatRoomId: ChatRoomId): ChatRoomSequenceMongoEntity {
            return ChatRoomSequenceMongoEntity(
                chatRoomId = chatRoomId.id,
                sequence = 0,
            )
        }
    }

    fun toChatRoomSequence(): ChatRoomSequence {
        return ChatRoomSequence.of(
            chatRoomId = ChatRoomId.of(chatRoomId),
            sequence = sequence,
        )
    }
}
