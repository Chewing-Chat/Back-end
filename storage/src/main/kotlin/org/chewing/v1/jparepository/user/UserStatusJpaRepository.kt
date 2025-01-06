package org.chewing.v1.jparepository.user

import org.chewing.v1.jpaentity.user.UserStatusJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

internal interface UserStatusJpaRepository : JpaRepository<UserStatusJpaEntity, String> {
    fun findAllByUserId(userId: String): List<UserStatusJpaEntity>
    fun deleteAllByStatusIdIn(statusesId: List<String>)
    fun findBySelectedTrueAndUserId(userId: String): Optional<UserStatusJpaEntity>
    fun findAllBySelectedTrueAndUserIdIn(userIds: List<String>): List<UserStatusJpaEntity>
    fun deleteAllByUserId(userId: String)
}
