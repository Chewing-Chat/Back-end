package org.chewing.v1.model.user

import org.chewing.v1.model.media.Media

class User private constructor(
    val userId: String,
    val name: UserName,
    val birth: String,
    val image: Media,
    val backgroundImage: Media,
    val status: AccessStatus,
) {
    companion object {
        fun of(
            userId: String,
            firstName: String,
            lastName: String,
            birth: String,
            image: Media,
            backgroundImage: Media,
            status: AccessStatus,
        ): User {
            return User(
                userId = userId,
                birth = birth,
                image = image,
                backgroundImage = backgroundImage,
                name = UserName.of(firstName, lastName),
                status = status,
            )
        }
    }

    fun updateName(
        name: UserName,
    ): User {
        return User(
            userId = userId,
            birth = birth,
            image = image,
            backgroundImage = backgroundImage,
            name = name,
            status = status,
        )
    }
}
