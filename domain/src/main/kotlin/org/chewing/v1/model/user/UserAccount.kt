package org.chewing.v1.model.user

class UserAccount private constructor(
    val user: User,
    val emailId: String?,
    val phoneId: String?,
) {
    companion object {
        fun of(
            user: User,
            emailId: String?,
            phoneNumberId: String?,
        ): UserAccount {
            return UserAccount(
                user = user,
                emailId = emailId,
                phoneId = phoneNumberId,
            )
        }
    }
}
