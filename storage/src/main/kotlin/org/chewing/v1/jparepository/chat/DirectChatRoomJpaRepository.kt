package org.chewing.v1.jparepository.chat

import org.chewing.v1.jpaentity.chat.DirectChatRoomJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

internal interface DirectChatRoomJpaRepository : JpaRepository<DirectChatRoomJpaEntity, String> {
    fun findByUserAIdAndUserBId(userAId: String, userBId: String): Optional<DirectChatRoomJpaEntity>

    @Query(
        """
    SELECT d FROM DirectChatRoomJpaEntity d
    WHERE (d.userAId = :userId AND d.userAStatus <> 'DELETED')
       OR (d.userBId = :userId AND d.userBStatus <> 'DELETED')
    """,
    )
    fun findByUserId(@Param("userId") userId: String): List<DirectChatRoomJpaEntity>
}
