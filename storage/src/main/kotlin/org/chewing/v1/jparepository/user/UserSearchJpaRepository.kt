package org.chewing.v1.jparepository.user

import org.chewing.v1.jpaentity.friend.UserSearchJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface UserSearchJpaRepository : JpaRepository<UserSearchJpaEntity, String> {
    fun findAllByUserIdOrderByCreatedAt(userId: String): List<UserSearchJpaEntity>
}
