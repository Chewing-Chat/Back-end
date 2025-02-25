package org.chewing.v1.mongoentity

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberSequence
import org.chewing.v1.model.user.UserId
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "chatRoomMemberSequences")
@CompoundIndex(name = "chatRoom_member_unique", def = "{'chatRoomId': 1, 'memberId': 1}", unique = true)
data class ChatRoomMemberSequenceMongoEntity(
    val chatRoomId: String,
    val memberId: String,
    var readSequence: Int,
    var joinSequence: Int,
) {
    fun toChatRoomMemberSequence(): ChatRoomMemberSequence {
        return ChatRoomMemberSequence.of(
            chatRoomId = ChatRoomId.of(chatRoomId),
            readSequenceNumber = readSequence,
            joinSequenceNumber = joinSequence,
        )
    }

    companion object {
        fun generate(
            chatRoomId: ChatRoomId,
            memberId: UserId,
        ): ChatRoomMemberSequenceMongoEntity {
            return ChatRoomMemberSequenceMongoEntity(
                chatRoomId = chatRoomId.id,
                memberId = memberId.id,
                readSequence = 0,
                joinSequence = 0,
            )
        }
    }
}
