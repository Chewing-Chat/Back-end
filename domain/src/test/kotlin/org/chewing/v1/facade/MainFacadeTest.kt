package org.chewing.v1.facade

import io.mockk.every
import io.mockk.mockk
import org.chewing.v1.TestDataFactory
import org.chewing.v1.implementation.main.MainAggregator
import org.chewing.v1.model.friend.FriendSortCriteria
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.service.friend.FriendShipService
import org.chewing.v1.service.user.UserService
import org.chewing.v1.service.user.UserStatusService
import org.junit.jupiter.api.Test

class MainFacadeTest {
    private val userService: UserService = mockk()
    private val userStatusService: UserStatusService = mockk()
    private val friendShipService: FriendShipService = mockk()
    private val mainAggregator: MainAggregator = MainAggregator()
    private val mainFacade = MainFacade(userService, userStatusService, friendShipService, mainAggregator)

    @Test
    fun `메인 페이지 조회`() {
        // given
        val userId = "123"
        val friendId1 = "456"
        val friendId2 = "789"
        val user = TestDataFactory.createUser(userId)
        val userStatus = TestDataFactory.createUserStatus(userId)
        val friendShips = listOf(
            TestDataFactory.createFriendShip(friendId1, AccessStatus.ACCESS),
            TestDataFactory.createFriendShip(friendId2, AccessStatus.ACCESS),
        )

        val friendIds = friendShips.map { it.friendId }
        val users = listOf(
            TestDataFactory.createUser(friendId2),
            TestDataFactory.createUser(friendId1),
        )
        val usersStatuses = listOf(
            TestDataFactory.createUserStatus(friendId2),
            TestDataFactory.createUserStatus(friendId1),
        )

        every { userService.getAccessUser(userId) } returns user
        every { userStatusService.getSelectedStatus(userId) } returns userStatus
        every { friendShipService.getAccessFriendShips(userId, FriendSortCriteria.NAME) } returns friendShips
        every { userService.getUsers(friendIds) } returns users
        every { userStatusService.getSelectedStatuses(friendIds) } returns usersStatuses

        val result = mainFacade.getMainPage(userId, FriendSortCriteria.NAME)

        assert(result.first == user)
        assert(result.second == userStatus)
        assert(result.third.size == 2)
        assert(result.third[0].isFavorite == friendShips[0].isFavorite)
        assert(result.third[1].isFavorite == friendShips[1].isFavorite)
        assert(result.third[0].name == friendShips[0].friendName)
        assert(result.third[1].name == friendShips[1].friendName)
        assert(result.third[0].status == usersStatuses[1])
        assert(result.third[1].status == usersStatuses[0])
        assert(result.third[0].type == friendShips[1].type)
        assert(result.third[1].type == friendShips[0].type)
        assert(result.third[0].user == users[1])
        assert(result.third[1].user == users[0])
    }
}
