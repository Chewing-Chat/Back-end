package org.chewing.v1.dto.request.chat

import org.chewing.v1.model.user.UserId

class ChatRoomRequest {
    data class Create(
        val friendId: String,
    ) {
        fun toFriendId() = UserId.of(friendId)
    }
    data class CreateGroup(
        val friendIds: List<String> = emptyList(),
    ) {
        fun toFriendIds() = friendIds.map { UserId.of(it) }
    }
    data class Delete(
        val chatRoomIds: List<String> = emptyList(),
    )
    data class Invite(
        val chatRoomId: String,
        val friendId: String,
    ) {
        fun toFriendId() = UserId.of(friendId)
    }
    data class Favorite(
        val chatRoomId: String,
        val favorite: Boolean,
    )
}
