package org.chewing.v1.facade

//import io.mockk.every
//import io.mockk.mockk
//import org.chewing.v1.TestDataFactory
//import org.chewing.v1.implementation.main.MainAggregator
//import org.chewing.v1.model.friend.FriendShipStatus
//import org.chewing.v1.model.friend.FriendSortCriteria
//import org.chewing.v1.model.user.AccessStatus
//import org.chewing.v1.service.friend.FriendShipService
//import org.chewing.v1.service.user.UserService
//import org.junit.jupiter.api.Test

class MainFacadeTest {
//    private val userService: UserService = mockk()
//    private val friendShipService: FriendShipService = mockk()
//    private val mainAggregator: MainAggregator = MainAggregator()
//    private val mainFacade = MainFacade(userService, friendShipService, mainAggregator)

//    @Test
//    fun `메인 페이지 조회`() {
//        // given
//        val userId = TestDataFactory.createUserId()
//        val friendId1 = TestDataFactory.createFriendId()
//        val friendId2 = TestDataFactory.createSecondFriendId()
//        val user = TestDataFactory.createUser(userId, AccessStatus.ACCESS)
//        val friendShips = listOf(
//            TestDataFactory.createFriendShip(userId, friendId1, FriendShipStatus.FRIEND),
//            TestDataFactory.createFriendShip(userId, friendId2, FriendShipStatus.FRIEND),
//        )
//
//        val friendIds = friendShips.map { it.friendId }
//        val friendInfos = listOf(
//            TestDataFactory.createUser(friendId2, AccessStatus.ACCESS),
//            TestDataFactory.createUser(friendId1, AccessStatus.ACCESS),
//        )
//
//        every { userService.getUser(userId, AccessStatus.ACCESS) } returns user
//        every { friendShipService.getFriendShips(userId, FriendSortCriteria.NAME) } returns friendShips
//        every { userService.getUsers(friendIds) } returns friendInfos
//
//        val result = mainFacade.getMainPage(userId, FriendSortCriteria.NAME)
//
//        assert(result.first == user)
//        assert(result.second.size == 2)
//        assert(result.second[0].user == friendInfos[1])
//        assert(result.second[1].user == friendInfos[0])
//    }
}
