package org.chewing.v1.model.chat.member

class ChatRoomMember private constructor(
    val memberId: String,
    val readSeqNumber: Int,
    val isOwned: Boolean,
) {
    companion object {
        fun of(
            memberId: String,
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
