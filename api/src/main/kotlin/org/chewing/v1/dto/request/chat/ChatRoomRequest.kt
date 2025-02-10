package org.chewing.v1.dto.request.chat

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.user.UserId

class ChatRoomRequest {
    data class Create(
        val friendId: String,
        val message: String,
    ) {
        fun toFriendId() = UserId.of(friendId)
        fun toMessage() = message
    }
    data class CreateGroup(
        val friendIds: List<String> = emptyList(),
        val name: String,
    ) {
        fun toFriendIds() = friendIds.map { UserId.of(it) }
        fun toName() = name
    }
    data class Delete(
        val chatRoomId: String,
    ) {
        fun toChatRoomId() = ChatRoomId.of(chatRoomId)
    }
    data class Invite(
        val chatRoomId: String,
        val friendId: String,
    ) {
        fun toFriendId() = UserId.of(friendId)
        fun toChatRoomId() = ChatRoomId.of(chatRoomId)
    }
    data class Leave(
        val chatRoomId: String,
    ) {
        fun toChatRoomId() = ChatRoomId.of(chatRoomId)
    }
    data class Favorite(
        val chatRoomId: String,
        val favorite: Boolean,
    ) {
        fun toFavorite(): ChatRoomMemberStatus {
            if (favorite) {
                ChatRoomMemberStatus.FAVORITE
            }
            return ChatRoomMemberStatus.NORMAL
        }
        fun toChatRoomId() = ChatRoomId.of(chatRoomId)
    }
}
