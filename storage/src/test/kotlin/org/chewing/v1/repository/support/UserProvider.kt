package org.chewing.v1.repository.support

import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.media.MediaType
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.User
import org.chewing.v1.model.user.UserContent
import org.chewing.v1.model.user.UserName

object UserProvider {
    fun buildNormal(userId: String): User {
        return User.of(
            userId,
            "testFirstName",
            "testLastName",
            "2000-00-00",
            Media.of(FileCategory.PROFILE, "www.example.com", 0, MediaType.IMAGE_BASIC),
            Media.of(FileCategory.BACKGROUND, "www.example.com", 0, MediaType.IMAGE_BASIC),
            AccessStatus.NOT_ACCESS,
        )
    }
    fun buildFriend(userId: String): User {
        return User.of(
            userId,
            "friendFirstName",
            "friendLastName",
            "2000-00-00",
            Media.of(FileCategory.PROFILE, "www.example.com", 0, MediaType.IMAGE_BASIC),
            Media.of(FileCategory.BACKGROUND, "www.example.com", 0, MediaType.IMAGE_BASIC),
            AccessStatus.NOT_ACCESS,
        )
    }
    fun buildFriendName(): UserName {
        return UserName.of("friendFirstName", "friendLastName")
    }

    fun buildNewUserName(): UserName {
        return UserName.of("newFirstName", "newLastName")
    }
    fun buildNewUserContent(): UserContent {
        return UserContent.of("newFirstName", "newLastName", "2000-00-00")
    }

    fun buildNewBirth(): String {
        return "1000-00-00"
    }
}
