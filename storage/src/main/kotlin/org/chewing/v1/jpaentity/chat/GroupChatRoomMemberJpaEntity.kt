package org.chewing.v1.jpaentity.chat

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.GroupChatRoomMemberInfo
import org.chewing.v1.model.user.UserId
import org.hibernate.annotations.DynamicInsert

@DynamicInsert
@Entity
@Table(name = "group_chat_room_member", schema = "chewing")
internal class GroupChatRoomMemberJpaEntity(
    @EmbeddedId
    private val id: ChatRoomMemberId,

    @Enumerated(EnumType.STRING)
    var userStatus: ChatRoomMemberStatus,
) : BaseEntity() {
    companion object {
        fun generate(chatRoomId: ChatRoomId, userId: UserId): GroupChatRoomMemberJpaEntity {
            return GroupChatRoomMemberJpaEntity(
                id = ChatRoomMemberId.of(chatRoomId, userId),
                userStatus = ChatRoomMemberStatus.NORMAL,
            )
        }
    }

    fun updateStatus(status: ChatRoomMemberStatus) {
        this.userStatus = status
    }

    fun toChatRoomMember(): GroupChatRoomMemberInfo {
        return GroupChatRoomMemberInfo.of(
            chatRoomId = ChatRoomId.of(id.chatRoomId),
            memberId = UserId.of(id.userId),
            status = userStatus,
        )
    }
}
