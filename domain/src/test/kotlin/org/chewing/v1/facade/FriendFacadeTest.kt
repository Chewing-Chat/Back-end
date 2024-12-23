package org.chewing.v1.facade

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.chewing.v1.TestDataFactory
import org.chewing.v1.service.auth.AuthService
import org.chewing.v1.service.friend.FriendShipService
import org.chewing.v1.service.user.UserService
import org.junit.jupiter.api.Test
class FriendFacadeTest {
    private val friendShipService: FriendShipService = mockk()
    private val userService: UserService = mockk()
    private val authService: AuthService = mockk()

    private val friendFacade = FriendFacade(friendShipService, userService, authService)

    @Test
    fun `친구 추가`() {
        // given
        val userId = "123"
        val friendName = TestDataFactory.createUserName()
        val verificationCode = "123"
        val targetContact = TestDataFactory.createPhoneNumber()
        val contact = TestDataFactory.createPhone(verificationCode)
        val targetUser = TestDataFactory.createUser(userId)
        val userAccount = TestDataFactory.createUserAccount(null, null)

        every { authService.getContact(targetContact) } returns contact
        every { userService.getUserByContact(contact) } returns targetUser
        every { userService.getUserAccount(userId) } returns userAccount
        every { friendShipService.createFriendShip(userId, userAccount.user.name, targetUser.userId, friendName) } just Runs

        friendFacade.addFriend(userId, friendName, targetContact)

        verify { friendShipService.createFriendShip(userId, userAccount.user.name, targetUser.userId, friendName) }
    }
}
