package org.chewing.v1.jparepository.user

import org.chewing.v1.jpaentity.user.UserJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

internal interface UserJpaRepository : JpaRepository<UserJpaEntity, String> {
    fun findUserJpaEntityByCountryCodeAndPhoneNumber(countryCode: String, phoneNumber: String): Optional<UserJpaEntity>
}
