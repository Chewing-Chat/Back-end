package org.chewing.v1.service.auth

import org.chewing.v1.implementation.auth.*
import org.chewing.v1.model.auth.Credential
import org.chewing.v1.model.auth.JwtToken
import org.chewing.v1.model.auth.LoginInfo
import org.chewing.v1.model.user.User
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authReader: AuthReader,
    private val authAppender: AuthAppender,
    private val authSender: AuthSender,
    private val authValidator: AuthValidator,
    private val authUpdater: AuthUpdater,
    private val authGenerator: AuthGenerator,
    private val jwtTokenProvider: JwtTokenProvider,
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

    fun createLoginInfo(user: User): LoginInfo {
        val token = jwtTokenProvider.createJwtToken(user.userId)
        authAppender.appendLoggedIn(token.refreshToken, user.userId)
        return LoginInfo.of(token, user)
    }

    fun logout(refreshToken: String) {
        jwtTokenProvider.validateToken(refreshToken)
        authRemover.removeLoginInfo(refreshToken)
    }

    fun refreshJwtToken(refreshToken: String): JwtToken {
        val (token, userId) = jwtTokenProvider.refresh(refreshToken)
        val ownedRefreshToken = authReader.readRefreshToken(refreshToken, userId)
        authUpdater.updateRefreshToken(token.refreshToken, ownedRefreshToken)
        return token
    }

    fun encryptPassword(password: String): String = authGenerator.hashPassword(password)
}
