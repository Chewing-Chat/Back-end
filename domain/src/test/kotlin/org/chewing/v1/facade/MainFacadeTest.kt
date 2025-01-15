package org.chewing.v1.facade

import io.mockk.every
import io.mockk.mockk
import org.chewing.v1.TestDataFactory
import org.chewing.v1.implementation.main.MainAggregator
import org.chewing.v1.model.friend.FriendSortCriteria
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.service.friend.FriendShipService
import org.chewing.v1.service.user.UserService
import org.junit.jupiter.api.Test

class MainFacadeTest {
    private val userService: UserService = mockk()
    private val friendShipService: FriendShipService = mockk()
    private val mainAggregator: MainAggregator = MainAggregator()
    private val mainFacade = MainFacade(userService, friendShipService, mainAggregator)

    @Test
    fun `메인 페이지 조회`() {
        // given
        val userId = "123"
        val friendId1 = "456"
        val friendId2 = "789"
        val user = TestDataFactory.createAccessUser(userId)
        val friendShips = listOf(
            TestDataFactory.createFriendShip(friendId1, AccessStatus.ACCESS),
            TestDataFactory.createFriendShip(friendId2, AccessStatus.ACCESS),
        )

        val friendIds = friendShips.map { it.friendId }
        val users = listOf(
            TestDataFactory.createAccessUser(friendId2),
            TestDataFactory.createAccessUser(friendId1),
        )

        every { userService.getAccessUser(userId) } returns user
        every { friendShipService.getAccessFriendShips(userId, FriendSortCriteria.NAME) } returns friendShips
        every { userService.getUsers(friendIds) } returns users

        val result = mainFacade.getMainPage(userId, FriendSortCriteria.NAME)

        assert(result.first == user)
        assert(result.second.size == 2)
        assert(result.second[0].isFavorite == friendShips[0].isFavorite)
        assert(result.second[1].isFavorite == friendShips[1].isFavorite)
        assert(result.second[0].name == friendShips[0].friendName)
        assert(result.second[1].name == friendShips[1].friendName)
        assert(result.second[0].type == friendShips[1].type)
        assert(result.second[1].type == friendShips[0].type)
        assert(result.second[0].user == users[1])
        assert(result.second[1].user == users[0])
    }
}
