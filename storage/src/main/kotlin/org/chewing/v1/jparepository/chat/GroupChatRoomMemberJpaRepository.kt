package org.chewing.v1.jparepository.chat

import org.chewing.v1.jpaentity.chat.ChatRoomMemberId
import org.chewing.v1.jpaentity.chat.GroupChatRoomJpaEntity
import org.chewing.v1.jpaentity.chat.GroupChatRoomMemberJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface GroupChatRoomMemberJpaRepository :JpaRepository<GroupChatRoomMemberJpaEntity, ChatRoomMemberId> {
    fun findAllByIdUserId(userId: String): List<GroupChatRoomMemberJpaEntity>
    fun findByIdChatRoomId(chatRoomId: String): List<GroupChatRoomMemberJpaEntity>
    fun findAllByIdChatRoomIdIn(chatRoomIds: List<String>): List<GroupChatRoomMemberJpaEntity>
}
