package org.chewing.v1.service.auth

import org.chewing.v1.implementation.auth.*
import org.chewing.v1.model.auth.Credential
import org.chewing.v1.model.token.RefreshToken
import org.chewing.v1.model.user.User
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
) {
    fun createCredential(credential: Credential) {
        val verificationCode = authGenerator.generateVerificationCode()
        authAppender.appendVerification(credential, verificationCode)
        authSender.sendVerificationCode(credential, verificationCode)
    }

    fun verify(credential: Credential, verificationCode: String) {
        val existingVerificationCode = authReader.readVerificationCode(credential)
        authValidator.validateVerifyCode(existingVerificationCode, verificationCode)
    }

    fun validatePassword(user: User, password: String) {
        authValidator.validatePassword(
            sourcePassword = password,
            targetPassword = user.password,
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
