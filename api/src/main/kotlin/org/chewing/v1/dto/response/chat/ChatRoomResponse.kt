package org.chewing.v1.dto.response.chat

data class ChatRoomResponse(
    val chatRoomId: String,
    val favorite: Boolean,
    val groupChatRoom: Boolean,
    val latestMessage: String,
    val latestMessageTime: String,
    val totalUnReadMessage: Int,
    val latestPage: Int,
    val latestSeqNumber: Int,
    val members: List<ChatRoomMemberResponse>,
) {
//    companion object {
//        fun of(chatRoom: ChatRoom): ChatRoomResponse {
//            val formatLatestMessageTime =
//                chatRoom.latestMessageTime.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))
//            return ChatRoomResponse(
//                chatRoomId = chatRoom.chatRoomId,
//                favorite = chatRoom.favorite,
//                groupChatRoom = chatRoom.groupChatRoom,
//                latestMessage = chatRoom.latestMessage,
//                latestMessageTime = formatLatestMessageTime,
//                totalUnReadMessage = chatRoom.totalUnReadMessage,
//                latestPage = chatRoom.latestPage,
//                latestSeqNumber = chatRoom.latestSeqNumber,
//                members = chatRoom.chatRoomMemberInfos.map { ChatRoomMemberResponse.from(it) },
//            )
//        }
//    }
}
