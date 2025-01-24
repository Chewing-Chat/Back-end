package org.chewing.v1.mongoentity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class ChatSequenceMongoEntity(
    @Id
    val chatRoomId: String,
    var seqNumber: Int,
)
