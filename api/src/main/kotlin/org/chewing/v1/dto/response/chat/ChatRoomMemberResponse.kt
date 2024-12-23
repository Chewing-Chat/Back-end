package org.chewing.v1.dto.response.chat

import org.chewing.v1.model.chat.member.ChatRoomMember

data class ChatRoomMemberResponse(
    val memberId: String,
    val readSeqNumber: Int,
    val owned: Boolean,
) {
    companion object {
        // ChatFriend를 ChatFriendResponse로 변환하는 함수
        fun from(chatRoomMember: ChatRoomMember): ChatRoomMemberResponse {
            return ChatRoomMemberResponse(
                memberId = chatRoomMember.memberId,
                readSeqNumber = chatRoomMember.readSeqNumber,
                owned = chatRoomMember.isOwned,
            )
        }
    }
}
