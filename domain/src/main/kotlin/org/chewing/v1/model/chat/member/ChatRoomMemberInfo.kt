package org.chewing.v1.model.chat.member

import org.chewing.v1.model.user.UserId

class ChatRoomMemberInfo private constructor(
    val memberId: UserId,
    val chatRoomId: String,
    val readSeqNumber: Int,
    val startSeqNumber: Int,
    val favorite: Boolean,
) {
    companion object {
        fun of(
            memberId: UserId,
            chatRoomId: String,
            readSeqNumber: Int,
            startSeqNumber: Int,
            favorite: Boolean,
        ): ChatRoomMemberInfo {
            return ChatRoomMemberInfo(memberId, chatRoomId, readSeqNumber, startSeqNumber, favorite)
        }
    }
}
