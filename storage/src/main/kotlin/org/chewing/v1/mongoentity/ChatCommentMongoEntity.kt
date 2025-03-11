package org.chewing.v1.mongoentity

import org.chewing.v1.model.chat.log.ChatCommentLog
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.ChatLogType
import org.chewing.v1.model.chat.message.ChatCommentMessage
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.feed.FeedType
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.user.UserId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chat_messages")
internal class ChatCommentMongoEntity(
    messageId: String,
    chatRoomId: String,
    senderId: String,
    sequence: Int,
    createAt: LocalDateTime,
    val medias: List<Media>,
    val comment: String,
    val feedId: String,
    val feedType: FeedType,
    val content: String,
) : ChatMessageMongoEntity(
    messageId = messageId,
    chatRoomId = chatRoomId,
    senderId = senderId,
    type = ChatLogType.COMMENT,
    sequence = sequence,
    createAt = createAt,
) {
    override fun toChatLog(): ChatLog {
        return ChatCommentLog.of(
            messageId = messageId,
            chatRoomId = ChatRoomId.of(chatRoomId),
            senderId = UserId.of(senderId),
            timestamp = this.createAt,
            medias = medias,
            comment = comment,
            feedId = FeedId.of(feedId),
            feedType = feedType,
            type = type,
            roomSequence = ChatRoomSequence.of(ChatRoomId.of(chatRoomId), sequence),
            content = content,
        )
    }

    companion object {
        fun from(
            chatCommentMessage: ChatCommentMessage,
        ): ChatCommentMongoEntity {
            return ChatCommentMongoEntity(
                chatCommentMessage.messageId,
                chatCommentMessage.chatRoomId.id,
                chatCommentMessage.senderId.id,
                chatCommentMessage.roomSequence.sequence,
                chatCommentMessage.timestamp,
                chatCommentMessage.medias,
                chatCommentMessage.comment,
                chatCommentMessage.feedId.id,
                chatCommentMessage.feedType,
                chatCommentMessage.content,
            )
        }
    }
}
