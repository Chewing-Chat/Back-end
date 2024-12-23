package org.chewing.v1.dto.response.user

import org.chewing.v1.model.auth.Account

data class AccountResponse(
    val firstName: String,
    val lastName: String,
    val birth: String,
    val phoneNumber: String,
    val countryCode: String,
    val emailAddress: String,
) {
    companion object {
        fun of(
            account: Account,
        ): AccountResponse {
            return AccountResponse(
                firstName = account.user.name.firstName(),
                lastName = account.user.name.lastName(),
                birth = account.user.birth,
                phoneNumber = account.phoneNumber,
                countryCode = account.countryCode,
                emailAddress = account.emailAddress,
            )
        }
    }
}
