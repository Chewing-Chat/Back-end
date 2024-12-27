package org.chewing.v1.repository.support

import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.media.MediaType
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.User

object UserProvider {
    fun buildUserName(): String {
        return "testName"
    }

    fun buildNormal(userId: String): User {
        return User.of(
            userId,
            "testName",
            "2000-00-00",
            Media.of(FileCategory.PROFILE, "www.example.com", 0, MediaType.IMAGE_BASIC),
            Media.of(FileCategory.BACKGROUND, "www.example.com", 0, MediaType.IMAGE_BASIC),
            AccessStatus.NOT_ACCESS,
            PhoneNumberProvider.buildPhoneNumber(),
            "testPassword",
        )
    }

    fun buildFriend(userId: String): User {
        return User.of(
            userId,
            "friendName",
            "2000-00-00",
            Media.of(FileCategory.PROFILE, "www.example.com", 0, MediaType.IMAGE_BASIC),
            Media.of(FileCategory.BACKGROUND, "www.example.com", 0, MediaType.IMAGE_BASIC),
            AccessStatus.NOT_ACCESS,
            PhoneNumberProvider.buildPhoneNumber(),
            "testPassword",
        )
    }

    fun buildFriendName(): String {
        return "friendName"
    }

    fun buildNewUserName(): String {
        return "newName"
    }

    fun buildNewBirth(): String {
        return "1000-00-00"
    }
}
