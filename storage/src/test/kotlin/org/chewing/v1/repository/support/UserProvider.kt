package org.chewing.v1.repository.support

import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.media.MediaType
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserInfo
import org.chewing.v1.model.user.UserId
import java.time.LocalDate

object UserProvider {
    fun buildUserName(): String {
        return "testName"
    }

    fun buildNormal(userId: UserId): UserInfo {
        return UserInfo.of(
            userId,
            "testName",
            Media.of(FileCategory.PROFILE, "www.example.com", 0, MediaType.IMAGE_BASIC),
            AccessStatus.NOT_ACCESS,
            PhoneNumberProvider.buildPhoneNumber(),
            "testPassword",
            "testStatusMessage",
            LocalDate.now(),
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
