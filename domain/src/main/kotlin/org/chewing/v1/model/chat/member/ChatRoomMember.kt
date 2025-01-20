package org.chewing.v1.model.chat.member

import org.chewing.v1.model.user.UserId

class ChatRoomMember private constructor(
    val memberId: UserId,
    val readSeqNumber: Int,
    val isOwned: Boolean,
) {
    companion object {
        fun of(
            memberId: UserId,
            readSeqNumber: Int,
            isOwned: Boolean,
        ): ChatRoomMember {
            return ChatRoomMember(
                memberId = memberId,
                readSeqNumber = readSeqNumber,
                isOwned = isOwned,
            )
        }
    }
}
