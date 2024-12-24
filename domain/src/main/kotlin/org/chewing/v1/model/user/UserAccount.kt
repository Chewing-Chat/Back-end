package org.chewing.v1.model.user

class UserAccount private constructor(
    val user: User,
    val phoneId: String?,
) {
    companion object {
        fun of(
            user: User,
            phoneNumberId: String?,
        ): UserAccount {
            return UserAccount(
                user = user,
                phoneId = phoneNumberId,
            )
        }
    }
}
