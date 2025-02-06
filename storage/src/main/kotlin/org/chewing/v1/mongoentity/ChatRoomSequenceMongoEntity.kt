package org.chewing.v1.mongoentity

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatSequence
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class ChatRoomSequenceMongoEntity(
    @Id
    val chatRoomId: String,
    var seqNumber: Int,
){
    companion object {
        fun generate(chatRoomId: ChatRoomId): ChatRoomSequenceMongoEntity {
            return ChatRoomSequenceMongoEntity(
                chatRoomId = chatRoomId.id,
                seqNumber = 0,
            )
        }
    }

    fun toChatRoomSequence(): ChatSequence {
        return ChatSequence.of(
            chatRoomId = ChatRoomId.of(chatRoomId),
            sequenceNumber = seqNumber,
        )
    }
}
