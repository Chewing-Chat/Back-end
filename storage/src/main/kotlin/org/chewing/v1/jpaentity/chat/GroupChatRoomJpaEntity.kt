package org.chewing.v1.jpaentity.chat

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.GroupChatRoomInfo
import org.hibernate.annotations.DynamicInsert
import java.util.UUID

@DynamicInsert
@Entity
@Table(name = "group_chat_room", schema = "chewing")
internal class GroupChatRoomJpaEntity(
    @Id
    private val chatRoomId: String = UUID.randomUUID().toString(),

    var name: String,
) : BaseEntity() {
    companion object {
        fun generate(name: String): GroupChatRoomJpaEntity {
            return GroupChatRoomJpaEntity(
                name = name,
            )
        }
    }

    fun updateGroupName(groupName: String) {
        name = groupName
    }

    fun toChatRoom(): GroupChatRoomInfo {
        return GroupChatRoomInfo.of(
            chatRoomId = ChatRoomId.of(chatRoomId),
            name = name,
        )
    }

    fun toChatRoomId(): ChatRoomId {
        return ChatRoomId.of(chatRoomId)
    }
}
