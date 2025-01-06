package org.chewing.v1.dto.response.search

import org.chewing.v1.dto.response.chat.ChatRoomResponse
import org.chewing.v1.dto.response.friend.FriendShipResponse
import org.chewing.v1.model.search.Search

data class SearchResultResponse(
    val friends: List<FriendShipResponse>,
    val chatRooms: List<ChatRoomResponse>,
) {
    companion object {
        fun ofList(search: Search): SearchResultResponse {
            return SearchResultResponse(
                friends = search.friends.map { FriendShipResponse.of(it) },
                chatRooms = search.chatRooms.map { ChatRoomResponse.of(it) },
            )
        }
    }
}
