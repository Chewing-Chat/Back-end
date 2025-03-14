package org.chewing.v1.dto.response.chat

import org.chewing.v1.model.chat.room.ThumbnailDirectChatRoom
import org.chewing.v1.model.chat.room.ThumbnailGroupChatRoom
import org.chewing.v1.model.user.UserId

data class ChatRoomListResponse(
    val directChatRooms: List<ThumbnailDirectChatRoomResponse>,
    val groupChatRooms: List<ThumbnailGroupChatRoomResponse>,
) {
    companion object {
        fun from(
            thumbnailDirectChatRooms: List<ThumbnailDirectChatRoom>,
            thumbnailGroupChatRooms: List<ThumbnailGroupChatRoom>,
            userId: UserId,
        ): ChatRoomListResponse {
            val directChatResponses = thumbnailDirectChatRooms.map { ThumbnailDirectChatRoomResponse.of(it) }

            val groupChatResponses = thumbnailGroupChatRooms.map { ThumbnailGroupChatRoomResponse.of(it, userId) }

            return ChatRoomListResponse(directChatResponses, groupChatResponses)
        }
    }
}
