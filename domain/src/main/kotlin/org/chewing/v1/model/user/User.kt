package org.chewing.v1.model.user

import org.chewing.v1.model.contact.LocalPhoneNumber

class User private constructor(
    val info: UserInfo,
    val localPhoneNumber: LocalPhoneNumber,
) {
    companion object {
        fun of(
            userInfo: UserInfo,
            localPhoneNumber: LocalPhoneNumber,
        ): User {
            return User(
                info = userInfo,
                localPhoneNumber = localPhoneNumber,
            )
        }
    }
}
