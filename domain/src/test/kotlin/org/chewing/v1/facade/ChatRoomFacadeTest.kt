package org.chewing.v1.facade

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.chewing.v1.TestDataFactory
import org.chewing.v1.implementation.chat.room.ChatRoomAggregator
import org.chewing.v1.model.chat.room.ChatRoomSortCriteria
import org.chewing.v1.service.chat.ChatLogService
import org.chewing.v1.service.chat.RoomService
import org.chewing.v1.service.notification.NotificationService
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ChatRoomFacadeTest {
    private val chatLogService: ChatLogService = mockk()
    private val roomService: RoomService = mockk()
    private val notificationService: NotificationService = mockk()
    private val chatRoomAggregator = ChatRoomAggregator()

    private val chatRoomFacade = ChatRoomFacade(chatLogService, roomService, chatRoomAggregator, notificationService)

    @Test
    fun `채팅방 들 에서 나가야 함`() {
        val chatRoomIds = listOf("1", "2", "3")
        val userId = "userId"
        val messageId = "messageId"
        val chatRoomId = "chatRoomId"
        val chatMessage = TestDataFactory.createLeaveMessage(messageId, chatRoomId)
        val chatRoomInfo = TestDataFactory.createChatRoomInfo(chatRoomId)
        val chatRoomMemberInfo =
            TestDataFactory.createChatRoomMemberInfo(chatRoomId, userId, chatMessage.number.sequenceNumber, false)

        every { roomService.deleteGroupChatRooms(any(), any()) } just Runs
        every { chatLogService.leaveMessages(any(), any()) } returns listOf(chatMessage)
        every { roomService.activateChatRoom(any(), any(), any()) } returns chatRoomInfo
        every { roomService.getChatRoomFriends(any(), any(), any()) } returns listOf(chatRoomMemberInfo)
        every { notificationService.handleMessagesNotification(any(), any(), any()) } just Runs

        chatRoomFacade.leavesChatRoom(chatRoomIds, userId)

        verify { notificationService.handleMessagesNotification(chatMessage, listOf(userId), userId) }
    }

    @Test
    fun `그룹 채팅방을 만들어야 함`() {
        val userId = "userId"
        val friendIds = listOf("friendId1", "friendId2")
        val newRoomId = "newRoomId"
        val chatMessage = TestDataFactory.createInviteMessage(newRoomId, userId)

        every { roomService.createGroupChatRoom(any(), any()) } returns newRoomId
        every { chatLogService.inviteMessages(any(), any(), any()) } returns chatMessage
        every { notificationService.handleMessagesNotification(any(), any(), any()) } just Runs

        val result = chatRoomFacade.createGroupChatRoom(userId, friendIds)

        assert(result == newRoomId)
        verify { notificationService.handleMessagesNotification(chatMessage, friendIds, userId) }
    }

    @Test
    fun `채팅방 목록을 가져와야 함 - 일반 메시지`() {
        val userId = "userId"
        val chatRoomId = "chatRoomId"
        val messageId = "messageId"
        val friendId = "friendId"
        val sort = ChatRoomSortCriteria.DATE
        val time = LocalDateTime.now()

        // chatRoomId2 가 더 최근에 대화가 있음
        val chatRoomInfo = TestDataFactory.createRoom(chatRoomId, userId, friendId, false)
        val chatNumber = TestDataFactory.createChatNumber(chatRoomId)
        val latestChatLog = TestDataFactory.createChatNormalLog(messageId, chatRoomId, userId, chatNumber, time)

        every { roomService.getChatRooms(userId) } returns listOf(chatRoomInfo)
        every { chatLogService.getLatestChat(listOf(chatRoomId)) } returns listOf(latestChatLog)

        val result = chatRoomFacade.getChatRooms(userId, sort)

        assert(result.size == 1)
        assert(result[0].chatRoomId == chatRoomId)
        assert(result[0].latestMessage == latestChatLog.text)
    }

    @Test
    fun `채팅방 목록을 가져와야 함 - 파일 메시지`() {
        val userId = "userId"
        val chatRoomId = "chatRoomId"
        val messageId = "messageId"
        val friendId = "friendId"
        val sort = ChatRoomSortCriteria.DATE

        val chatRoomInfo = TestDataFactory.createRoom(chatRoomId, userId, friendId, false)
        val chatNumber = TestDataFactory.createChatNumber(chatRoomId)
        val latestChatLog = TestDataFactory.createChatFileLog(messageId, chatRoomId, userId, chatNumber)

        every { roomService.getChatRooms(userId) } returns listOf(chatRoomInfo)
        every { chatLogService.getLatestChat(listOf(chatRoomId)) } returns listOf(latestChatLog)

        val result = chatRoomFacade.getChatRooms(userId, sort)

        assert(result.size == 1)
        assert(result[0].chatRoomId == chatRoomId)
        assert(result[0].latestMessage == latestChatLog.medias[0].url)
    }

    @Test
    fun `채팅방 목록을 가져와야 함 - 초대 메시지`() {
        val userId = "userId"
        val chatRoomId = "chatRoomId"
        val messageId = "messageId"
        val friendId = "friendId"
        val sort = ChatRoomSortCriteria.DATE

        // chatRoomId2 가 더 최근에 대화가 있음
        val chatRoomInfo = TestDataFactory.createRoom(chatRoomId, userId, friendId, false)
        val chatNumber = TestDataFactory.createChatNumber(chatRoomId)
        val latestChatLog = TestDataFactory.createChatInviteLog(messageId, chatRoomId, userId, chatNumber)

        every { roomService.getChatRooms(userId) } returns listOf(chatRoomInfo)
        every { chatLogService.getLatestChat(listOf(chatRoomId)) } returns listOf(latestChatLog)

        val result = chatRoomFacade.getChatRooms(userId, sort)

        assert(result.size == 1)
        assert(result[0].chatRoomId == chatRoomId)
        assert(result[0].latestMessage == "")
    }

    @Test
    fun `채팅방 목록을 가져와야 함 - 폭탄 메시지`() {
        val userId = "userId"
        val chatRoomId = "chatRoomId"
        val messageId = "messageId"
        val friendId = "friendId"
        val sort = ChatRoomSortCriteria.DATE

        // chatRoomId2 가 더 최근에 대화가 있음
        val chatRoomInfo = TestDataFactory.createRoom(chatRoomId, userId, friendId, false)
        val chatNumber = TestDataFactory.createChatNumber(chatRoomId)
        val latestChatLog = TestDataFactory.createChatBombLog(messageId, chatRoomId, userId, chatNumber)

        every { roomService.getChatRooms(userId) } returns listOf(chatRoomInfo)
        every { chatLogService.getLatestChat(listOf(chatRoomId)) } returns listOf(latestChatLog)

        val result = chatRoomFacade.getChatRooms(userId, sort)

        assert(result.size == 1)
        assert(result[0].chatRoomId == chatRoomId)
        assert(result[0].latestMessage == latestChatLog.text)
    }

    @Test
    fun `채팅방 목록을 가져와야 함 - 답글 메시지`() {
        val userId = "userId"
        val chatRoomId = "chatRoomId"
        val messageId = "messageId"
        val friendId = "friendId"
        val sort = ChatRoomSortCriteria.DATE

        // chatRoomId2 가 더 최근에 대화가 있음
        val chatRoomInfo = TestDataFactory.createRoom(chatRoomId, userId, friendId, false)
        val chatNumber = TestDataFactory.createChatNumber(chatRoomId)
        val latestChatLog = TestDataFactory.createChatReplyLog(messageId, chatRoomId, userId, chatNumber)

        every { roomService.getChatRooms(userId) } returns listOf(chatRoomInfo)
        every { chatLogService.getLatestChat(listOf(chatRoomId)) } returns listOf(latestChatLog)

        val result = chatRoomFacade.getChatRooms(userId, sort)

        assert(result.size == 1)
        assert(result[0].chatRoomId == chatRoomId)
        assert(result[0].latestMessage == latestChatLog.text)
    }

    @Test
    fun `채팅방 목록을 가져와야 함 - 방 나감 메시지`() {
        val userId = "userId"
        val chatRoomId = "chatRoomId"
        val messageId = "messageId"
        val friendId = "friendId"
        val sort = ChatRoomSortCriteria.DATE

        // chatRoomId2 가 더 최근에 대화가 있음
        val chatRoomInfo = TestDataFactory.createRoom(chatRoomId, userId, friendId, false)
        val chatNumber = TestDataFactory.createChatNumber(chatRoomId)
        val latestChatLog = TestDataFactory.createChatLeaveLog(messageId, chatRoomId, userId, chatNumber)

        every { roomService.getChatRooms(userId) } returns listOf(chatRoomInfo)
        every { chatLogService.getLatestChat(listOf(chatRoomId)) } returns listOf(latestChatLog)

        val result = chatRoomFacade.getChatRooms(userId, sort)

        assert(result.size == 1)
        assert(result[0].chatRoomId == chatRoomId)
        assert(result[0].latestMessage == "")
    }

    @Test
    fun `채팅방 목록을 가져와야 함 - 최근 메시지 보낸 순`() {
        val userId = "userId"
        val chatRoomId = "chatRoomId"
        val chatRoomId2 = "chatRoomId2"
        val messageId = "messageId"
        val messageId2 = "messageId2"
        val friendId = "friendId"
        val sort = ChatRoomSortCriteria.DATE
        val time = LocalDateTime.now()
        val time2 = LocalDateTime.now().plusDays(1)

        // chatRoomId2 가 더 최근에 대화가 있음
        val chatRoomInfo = TestDataFactory.createRoom(chatRoomId, userId, friendId, false)
        val chatNumber = TestDataFactory.createChatNumber(chatRoomId)
        val latestChatLog = TestDataFactory.createChatNormalLog(messageId, chatRoomId, userId, chatNumber, time)

        val chatRoomInfo2 = TestDataFactory.createRoom(chatRoomId2, userId, friendId, false)
        val chatNumber2 = TestDataFactory.createChatNumber(chatRoomId2)
        val latestChatLog2 =
            TestDataFactory.createChatNormalLog(messageId2, chatRoomId2, userId, chatNumber2, time2)

        every { roomService.getChatRooms(userId) } returns listOf(chatRoomInfo2, chatRoomInfo)
        every { chatLogService.getLatestChat(listOf(chatRoomId2, chatRoomId)) } returns listOf(latestChatLog2, latestChatLog)

        val result = chatRoomFacade.getChatRooms(userId, sort)

        // 최근 대화가 있는 채팅방이 먼저 나와야 함
        assert(result.size == 2)
        assert(result[0].chatRoomId == chatRoomId2)
        assert(result[1].chatRoomId == chatRoomId)
        assert(result[0].latestMessageTime > result[1].latestMessageTime)
    }

    @Test
    fun `채티방 목록을 가져와야함 - 즐겨찾기를 한 순, 갖다면 최근 메시지 순으로`() {
        val userId = "userId"
        val chatRoomId = "chatRoomId"
        val chatRoomId2 = "chatRoomId2"
        val messageId = "messageId"
        val messageId2 = "messageId2"
        val chatRoomId3 = "chatRoomId3"
        val messageId3 = "messageId3"
        val friendId = "friendId"
        val sort = ChatRoomSortCriteria.FAVORITE
        val time = LocalDateTime.now()
        val time2 = LocalDateTime.now().plusDays(1)

        // 즐겨찾기를 하지 않고 최근 대화가 있음
        val chatRoomInfo = TestDataFactory.createRoom(chatRoomId, userId, friendId, false)
        val chatNumber = TestDataFactory.createChatNumber(chatRoomId)
        val latestChatLog = TestDataFactory.createChatNormalLog(messageId, chatRoomId, userId, chatNumber, time2)

        // 즐겨찾기를 하고 엤날 대화가 있음
        val chatRoomInfo2 = TestDataFactory.createRoom(chatRoomId2, userId, friendId, true)
        val chatNumber2 = TestDataFactory.createChatNumber(chatRoomId2)
        val latestChatLog2 = TestDataFactory.createChatNormalLog(messageId2, chatRoomId2, userId, chatNumber2, time)

        // 즐겨찾기를 하고 최근 대화가 있음
        val chatRoomInfo3 = TestDataFactory.createRoom(chatRoomId3, userId, friendId, true)
        val chatNumber3 = TestDataFactory.createChatNumber(chatRoomId3)
        val latestChatLog3 = TestDataFactory.createChatNormalLog(messageId3, chatRoomId3, userId, chatNumber3, time2)

        every { roomService.getChatRooms(userId) } returns listOf(chatRoomInfo2, chatRoomInfo, chatRoomInfo3)
        every { chatLogService.getLatestChat(listOf(chatRoomId2, chatRoomId, chatRoomId3)) } returns listOf(
            latestChatLog2,
            latestChatLog,
            latestChatLog3,
        )

        val result = chatRoomFacade.getChatRooms(userId, sort)

        // 즐겨찾기 있고 최근 대화가 있는 채팅방이 먼저 나와야 함
        assert(result.size == 3)
        assert(result[0].chatRoomId == chatRoomId3)
        assert(result[1].chatRoomId == chatRoomId2)
        assert(result[2].chatRoomId == chatRoomId)
        assert(result[0].favorite)
        assert(result[1].favorite)
        assert(!result[2].favorite)
        assert(result[0].latestMessageTime > result[1].latestMessageTime)
    }

    @Test
    fun `채티방 목록을 가져와야함 - 안 읽은 메시지를 한 순, 갖다면 최근 메시지 순으로`() {
        val userId = "userId"
        val chatRoomId = "chatRoomId"
        val chatRoomId2 = "chatRoomId2"
        val messageId = "messageId"
        val messageId2 = "messageId2"
        val chatRoomId3 = "chatRoomId3"
        val messageId3 = "messageId3"
        val friendId = "friendId"
        val sort = ChatRoomSortCriteria.NOT_READ
        val time = LocalDateTime.now()
        val time2 = LocalDateTime.now().plusDays(1)

        // 안읽은 메시지가 없고 최근 대화가 있음
        val chatRoomInfo = TestDataFactory.createRoom(chatRoomId, userId, friendId, false)
        val chatNumber = TestDataFactory.createChatNumber(chatRoomId)
        val latestChatLog = TestDataFactory.createChatNormalLog(messageId, chatRoomId, userId, chatNumber, time2)

        // 안읽은 메시지가 있고 엤날 대화가 있음
        val chatRoomInfo2 = TestDataFactory.createRoom(chatRoomId2, userId, friendId, true)
        val chatNumber2 = TestDataFactory.create100SeqChatNumber(chatRoomId2)
        val latestChatLog2 = TestDataFactory.createChatNormalLog(messageId2, chatRoomId2, userId, chatNumber2, time)

        // 안읽은 메시지가 있고 최근 대화가 있음
        val chatRoomInfo3 = TestDataFactory.createRoom(chatRoomId3, userId, friendId, true)
        val chatNumber3 = TestDataFactory.create100SeqChatNumber(chatRoomId3)
        val latestChatLog3 = TestDataFactory.createChatNormalLog(messageId3, chatRoomId3, userId, chatNumber3, time2)

        every { roomService.getChatRooms(userId) } returns listOf(chatRoomInfo2, chatRoomInfo, chatRoomInfo3)
        every { chatLogService.getLatestChat(listOf(chatRoomId2, chatRoomId, chatRoomId3)) } returns listOf(
            latestChatLog2,
            latestChatLog,
            latestChatLog3,
        )

        val result = chatRoomFacade.getChatRooms(userId, sort)

        // 안읽은 메시지가 있고, 최신 채팅 메시지가 있는 채팅방이 먼저 나와야 함
        assert(result.size == 3)
        assert(result[0].chatRoomId == chatRoomId3)
        assert(result[1].chatRoomId == chatRoomId2)
        assert(result[2].chatRoomId == chatRoomId)
        assert(result[0].favorite)
        assert(result[1].favorite)
        assert(!result[2].favorite)
        assert(result[0].latestMessageTime > result[1].latestMessageTime)
    }
}
