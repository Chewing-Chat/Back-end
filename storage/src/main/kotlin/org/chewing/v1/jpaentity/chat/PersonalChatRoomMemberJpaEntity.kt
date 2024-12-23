package org.chewing.v1.jpaentity.chat

import jakarta.persistence.*
import org.chewing.v1.model.chat.member.ChatRoomMemberInfo
import org.chewing.v1.model.chat.room.ChatNumber

@Entity
@Table(
    name = "personal_chat_room_member",
    schema = "chewing",
    indexes = [
        Index(name = "personal_chat_room_member_idx_user_id", columnList = "user_id"),
        Index(name = "personal_chat_room_member_idx_chat_room_id", columnList = "chat_room_id"),
    ],
)
internal class PersonalChatRoomMemberJpaEntity(
    @EmbeddedId
    private val id: ChatRoomMemberId,
    private val friendId: String,
    private var favorite: Boolean,
    private var readSeqNumber: Int,
    @Version
    private var version: Long? = 0,
    private var startSeqNumber: Int,
) {
    companion object {
        fun generate(
            userId: String,
            friendId: String,
            chatRoomId: String,
            number: ChatNumber,
        ): PersonalChatRoomMemberJpaEntity = PersonalChatRoomMemberJpaEntity(
            id = ChatRoomMemberId(chatRoomId, userId),
            friendId = friendId,
            favorite = false,
            startSeqNumber = number.sequenceNumber,
            readSeqNumber = number.sequenceNumber,
        )
    }

    // ChatFriendEntity -> ChatFriend 변환 메서드
    fun toRoomOwned(): ChatRoomMemberInfo = ChatRoomMemberInfo.of(
        memberId = this.id.userId,
        chatRoomId = this.id.chatRoomId,
        readSeqNumber = this.readSeqNumber,
        favorite = this.favorite,
        startSeqNumber = this.startSeqNumber,
    )

    fun toRoomFriend(): ChatRoomMemberInfo = ChatRoomMemberInfo.of(
        memberId = this.friendId,
        chatRoomId = this.id.chatRoomId,
        readSeqNumber = this.readSeqNumber,
        favorite = this.favorite,
        startSeqNumber = this.startSeqNumber,
    )

    fun updateFavorite(favorite: Boolean) {
        this.favorite = favorite
    }
    fun updateRead(number: ChatNumber) {
        this.readSeqNumber = number.sequenceNumber
    }

    fun chatRoomId(): String = id.chatRoomId
}
