package org.chewing.v1.model.user

import org.chewing.v1.model.auth.PhoneNumber
import org.chewing.v1.model.media.Media

class User private constructor(
    val userId: String,
    val name: String,
    val birth: String,
    val image: Media,
    val backgroundImage: Media,
    val status: AccessStatus,
    val phoneNumber: PhoneNumber,
    val password: String,
) {
    companion object {
        fun of(
            userId: String,
            name: String,
            birth: String,
            image: Media,
            backgroundImage: Media,
            status: AccessStatus,
            phoneNumber: PhoneNumber,
            password: String,
        ): User {
            return User(
                userId = userId,
                birth = birth,
                image = image,
                backgroundImage = backgroundImage,
                name = name,
                status = status,
                phoneNumber = phoneNumber,
                password = password,
            )
        }
    }

    fun updateName(
        name: String,
    ): User {
        return User(
            userId = userId,
            birth = birth,
            image = image,
            backgroundImage = backgroundImage,
            name = name,
            status = status,
            phoneNumber = phoneNumber,
            password = password,
        )
    }
}
