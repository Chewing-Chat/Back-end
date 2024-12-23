package org.chewing.v1.facade

import org.chewing.v1.model.auth.Account
import org.chewing.v1.model.auth.Credential
import org.chewing.v1.model.auth.LoginInfo
import org.chewing.v1.model.auth.PushToken
import org.chewing.v1.model.contact.ContactType
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
    fun loginAndCreateUser(
        credential: Credential,
        verificationCode: String,
        appToken: String,
        device: PushToken.Device,
    ): LoginInfo {
        val contact = authService.verify(credential, verificationCode)
        val user = userService.createUser(contact, appToken, device)
        return authService.createLoginInfo(user)
    }

    fun changeCredential(
        userId: String,
        credential: Credential,
        verificationCode: String,
    ) {
        val contact = authService.verify(credential, verificationCode)
        userService.updateUserContact(userId, contact)
    }

    fun deleteAccount(userId: String) {
        userService.deleteUser(userId)
        userStatusService.deleteAllUserStatuses(userId)
        scheduleService.deleteUsers(userId)
    }

    fun getAccount(userId: String): Account {
        val userAccount = userService.getUserAccount(userId)

        val email = userAccount.emailId?.let {
            authService.getContactById(it, ContactType.EMAIL)
        }

        val phone = userAccount.phoneId?.let {
            authService.getContactById(it, ContactType.PHONE)
        }
        return Account.of(userAccount, email, phone)
    }
}
