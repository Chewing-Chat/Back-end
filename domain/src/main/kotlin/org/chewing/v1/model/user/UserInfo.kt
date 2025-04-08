package org.chewing.v1.model.user

import org.chewing.v1.model.contact.PhoneNumber
import org.chewing.v1.model.media.Media
import java.time.LocalDate

class UserInfo private constructor(
    val userId: UserId,
    val name: String,
    val image: Media,
    val status: AccessStatus,
    val phoneNumber: PhoneNumber,
    val password: String,
    val statusMessage: String,
    val birthday: LocalDate?,
) {
    companion object {
        fun of(
            userId: UserId,
            name: String,
            image: Media,
            status: AccessStatus,
            phoneNumber: PhoneNumber,
            password: String,
            statusMessage: String,
            birthday: LocalDate?,
        ): UserInfo {
            return UserInfo(
                userId = userId,
                image = image,
                name = name,
                status = status,
                phoneNumber = phoneNumber,
                password = password,
                statusMessage = statusMessage,
                birthday = birthday,
            )
        }
    }
}
