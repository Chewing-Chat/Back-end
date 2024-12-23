package org.chewing.v1.jparepository.auth

import org.chewing.v1.jpaentity.auth.EmailJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

internal interface EmailJpaRepository : JpaRepository<EmailJpaEntity, String> {
    fun findByAddress(emailAddress: String): Optional<EmailJpaEntity>

    fun findByEmailId(emailId: String): Optional<EmailJpaEntity>
}
