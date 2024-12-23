package org.chewing.v1.model.auth

class EmailAddress private constructor(
    val address: String,
) : Credential() {
    companion object {
        fun of(
            email: String,
        ): EmailAddress {
            return EmailAddress(
                address = email,
            )
        }
    }
}
