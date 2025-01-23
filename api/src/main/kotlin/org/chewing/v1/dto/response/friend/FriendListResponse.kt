package org.chewing.v1.dto.response.friend

import org.chewing.v1.model.friend.Friend

data class FriendListResponse(
    val friends: List<FriendResponse>,
) {
    companion object {
        fun of(
            friends: List<Friend>,
        ): FriendListResponse {
            return FriendListResponse(
                friends = friends.map { FriendResponse.of(it) },
            )
        }
    }
}
