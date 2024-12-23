package org.chewing.v1.facade

import io.mockk.every
import io.mockk.mockk
import org.chewing.v1.TestDataFactory
import org.chewing.v1.implementation.chat.room.ChatRoomAggregator
import org.chewing.v1.implementation.search.ChatRoomSearchEngine
import org.chewing.v1.implementation.search.FriendSearchEngine
import org.chewing.v1.model.friend.FriendSortCriteria
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.service.chat.ChatLogService
import org.chewing.v1.service.chat.RoomService
import org.chewing.v1.service.friend.FriendShipService
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class SearchFacadeTest {

    private val friendShipService: FriendShipService = mockk()
    private val roomService: RoomService = mockk()
    private val chatLogService: ChatLogService = mockk()
    private val chatRoomAggregator: ChatRoomAggregator = ChatRoomAggregator()
    private val friendSearchEngine: FriendSearchEngine = FriendSearchEngine()
    private val chatRoomSearchEngine = ChatRoomSearchEngine()

    private val searchFacade = SearchFacade(
        friendShipService,
        roomService,
        chatLogService,
        chatRoomAggregator,
        friendSearchEngine,
        chatRoomSearchEngine,
    )

    @Test
    fun `검색 결과를 가져와야 함 - 실패 키워드에 맞는 친구 이름이 없음 - 채팅방도 존재 하지 않음`() {
        val userId = "userId"
        val friendId = "friendId"

        val friendShip = TestDataFactory.createFriendShip(friendId, AccessStatus.ACCESS)
        val keyword = "keyword"

        every { friendShipService.getAccessFriendShips(userId, FriendSortCriteria.NAME) } returns listOf(friendShip)
        every { roomService.getChatRooms(userId) } returns listOf()
        every { chatLogService.getLatestChat(listOf()) } returns listOf()

        val search = searchFacade.search(userId, keyword)

        assert(search.chatRooms.isEmpty())
        assert(search.friends.isEmpty())
    }

    @Test
    fun `검색 결과를 가져와야 함 - 성공 키워드에 맞는 이름이 있음 - 채팅방은 존재 하지 않음`() {
        val userId = "userId10"
        val friendId = "friendId10"

        val friendShip = TestDataFactory.createFriendShip(friendId, AccessStatus.ACCESS)
        val keyword = friendShip.friendName.firstName

        every { friendShipService.getAccessFriendShips(userId, FriendSortCriteria.NAME) } returns listOf(friendShip)
        every { roomService.getChatRooms(userId) } returns listOf()
        every { chatLogService.getLatestChat(listOf()) } returns listOf()

        val search = searchFacade.search(userId, keyword)

        assert(search.chatRooms.isEmpty())
        assert(search.friends.size == 1)
        assert(search.friends[0].friendId == friendId)
        assert(search.friends[0].friendName == friendShip.friendName)
        assert(search.friends[0].isFavorite == friendShip.isFavorite)
        assert(search.friends[0].type == friendShip.type)
    }

    @Test
    fun `검색 결과를 가져와야 함 - 성공 키워드에 맞는 이름이 있음 - 채팅방은 존재 함`() {
        val userId = "userId20"
        val friendId = "friendId20"
        val chatRoomId = "chatRoomId20"
        val messageId = "messageId20"
        val time = LocalDateTime.now()

        val friendShip = TestDataFactory.createFriendShip(friendId, AccessStatus.ACCESS)
        val keyword = friendShip.friendName.firstName
        val roomInfo = TestDataFactory.createRoom(chatRoomId, userId, friendId, false)
        val chatLog = TestDataFactory.createChatNormalLog(
            messageId,
            chatRoomId,
            userId,
            TestDataFactory.createChatNumber(chatRoomId),
            time,
        )

        every { friendShipService.getAccessFriendShips(userId, FriendSortCriteria.NAME) } returns listOf(friendShip)
        every { roomService.getChatRooms(userId) } returns listOf(roomInfo)
        every { chatLogService.getLatestChat(listOf(chatRoomId)) } returns listOf(chatLog)

        val search = searchFacade.search(userId, keyword)

        assert(search.chatRooms.size == 1)
        assert(search.chatRooms[0].chatRoomId == chatRoomId)
        assert(search.chatRooms[0].groupChatRoom == roomInfo.groupChatRoom)
        assert(search.chatRooms[0].favorite == roomInfo.favorite)
        assert(search.chatRooms[0].latestMessage == chatLog.text)
        assert(search.chatRooms[0].latestMessageTime == chatLog.timestamp)
        assert(search.chatRooms[0].latestPage == chatLog.number.page)
        assert(search.chatRooms[0].latestSeqNumber == chatLog.number.sequenceNumber)
        assert(search.chatRooms[0].chatRoomMemberInfos.size == roomInfo.chatRoomMemberInfos.size)
        assert(search.friends.size == 1)
        assert(search.friends[0].friendId == friendId)
        assert(search.friends[0].friendName == friendShip.friendName)
        assert(search.friends[0].isFavorite == friendShip.isFavorite)
        assert(search.friends[0].type == friendShip.type)
    }

    @Test
    fun `검색 결과를 가져와야 함 - 성공 키워드에 맞는 이름이 있음 - 채팅방은 존재 함 - 즐겨 찾기 기준으로 정렬`() {
        val userId = "userId20"
        val friendId = "friendId20"
        val chatRoomId = "chatRoomId20"
        val messageId = "messageId20"
        val time = LocalDateTime.now()

        val friendShip = TestDataFactory.createFriendShip(friendId, AccessStatus.ACCESS)
        val keyword = friendShip.friendName.firstName
        val roomInfo = TestDataFactory.createRoom(chatRoomId, userId, friendId, false)
        val chatLog = TestDataFactory.createChatNormalLog(
            messageId,
            chatRoomId,
            userId,
            TestDataFactory.createChatNumber(chatRoomId),
            time,
        )

        every { friendShipService.getAccessFriendShips(userId, FriendSortCriteria.NAME) } returns listOf(friendShip)
        every { roomService.getChatRooms(userId) } returns listOf(roomInfo)
        every { chatLogService.getLatestChat(listOf(chatRoomId)) } returns listOf(chatLog)

        val search = searchFacade.search(userId, keyword)

        assert(search.chatRooms.size == 1)
        assert(search.chatRooms[0].chatRoomId == chatRoomId)
        assert(search.chatRooms[0].groupChatRoom == roomInfo.groupChatRoom)
        assert(search.chatRooms[0].favorite == roomInfo.favorite)
        assert(search.chatRooms[0].latestMessage == chatLog.text)
        assert(search.chatRooms[0].latestMessageTime == chatLog.timestamp)
        assert(search.chatRooms[0].latestPage == chatLog.number.page)
        assert(search.chatRooms[0].latestSeqNumber == chatLog.number.sequenceNumber)
        assert(search.chatRooms[0].chatRoomMemberInfos.size == roomInfo.chatRoomMemberInfos.size)
        assert(search.friends.size == 1)
        assert(search.friends[0].friendId == friendId)
        assert(search.friends[0].friendName == friendShip.friendName)
        assert(search.friends[0].isFavorite == friendShip.isFavorite)
        assert(search.friends[0].type == friendShip.type)
    }
}
