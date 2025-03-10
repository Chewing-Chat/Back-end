package org.chewing.v1.jparepository.chat

import org.chewing.v1.jpaentity.chat.ChatRoomMemberId
import org.chewing.v1.jpaentity.chat.GroupChatRoomMemberJpaEntity
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.springframework.data.jpa.repository.JpaRepository

internal interface GroupChatRoomMemberJpaRepository : JpaRepository<GroupChatRoomMemberJpaEntity, ChatRoomMemberId> {
    fun findAllByIdUserIdAndUserStatusNot(userId: String, status: ChatRoomMemberStatus): List<GroupChatRoomMemberJpaEntity>
    fun findByIdChatRoomIdAndUserStatusNot(chatRoomId: String, status: ChatRoomMemberStatus): List<GroupChatRoomMemberJpaEntity>
    fun findAllByIdChatRoomIdIn(chatRoomIds: List<String>): List<GroupChatRoomMemberJpaEntity>
    fun existsByIdAndUserStatusNot(chatRoomMemberId: ChatRoomMemberId, status: ChatRoomMemberStatus): Boolean
}
