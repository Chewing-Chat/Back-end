package org.chewing.v1.facade

import org.chewing.v1.model.auth.CredentialTarget
import org.chewing.v1.model.notification.PushInfo
import org.chewing.v1.model.contact.LocalPhoneNumber
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserId
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
        localPhoneNumber: LocalPhoneNumber,
        verificationCode: String,
        appToken: String,
        device: PushInfo.Device,
        userName: String,
    ): UserId {
        authService.verify(localPhoneNumber, verificationCode)
        val user = userService.createUser(localPhoneNumber, appToken, device, userName)
        return user.userId
    }

    fun registerCredential(
        localPhoneNumber: LocalPhoneNumber,
        type: CredentialTarget,
    ) {
        userService.checkAvailability(localPhoneNumber, type)
        authService.createCredential(localPhoneNumber)
    }

    fun resetCredential(
        localPhoneNumber: LocalPhoneNumber,
        verificationCode: String,
    ): UserId {
        authService.verify(localPhoneNumber, verificationCode)
        val user = userService.getUserByContact(localPhoneNumber, AccessStatus.ACCESS)
        return user.info.userId
    }

    fun changePassword(
        userId: UserId,
        password: String,
    ) {
        val password = authService.encryptPassword(password)
        userService.updatePassword(userId, password)
    }

    fun createPassword(
        userId: UserId,
        password: String,
    ) {
        val password = authService.encryptPassword(password)
        userService.createPassword(userId, password)
    }

    fun deleteAccount(userId: UserId) {
        userService.deleteUser(userId)
        scheduleService.deleteAllParticipant(userId)
    }

    fun login(
        localPhoneNumber: LocalPhoneNumber,
        password: String,
        device: PushInfo.Device,
        appToken: String,
    ): UserId {
        val user = userService.getUserByContact(localPhoneNumber, AccessStatus.ACCESS)
        authService.validatePassword(user.info, password)
        userService.createDeviceInfo(user.info, device, appToken)
        return user.info.userId
    }
}
