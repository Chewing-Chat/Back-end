package org.chewing.v1.service.user

import org.chewing.v1.implementation.contact.ContactFormatter
import org.chewing.v1.implementation.media.FileHandler
import org.chewing.v1.implementation.user.UserAppender
import org.chewing.v1.implementation.user.UserReader
import org.chewing.v1.implementation.user.UserRemover
import org.chewing.v1.implementation.user.UserUpdater
import org.chewing.v1.implementation.user.UserValidator
import org.chewing.v1.model.auth.CredentialTarget
import org.chewing.v1.model.auth.PushToken
import org.chewing.v1.model.contact.LocalPhoneNumber
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
    private val contactFormatter: ContactFormatter,
) {

    fun getUsers(userIds: List<UserId>): List<User> {
        val userInfos = userReader.reads(userIds)
        return userInfos.map {
            val phoneNumber = contactFormatter.extractCountryCodeAndLocalNumber(it.phoneNumber)
            User.of(it, phoneNumber)
        }
    }

    fun getUser(userId: UserId, status: AccessStatus): User {
        val userInfo = userReader.read(userId, status)
        val localPhoneNumber = contactFormatter.extractCountryCodeAndLocalNumber(userInfo.phoneNumber)
        return User.of(userInfo, localPhoneNumber)
    }

    fun getUserByContact(
        localPhoneNumber: LocalPhoneNumber,
        accessStatus: AccessStatus,
    ): User {
        val phoneNumber = contactFormatter.formatContact(localPhoneNumber)
        val userInfo = userReader.readByContact(phoneNumber, accessStatus)
        return User.of(userInfo, localPhoneNumber)
    }

    fun getUsersByContacts(
        localPhoneNumbers: List<LocalPhoneNumber>,
        accessStatus: AccessStatus,
    ): List<User> {
        val phoneNumbers = localPhoneNumbers.map { contactFormatter.formatContact(it) }
        val userInfos = userReader.readsByContacts(phoneNumbers, accessStatus)
        return userInfos.map {
            User.of(it, contactFormatter.extractCountryCodeAndLocalNumber(it.phoneNumber))
        }
    }

    fun createUser(
        localPhoneNumber: LocalPhoneNumber,
        appToken: String,
        device: PushToken.Device,
        userName: String,
    ): UserInfo {
        val phoneNumber = contactFormatter.formatContact(localPhoneNumber)
        userValidator.isNotAlreadyCreated(phoneNumber)
        val user = userAppender.append(phoneNumber, userName)
        userRemover.removePushToken(device)
        userAppender.appendUserPushToken(user, appToken, device)
        return user
    }

    fun createDeviceInfo(
        userInfo: UserInfo,
        device: PushToken.Device,
        appToken: String,
    ) {
        userAppender.appendUserPushToken(userInfo, appToken, device)
    }

    fun createPassword(userId: UserId, password: String) {
        userAppender.appendPassword(userId, password)
    }

    fun updatePassword(userId: UserId, password: String) {
        userUpdater.updatePassword(userId, password)
    }

    fun updateFile(file: FileData, userId: UserId, category: FileCategory) {
        val media = fileHandler.handleNewFile(userId, file, category)
        val oldMedia = userUpdater.updateFileUrl(userId, media)
        fileHandler.handleOldFile(oldMedia)
    }

    fun updateStatusMessage(userId: UserId, statusMessage: String) {
        userUpdater.updateStatusMessage(userId, statusMessage)
    }

    fun checkAvailability(
        localPhoneNumber: LocalPhoneNumber,
        type: CredentialTarget,
    ) {
        val phoneNumber = contactFormatter.formatContact(localPhoneNumber)
        when (type) {
            CredentialTarget.SIGN_UP -> userValidator.isNotAlreadyCreated(phoneNumber)
            CredentialTarget.RESET -> userValidator.isAlreadyCreated(phoneNumber)
        }
    }

    fun deleteUser(userId: UserId) {
        val removedUser = userRemover.remove(userId)
        fileHandler.handleOldFile(removedUser.image)
    }
}
