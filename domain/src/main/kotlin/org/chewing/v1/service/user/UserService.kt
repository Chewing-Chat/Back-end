package org.chewing.v1.service.user

import org.chewing.v1.implementation.media.FileHandler
import org.chewing.v1.implementation.user.user.*
import org.chewing.v1.model.auth.Credential
import org.chewing.v1.model.auth.CredentialTarget
import org.chewing.v1.model.auth.PushToken
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.FileData
import org.chewing.v1.model.user.*
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userReader: UserReader,
    private val fileHandler: FileHandler,
    private val userUpdater: UserUpdater,
    private val userValidator: UserValidator,
    private val userRemover: UserRemover,
    private val userAppender: UserAppender,
) {

    fun getUsers(userIds: List<String>): List<User> {
        return userReader.reads(userIds)
    }

    fun getUser(userId: String): User {
        return userReader.read(userId)
    }

    fun getUserByCredential(credential: Credential): User {
        return userReader.readByCredential(credential)
    }

    fun createUser(
        credential: Credential,
        appToken: String,
        device: PushToken.Device,
        userName: String,
    ): User {
        userValidator.isNotAlreadyCreated(credential)
        val user = userAppender.append(credential, userName)
        userRemover.removePushToken(device)
        userAppender.appendUserPushToken(user, appToken, device)
        return user
    }

    fun loginUser(
        user: User,
        device: PushToken.Device,
        appToken: String,
    ) {
        userAppender.appendUserPushToken(user, appToken, device)
    }

    fun updatePassword(userId: String, password: String) {
        userUpdater.updatePassword(userId, password)
    }

    fun updateFile(file: FileData, userId: String, category: FileCategory) {
        val media = fileHandler.handleNewFile(userId, file, category)
        val oldMedia = userUpdater.updateFileUrl(userId, media)
        fileHandler.handleOldFile(oldMedia)
    }

    fun checkAvailability(credential: Credential, type: CredentialTarget) {
        when (type) {
            CredentialTarget.SIGN_UP -> userValidator.isNotAlreadyCreated(credential)
            CredentialTarget.RESET -> userValidator.isAlreadyCreated(credential)
        }
    }

    fun getAccessUser(userId: String): User {
        val user = userReader.read(userId)
        userValidator.isAccess(user)
        return user
    }

    fun deleteUser(userId: String) {
        val removedUser = userRemover.remove(userId)
        fileHandler.handleOldFiles(listOf(removedUser.image, removedUser.backgroundImage))
    }
}
