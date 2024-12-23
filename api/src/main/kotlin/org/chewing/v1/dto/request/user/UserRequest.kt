package org.chewing.v1.dto.request.user

import org.chewing.v1.model.user.UserContent
import org.chewing.v1.model.user.UserName

class UserRequest {
    data class UpdateName(
        val firstName: String,
        val lastName: String,
    ) {
        fun toUserName(): UserName = UserName.of(firstName, lastName)
    }

    data class UpdateBirth(
        val birth: String,
    ) {
        fun toBirth(): String = birth
    }
    data class UpdateProfile(
        val firstName: String,
        val lastName: String,
        val birth: String,
    ) {
        fun toUserContent(): UserContent = UserContent.of(firstName, lastName, birth)
    }
}
