package org.chewing.v1.service.auth

import org.chewing.v1.implementation.auth.*
import org.chewing.v1.model.auth.Credential
import org.chewing.v1.model.auth.JwtToken
import org.chewing.v1.model.auth.LoginInfo
import org.chewing.v1.model.contact.Contact
import org.chewing.v1.model.contact.ContactType
import org.chewing.v1.model.user.User
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authReader: AuthReader,
    private val authAppender: AuthAppender,
    private val authSender: AuthSender,
    private val authValidator: AuthValidator,
    private val authUpdater: AuthUpdater,
    private val jwtTokenProvider: JwtTokenProvider,
    private val authRemover: AuthRemover,
) {
    fun createCredential(credential: Credential) {
        val verificationCode = authAppender.makeCredential(credential)
        authSender.sendVerificationCode(credential, verificationCode)
    }

    fun verify(credential: Credential, verificationCode: String): Contact {
        val existingCredential = authReader.readContact(credential)
        authValidator.validateVerifyCode(existingCredential, verificationCode)
        return existingCredential
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

    fun createCredentialNotUsed(userId: String, credential: Credential) {
        authValidator.validateContactIsUsed(credential, userId)
        val verificationCode = authAppender.makeCredential(credential)
        authSender.sendVerificationCode(credential, verificationCode)
    }

    fun getContactById(contactId: String, contactType: ContactType): Contact? = authReader.readContactById(contactId, contactType)
    fun getContact(credential: Credential): Contact = authReader.readContact(credential)
}
