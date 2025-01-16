package org.chewing.v1.jparepository.user

import org.chewing.v1.jpaentity.user.UserJpaEntity
import org.chewing.v1.model.user.AccessStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

internal interface UserJpaRepository : JpaRepository<UserJpaEntity, String> {
    fun findUserJpaEntityByCountryCodeAndPhoneNumberAndType(
        countryCode: String,
        phoneNumber: String,
        type: AccessStatus,
    ): Optional<UserJpaEntity>
}
