package org.chewing.v1.jparepository.user

import org.chewing.v1.jpaentity.user.UserJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

internal interface UserJpaRepository : JpaRepository<UserJpaEntity, String> {
    fun findByEmailId(emailId: String): Optional<UserJpaEntity>
    fun findByPhoneNumberId(phoneNumber: String): Optional<UserJpaEntity>
    fun existsByEmailIdAndUserIdNot(emailId: String, userId: String): Boolean
    fun existsByPhoneNumberIdAndUserIdNot(phoneNumber: String, userId: String): Boolean
}
