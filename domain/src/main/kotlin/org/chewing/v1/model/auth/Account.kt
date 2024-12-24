package org.chewing.v1.model.auth

import org.chewing.v1.model.contact.Contact
import org.chewing.v1.model.contact.Phone
import org.chewing.v1.model.user.User
import org.chewing.v1.model.user.UserAccount

class Account private constructor(
    val user: User,
    val phoneNumber: String,
    val countryCode: String,
) {
    companion object {
        private const val DEFAULT_PHONE = "none"
        private const val DEFAULT_COUNTRY_CODE = "none"

        fun of(
            userAccount: UserAccount,
            phone: Contact?,
        ): Account {
            val phoneNumber = (phone as? Phone)?.number ?: DEFAULT_PHONE
            val countryCode = (phone as? Phone)?.countryCode ?: DEFAULT_COUNTRY_CODE

            return Account(
                userAccount.user,
                phoneNumber,
                countryCode,
            )
        }
    }
}
