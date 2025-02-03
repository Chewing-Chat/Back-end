package org.chewing.v1.jparepository.chat

import org.chewing.v1.jpaentity.chat.ChatRoomMemberId
import org.chewing.v1.jpaentity.chat.GroupChatRoomMemberJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface GroupChatRoomMemberJpaRepository : JpaRepository<GroupChatRoomMemberJpaEntity, ChatRoomMemberId> {
    fun findAllByIdUserId(userId: String): List<GroupChatRoomMemberJpaEntity>
    fun findByIdChatRoomIdIn(chatRoomIds: List<String>): List<GroupChatRoomMemberJpaEntity>
    fun findAllByIdChatRoomIdAndIdUserIdNot(chatRoomId: String, userId: String): List<GroupChatRoomMemberJpaEntity>
}
