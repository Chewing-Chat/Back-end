package org.chewing.v1.jpaentity.chat

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.DirectChatRoomInfo
import org.chewing.v1.model.user.UserId
import org.hibernate.annotations.DynamicInsert
import java.util.UUID

@DynamicInsert
@Entity
@Table(name = "direct_chat_room", schema = "chewing")
internal class DirectChatRoomJpaEntity(
    @Id
    private val chatRoomId: String = UUID.randomUUID().toString(),

    val userAId: String,

    val userBId: String,

    @Enumerated(EnumType.STRING)
    var userAStatus: ChatRoomMemberStatus,

    @Enumerated(EnumType.STRING)
    var userBStatus: ChatRoomMemberStatus,

) : BaseEntity() {
    companion object {
        fun generate(userId1: UserId, userId2: UserId): DirectChatRoomJpaEntity {
            val (aId, bId) = if (userId1.id < userId2.id) userId1 to userId2 else userId2 to userId1
            return DirectChatRoomJpaEntity(
                userAId = aId.id,
                userBId = bId.id,
                userAStatus = ChatRoomMemberStatus.NORMAL,
                userBStatus = ChatRoomMemberStatus.NORMAL,
            )
        }
    }

    fun toChatRoom(userId: UserId): DirectChatRoomInfo {
        return DirectChatRoomInfo.of(
            chatRoomId = ChatRoomId.of(chatRoomId),
            userId = userId,
            friendId = UserId.of(if (userId.id == userAId) userBId else userAId),
            status = if (userId.id == userAId) userAStatus else userBStatus,
            friendStatus = if (userId.id == userAId) userBStatus else userAStatus,
        )
    }

    fun updateStatus(userId: UserId, status: ChatRoomMemberStatus) {
        if (userId.id == userAId) {
            userAStatus = status
        } else {
            userBStatus = status
        }
    }
}
