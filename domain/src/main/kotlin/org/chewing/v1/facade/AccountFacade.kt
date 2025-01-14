package org.chewing.v1.facade

import org.chewing.v1.model.auth.Credential
import org.chewing.v1.model.auth.CredentialTarget
import org.chewing.v1.model.auth.LoginInfo
import org.chewing.v1.model.auth.PhoneNumber
import org.chewing.v1.model.auth.PushToken
import org.chewing.v1.service.auth.AuthService
import org.chewing.v1.service.user.ScheduleService
import org.chewing.v1.service.user.UserService
import org.chewing.v1.service.user.UserStatusService
import org.springframework.stereotype.Service

@Service
class AccountFacade(
    private val authService: AuthService,
    private val userService: UserService,
    private val userStatusService: UserStatusService,
    private val scheduleService: ScheduleService,
) {
    fun createUser(
        credential: Credential,
        verificationCode: String,
        appToken: String,
        device: PushToken.Device,
        userName: String,
    ): LoginInfo {
        authService.verify(credential, verificationCode)
        val user = userService.createUser(credential, appToken, device, userName)
        return authService.createLoginInfo(user)
    }

    fun registerCredential(phoneNumber: PhoneNumber, type: CredentialTarget) {
        userService.checkAvailability(phoneNumber, type)
        authService.createCredential(phoneNumber)
    }

    fun resetCredential(
        phoneNumber: PhoneNumber,
        verificationCode: String,
    ): LoginInfo {
        authService.verify(phoneNumber, verificationCode)
        val user = userService.getUserByCredential(phoneNumber)
        return authService.createLoginInfo(user)
    }

    fun changePassword(
        userId: String,
        password: String,
    ) {
        val password = authService.encryptPassword(password)
        userService.updatePassword(userId, password)
    }

    fun deleteAccount(userId: String) {
        userService.deleteUser(userId)
        userStatusService.deleteAllUserStatuses(userId)
        scheduleService.deleteUsers(userId)
    }

    fun login(
        phoneNumber: PhoneNumber,
        password: String,
        device: PushToken.Device,
        appToken: String,
    ): LoginInfo {
        val user = userService.getUserByCredential(phoneNumber)
        authService.validatePassword(user, password)
        userService.loginUser(user, device, appToken)
        return authService.createLoginInfo(user)
    }
}
