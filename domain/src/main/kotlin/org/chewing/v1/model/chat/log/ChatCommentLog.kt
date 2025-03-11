package org.chewing.v1.model.chat.log

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.feed.FeedType
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class ChatCommentLog private constructor(
    override val messageId: String,
    override val chatRoomId: ChatRoomId,
    override val senderId: UserId,
    override val timestamp: LocalDateTime,
    override val roomSequence: ChatRoomSequence,
    override val type: ChatLogType,
    val medias: List<Media>,
    val comment: String,
    val feedId: FeedId,
    val feedType: FeedType,
    val content: String,
) : ChatLog() {
    companion object {
        fun of(
            messageId: String,
            chatRoomId: ChatRoomId,
            senderId: UserId,
            medias: List<Media>,
            timestamp: LocalDateTime,
            roomSequence: ChatRoomSequence,
            type: ChatLogType,
            comment: String,
            feedId: FeedId,
            feedType: FeedType,
            content: String,
        ): ChatCommentLog {
            return ChatCommentLog(
                messageId = messageId,
                chatRoomId = chatRoomId,
                senderId = senderId,
                medias = medias,
                timestamp = timestamp,
                roomSequence = roomSequence,
                type = type,
                comment = comment,
                feedId = feedId,
                feedType = feedType,
                content = content,
            )
        }
    }
}
