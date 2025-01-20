package org.chewing.v1.model.user

import org.chewing.v1.model.auth.PhoneNumber
import org.chewing.v1.model.media.Media

class User private constructor(
    val userId: UserId,
    val name: String,
    val birth: String,
    val image: Media,
    val status: AccessStatus,
    val phoneNumber: PhoneNumber,
    val password: String,
    val statusMessage: String,
) {
    companion object {
        fun of(
            userId: UserId,
            name: String,
            birth: String,
            image: Media,
            status: AccessStatus,
            phoneNumber: PhoneNumber,
            password: String,
            statusMessage: String,
        ): User {
            return User(
                userId = userId,
                birth = birth,
                image = image,
                name = name,
                status = status,
                phoneNumber = phoneNumber,
                password = password,
                statusMessage = statusMessage,
            )
        }
    }
}
