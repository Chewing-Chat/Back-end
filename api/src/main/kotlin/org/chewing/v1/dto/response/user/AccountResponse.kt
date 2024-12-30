package org.chewing.v1.dto.response.user

import org.chewing.v1.model.user.User

data class AccountResponse(
    val name: String,
    val birth: String,
    val phoneNumber: String,
    val countryCode: String,
) {
    companion object {
        fun of(
            user: User,
        ): AccountResponse {
            return AccountResponse(
                name = user.name,
                birth = user.birth,
                phoneNumber = user.phoneNumber.number,
                countryCode = user.phoneNumber.countryCode,
            )
        }
    }
}
