package org.chewing.v1.dto.request.chat

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.user.UserId

class ChatRequest {
    data class Reply(
        val chatRoomId: String,
        val parentMessageId: String,
        val message: String,
    ) {
        fun toChatRoomId(): ChatRoomId {
            return ChatRoomId.of(chatRoomId)
        }
        fun toParentMessageId(): String {
            return parentMessageId
        }
    }
    data class Read(
        val chatRoomId: String,
        val sequenceNumber: Int,
    ) {
        fun toChatRoomId(): ChatRoomId {
            return ChatRoomId.of(chatRoomId)
        }
        fun toSequenceNumber(): Int {
            return sequenceNumber
        }
    }
    data class Common(
        val chatRoomId: String,
        val message: String,
    ) {
        fun toChatRoomId(): ChatRoomId {
            return ChatRoomId.of(chatRoomId)
        }
        fun toMessage(): String {
            return message
        }
    }
    data class Delete(
        val chatRoomId: String,
        val messageId: String,
    ) {
        fun toChatRoomId(): ChatRoomId {
            return ChatRoomId.of(chatRoomId)
        }
        fun toMessageId(): String {
            return messageId
        }
    }
    data class Comment(
        val comment: String,
        val feedId: String,
        val friendId: String,
    ) {
        fun toComment(): String {
            return comment
        }
        fun toFeedId(): FeedId {
            return FeedId.of(feedId)
        }
        fun toFriendId(): UserId {
            return UserId.of(friendId)
        }
    }
}
