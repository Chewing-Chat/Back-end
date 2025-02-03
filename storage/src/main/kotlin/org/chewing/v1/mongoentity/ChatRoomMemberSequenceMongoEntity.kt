package org.chewing.v1.mongoentity

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.DirectChatSequence
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document


@Document(collection = "chatRoomMemberSequences")
@CompoundIndex(name = "chatRoom_member_unique", def = "{'chatRoomId': 1, 'memberId': 1}", unique = true)
data class ChatRoomMemberSequenceMongoEntity(
    val chatRoomId: String,
    val memberId: String,
    var readSeqNumber: Int,
    var joinSeqNumber: Int
){
    fun toChatRoomMemberSequence(): DirectChatSequence {
        return DirectChatSequence.of(
            chatRoomId = ChatRoomId.of(chatRoomId),
            sequenceNumber = readSeqNumber,
        )
    }
}
