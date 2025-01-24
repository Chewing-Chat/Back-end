package org.chewing.v1.mongoentity

import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.ChatLogType
import org.chewing.v1.model.chat.message.*
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chat_messages")
// 복합 인덱스 설정

@CompoundIndexes(
    CompoundIndex(
        name = "chatRoomId_page_seqNumber_idx",
        def = "{'chatRoomId': 1, 'page': 1, 'seqNumber': 1}",
    ),
    CompoundIndex(name = "chatRoomId_seqNumber_idx", def = "{'chatRoomId': 1, 'seqNumber': 1}"),
    CompoundIndex(
        name = "chatRoom_message_compound_idx",
        def = "{'chatRoomId': 1, 'type': 1, 'message': 1}",
        partialFilter = "{ 'type': { \$in: ['NORMAL', 'REPLY'] } }",
    ),
)
internal sealed class ChatMessageMongoEntity(
    @Id
    protected val messageId: String,
    protected val chatRoomId: String,
    protected var type: ChatLogType,
    protected val senderId: String,
    protected val seqNumber: Int,
    protected val page: Int,
    protected val sendTime: LocalDateTime,
) {

    companion object {
        fun fromChatMessage(chatMessage: ChatMessage): ChatMessageMongoEntity? = when (chatMessage) {
            is ChatNormalMessage -> ChatNormalMongoEntity.from(chatMessage)
            is ChatInviteMessage -> ChatInviteMongoEntity.from(chatMessage)
            is ChatLeaveMessage -> ChatLeaveMongoEntity.from(chatMessage)
            is ChatFileMessage -> ChatFileMongoEntity.from(chatMessage)
            is ChatDeleteMessage -> null
            is ChatReadMessage -> null
            is ChatReplyMessage -> ChatReplyMongoEntity.from(chatMessage)
            is ChatBombMessage -> ChatBombMongoEntity.from(chatMessage)
        }
    }

    abstract fun toChatLog(): ChatLog
}
