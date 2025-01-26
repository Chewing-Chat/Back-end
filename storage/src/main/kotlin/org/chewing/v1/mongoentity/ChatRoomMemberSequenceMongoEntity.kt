package org.chewing.v1.mongoentity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document


@Document(collection = "chatRoomMemberSequences")
@CompoundIndex(name = "chatRoom_member_unique", def = "{'chatRoomId': 1, 'memberId': 1}", unique = true)
data class ChatRoomMemberSequenceMongoEntity(
    val chatRoomId: String,
    val memberId: String,
    var readSeqNumber: Int,
    var startReadSeqNumber: Int
)
