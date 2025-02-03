package org.chewing.v1.jparepository.chat

import org.chewing.v1.jpaentity.chat.ChatRoomMemberId
import org.chewing.v1.jpaentity.chat.PersonalChatRoomMemberJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

internal interface PersonalChatRoomMemberJpaRepository : JpaRepository<PersonalChatRoomMemberJpaEntity, ChatRoomMemberId> {

    fun findAllByIdUserId(userId: String): List<PersonalChatRoomMemberJpaEntity>

    @Query(
        """
        SELECT crm.id.chatRoomId
        FROM PersonalChatRoomMemberJpaEntity crm
        WHERE (crm.id.userId = :userId AND crm.friendId = :friendId)
           OR (crm.id.userId = :friendId AND crm.friendId = :userId)
    """,
    )
    fun findPersonalChatRoomId(
        @Param("userId") userId: String,
        @Param("friendId") friendId: String,
    ): String?
}
