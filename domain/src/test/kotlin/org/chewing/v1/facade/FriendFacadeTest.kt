package org.chewing.v1.facade

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.chewing.v1.TestDataFactory
import org.chewing.v1.service.auth.AuthService
import org.chewing.v1.service.friend.FriendShipService
import org.chewing.v1.service.user.UserService
import org.junit.jupiter.api.Test

class FriendFacadeTest {
    private val friendShipService: FriendShipService = mockk()
    private val userService: UserService = mockk()
    private val authService: AuthService = mockk()

    private val friendFacade = FriendFacade(friendShipService, userService)

    @Test
    fun `친구 추가`() {
        // given
        val userId = "userId"
        val friendName = "friendName"
        val friendId = "friendId"
        val targetCredential = TestDataFactory.createPhoneNumber()
        val targetUser = TestDataFactory.createAccessUser(friendId)
        val user = TestDataFactory.createAccessUser(userId)

        every { userService.getUserByCredential(targetCredential) } returns targetUser
        every { userService.getUser(userId) } returns user
        every { friendShipService.createFriendShip(userId, user.name, friendId, friendName) } just Runs
    }
}
