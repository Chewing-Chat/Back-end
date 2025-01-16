package org.chewing.v1.repository.user

import org.chewing.v1.model.auth.Credential
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.User
import org.springframework.stereotype.Repository

@Repository
interface UserRepository {
    fun read(userId: String): User?
    fun reads(userIds: List<String>): List<User>
    fun remove(userId: String): User?
    fun updateMedia(userId: String, media: Media): Media?
    fun append(credential: Credential, userName: String): User
    fun updatePassword(userId: String, password: String): String?
    fun updateStatusMessage(userId: String, statusMessage: String): String?
    fun readByCredential(credential: Credential,accessStatus: AccessStatus ): User?
}
