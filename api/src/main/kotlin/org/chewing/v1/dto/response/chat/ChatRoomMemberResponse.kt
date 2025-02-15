package org.chewing.v1.dto.response.chat

import org.chewing.v1.model.chat.member.ChatRoomMember

data class ChatRoomMemberResponse(
    val memberId: String,
    val readSeqNumber: Int,
) {
    companion object {
        // ChatFriend를 ChatFriendResponse로 변환하는 함수
        fun from(chatRoomMember: ChatRoomMember): ChatRoomMemberResponse {
            return ChatRoomMemberResponse(
                memberId = chatRoomMember.memberId.id,
                readSeqNumber = chatRoomMember.readSeqNumber,
            )
        }
    }
}
