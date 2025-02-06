package org.chewing.v1.dto.response.user

import org.chewing.v1.model.user.User

data class AccountResponse(
    val name: String,
    val phoneNumber: String,
    val countryCode: String,
) {
    companion object {
        fun of(
            user: User,
        ): AccountResponse {
            return AccountResponse(
                name = user.info.name,
                phoneNumber = user.localPhoneNumber.number,
                countryCode = user.localPhoneNumber.countryCode,
            )
        }
    }
}
