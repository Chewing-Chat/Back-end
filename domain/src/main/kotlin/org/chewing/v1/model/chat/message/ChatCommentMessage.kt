package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.feed.FeedType
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class ChatCommentMessage private constructor(
    val messageId: String,
    override val chatRoomId: ChatRoomId,
    override val senderId: UserId,
    override val timestamp: LocalDateTime,
    val roomSequence: ChatRoomSequence,
    val comment: String,
    val feedId: FeedId,
    val medias: List<Media>,
    val feedType: FeedType,
    val content: String,
    override val chatRoomType: ChatRoomType,
) : ChatMessage() {
    override val type: MessageType = MessageType.COMMENT

    companion object {
        fun of(
            messageId: String,
            chatRoomId: ChatRoomId,
            senderId: UserId,
            comment: String,
            roomSequence: ChatRoomSequence,
            timestamp: LocalDateTime,
            chatRoomType: ChatRoomType,
            feedId: FeedId,
            medias: List<Media>,
            feedType: FeedType,
            content: String,
        ): ChatCommentMessage {
            return ChatCommentMessage(
                messageId = messageId,
                chatRoomId = chatRoomId,
                senderId = senderId,
                comment = comment,
                roomSequence = roomSequence,
                timestamp = timestamp,
                chatRoomType = chatRoomType,
                feedId = feedId,
                medias = medias,
                feedType = feedType,
                content = content,
            )
        }
    }
}
