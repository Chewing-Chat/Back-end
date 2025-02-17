package org.chewing.v1.model.chat.member

import org.chewing.v1.model.user.UserId

class ChatRoomMember private constructor(
    val memberId: UserId,
    val readSeqNumber: Int,
) {
    companion object {
        fun of(
            memberId: UserId,
            readSeqNumber: Int,
        ): ChatRoomMember {
            return ChatRoomMember(
                memberId = memberId,
                readSeqNumber = readSeqNumber,
            )
        }
    }
}
