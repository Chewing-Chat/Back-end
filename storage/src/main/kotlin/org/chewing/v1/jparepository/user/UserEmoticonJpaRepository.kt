package org.chewing.v1.jparepository.user

import org.chewing.v1.jpaentity.user.UserEmoticonJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface UserEmoticonJpaRepository : JpaRepository<UserEmoticonJpaEntity, String> {
    fun findAllByIdUserId(userId: String): List<UserEmoticonJpaEntity>
}
