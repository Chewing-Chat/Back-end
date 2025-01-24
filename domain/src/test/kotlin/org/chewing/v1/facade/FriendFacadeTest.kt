package org.chewing.v1.facade

import io.mockk.mockk
import org.chewing.v1.implementation.friend.friend.FriendAggregator
import org.chewing.v1.service.friend.FriendShipService
import org.chewing.v1.service.user.UserService

class FriendFacadeTest {
    private val friendShipService: FriendShipService = mockk()
    private val userService: UserService = mockk()
    private val friendAggregator: FriendAggregator = mockk()

    private val friendFacade = FriendFacade(friendShipService, userService, friendAggregator)

//    @Test
//    fun `친구 추가`() {
//        // given
//        val userId = TestDataFactory.createUserId()
//        val friendId = TestDataFactory.createFriendId()
//        val targetUser = TestDataFactory.createUser(friendId, AccessStatus.ACCESS)
//        val user = TestDataFactory.createUser(userId, AccessStatus.ACCESS)
//        val localPhoneNumber = TestDataFactory.createLocalPhoneNumber()
//
//        every { userService.getUserByContact(localPhoneNumber, AccessStatus.ACCESS) } returns targetUser
//        every { userService.getUser(userId, AccessStatus.ACCESS) } returns user
//        every { friendShipService.createFriendShips(any(), any(), any()) } just Runs
//    }
}
