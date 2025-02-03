package org.chewing.v1.jparepository.user

import org.chewing.v1.jpaentity.user.UserJpaEntity
import org.chewing.v1.model.user.AccessStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

internal interface UserJpaRepository : JpaRepository<UserJpaEntity, String> {
    fun findUserJpaEntityByPhoneNumberAndStatus(
        phoneNumber: String,
        status: AccessStatus,
    ): Optional<UserJpaEntity>
    fun findByUserIdAndStatus(userId: String, status: AccessStatus): Optional<UserJpaEntity>
    fun findUserJpaEntitiesByPhoneNumberInAndStatus(
        phoneNumbers: List<String>,
        status: AccessStatus,
    ): List<UserJpaEntity>
}
