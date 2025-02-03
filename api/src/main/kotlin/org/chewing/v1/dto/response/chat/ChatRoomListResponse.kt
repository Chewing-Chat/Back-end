package org.chewing.v1.dto.response.chat

data class ChatRoomListResponse(
    val chatRooms: List<ChatRoomResponse>,
) {
//    companion object {
//        fun ofList(chatRooms: List<ChatRoom>): ChatRoomListResponse {
//            return ChatRoomListResponse(
//                chatRooms = chatRooms.map { ChatRoomResponse.of(it) },
//            )
//        }
//    }
}
