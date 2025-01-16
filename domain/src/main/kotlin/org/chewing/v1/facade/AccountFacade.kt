package org.chewing.v1.facade

import org.chewing.v1.model.auth.Credential
import org.chewing.v1.model.auth.CredentialTarget
import org.chewing.v1.model.auth.JwtToken
import org.chewing.v1.model.auth.PhoneNumber
import org.chewing.v1.model.auth.PushToken
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.service.auth.AuthService
import org.chewing.v1.service.user.ScheduleService
import org.chewing.v1.service.user.UserService
import org.springframework.stereotype.Service

@Service
class AccountFacade(
    private val authService: AuthService,
    private val userService: UserService,
    private val scheduleService: ScheduleService,
) {
    fun createUser(
        credential: Credential,
        verificationCode: String,
        appToken: String,
        device: PushToken.Device,
        userName: String,
    ): JwtToken {
        authService.verify(credential, verificationCode)
        val user = userService.createUser(credential, appToken, device, userName)
        return authService.createToken(user)
    }

    fun registerCredential(phoneNumber: PhoneNumber, type: CredentialTarget) {
        userService.checkAvailability(phoneNumber, type)
        authService.createCredential(phoneNumber)
    }

    fun resetCredential(
        phoneNumber: PhoneNumber,
        verificationCode: String,
    ): JwtToken {
        authService.verify(phoneNumber, verificationCode)
        val user = userService.getUserByCredential(phoneNumber, AccessStatus.ACCESS)
        return authService.createToken(user)
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
        scheduleService.deleteUsers(userId)
    }

    fun login(
        phoneNumber: PhoneNumber,
        password: String,
        device: PushToken.Device,
        appToken: String,
    ): JwtToken {
        val user = userService.getUserByCredential(phoneNumber, AccessStatus.ACCESS)
        authService.validatePassword(user, password)
        userService.createDeviceInfo(user, device, appToken)
        return authService.createToken(user)
    }
}
