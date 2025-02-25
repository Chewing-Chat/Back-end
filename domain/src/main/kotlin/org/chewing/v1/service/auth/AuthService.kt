package org.chewing.v1.service.auth

import org.chewing.v1.implementation.auth.*
import org.chewing.v1.implementation.contact.ContactFormatter
import org.chewing.v1.model.contact.LocalPhoneNumber
import org.chewing.v1.model.token.RefreshToken
import org.chewing.v1.model.user.UserInfo
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authReader: AuthReader,
    private val authAppender: AuthAppender,
    private val authSender: AuthSender,
    private val authValidator: AuthValidator,
    private val authUpdater: AuthUpdater,
    private val authGenerator: AuthGenerator,
    private val authRemover: AuthRemover,
    private val contactFormatter: ContactFormatter,
) {
    fun createCredential(
        localPhoneNumber: LocalPhoneNumber,
    ) {
        val phoneNumber = contactFormatter.formatContact(localPhoneNumber)
        val verificationCode = authGenerator.generateVerificationCode()
        authAppender.appendVerification(phoneNumber, verificationCode)
        authSender.sendVerificationCode(localPhoneNumber, verificationCode)
    }

    fun verify(
        localPhoneNumber: LocalPhoneNumber,
        verificationCode: String,
    ) {
        val phoneNumber = contactFormatter.formatContact(localPhoneNumber)
        val existingVerificationCode = authReader.readVerificationCode(phoneNumber)
        authValidator.validateVerifyCode(existingVerificationCode, verificationCode)
    }

    fun validatePassword(userInfo: UserInfo, password: String) {
        authValidator.validatePassword(
            sourcePassword = password,
            targetPassword = userInfo.password,
        )
    }

    fun createLoginInfo(userId: UserId, refreshToken: RefreshToken) {
        authAppender.appendLoggedIn(refreshToken, userId)
    }

    fun logout(refreshToken: String) {
        authRemover.removeLoginInfo(refreshToken)
    }

    fun updateLoginInfo(oldRefreshToken: String, newRefreshToken: RefreshToken, userId: UserId) {
        val ownedRefreshToken = authReader.readLoginInfo(oldRefreshToken, userId)
        authUpdater.updateLoginInfo(newRefreshToken, ownedRefreshToken)
    }

    fun encryptPassword(password: String): String = authGenerator.hashPassword(password)
}
