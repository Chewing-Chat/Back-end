package org.chewing.v1.repository.user

import org.chewing.v1.model.auth.Credential
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.User
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Repository

@Repository
interface UserRepository {
    fun read(userId: UserId): User?
    fun reads(userIds: List<UserId>): List<User>
    fun remove(userId: UserId): User?
    fun updateMedia(userId: UserId, media: Media): Media?
    fun append(credential: Credential, userName: String): User
    fun updatePassword(userId: UserId, password: String): UserId?
    fun updateStatusMessage(userId: UserId, statusMessage: String): UserId?
    fun readByCredential(credential: Credential, accessStatus: AccessStatus): User?
}
