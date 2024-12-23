package org.chewing.v1.service.user

import org.chewing.v1.implementation.media.FileHandler
import org.chewing.v1.implementation.user.user.*
import org.chewing.v1.model.auth.PushToken
import org.chewing.v1.model.contact.Contact
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
    fun getUserAccount(userId: String): UserAccount {
        return userReader.readAccount(userId)
    }

    fun getUsers(userIds: List<String>): List<User> {
        return userReader.reads(userIds)
    }

    fun getUserByContact(contact: Contact): User {
        return userReader.readByContact(contact)
    }

    fun createUser(
        contact: Contact,
        appToken: String,
        device: PushToken.Device,
    ): User {
        val user = userAppender.appendIfNotExist(contact)
        userRemover.removePushToken(device)
        userAppender.appendUserPushToken(user, appToken, device)
        return user
    }

    fun makeAccess(userId: String, userContent: UserContent) {
        userUpdater.updateAccess(userId, userContent)
    }

    fun updateFile(file: FileData, userId: String, category: FileCategory) {
        val media = fileHandler.handleNewFile(userId, file, category)
        val oldMedia = userUpdater.updateFileUrl(userId, media)
        fileHandler.handleOldFile(oldMedia)
    }

    // 사용자의 통합된 정보를 가져옴
    fun getAccessUser(userId: String): User {
        val user = userReader.read(userId)
        userValidator.isUserAccess(user)
        return user
    }

    fun updateName(userId: String, userName: UserName) {
        userUpdater.updateName(userId, userName)
    }

    fun updateBirth(userId: String, birth: String) {
        userUpdater.updateBirth(userId, birth)
    }

    fun updateUserContact(userId: String, contact: Contact) {
        userUpdater.updateContact(userId, contact)
    }

    fun deleteUser(userId: String) {
        val removedUser = userRemover.remove(userId)
        fileHandler.handleOldFiles(listOf(removedUser.image, removedUser.backgroundImage))
    }
}
