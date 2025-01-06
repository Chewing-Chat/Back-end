package org.chewing.v1.facade

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.chewing.v1.TestDataFactory
import org.chewing.v1.service.chat.ChatLogService
import org.chewing.v1.service.chat.RoomService
import org.chewing.v1.service.notification.NotificationService
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ChatFacadeTest {
    private val chatLogService: ChatLogService = mockk()
    private val roomService: RoomService = mockk()
    private val notificationService: NotificationService = mockk()

    private val chatFacade = ChatFacade(chatLogService, roomService, notificationService)

    @Test
    fun `파일 전송 채팅 처리`() {
        val messageId = "messageId"
        val userId = "userId"
        val chatRoomId = "chatRoomId"

        val chatMessage = TestDataFactory.createFileMessage(messageId, chatRoomId)
        val chatRoomMemberInfo = TestDataFactory.createChatRoomMemberInfo(userId, chatRoomId, 1, false)
        val chatRoomInfo = TestDataFactory.createChatRoomInfo(chatRoomId)

        every { chatLogService.uploadFiles(any(), any(), any()) } returns chatMessage
        every { roomService.activateChatRoom(any(), any(), any()) } returns chatRoomInfo
        every { roomService.getChatRoomFriends(any(), any(), any()) } returns listOf(chatRoomMemberInfo)
        every { notificationService.handleOwnedMessageNotification(any()) } just Runs
        every { notificationService.handleMessagesNotification(any(), any(), any()) } just Runs

        chatFacade.processFiles(listOf(), userId, chatRoomId)

        verify { notificationService.handleMessagesNotification(chatMessage, listOf(chatRoomMemberInfo.memberId), userId) }
    }

    @Test
    fun `읽음 처리`() {
        val messageId = "messageId"
        val userId = "userId"
        val chatRoomId = "chatRoomId"

        val chatMessage = TestDataFactory.createReadMessage(messageId)
        val chatRoomMemberInfo = TestDataFactory.createChatRoomMemberInfo(userId, chatRoomId, 1, false)
        val chatRoomInfo = TestDataFactory.createChatRoomInfo(chatRoomId)

        every { chatLogService.readMessage(any(), any()) } returns chatMessage
        every { roomService.getChatRoom(any()) } returns chatRoomInfo
        every { roomService.getChatRoomFriends(any(), any(), any()) } returns listOf(chatRoomMemberInfo)
        every { notificationService.handleOwnedMessageNotification(any()) } just Runs
        every { notificationService.handleMessagesNotification(any(), any(), any()) } just Runs
        every { roomService.updateReadChatRoom(any(), any(), any()) } just Runs
        chatFacade.processRead(chatRoomId, userId)

        verify { notificationService.handleMessagesNotification(chatMessage, listOf(chatRoomMemberInfo.memberId), userId) }
    }

    @Test
    fun `삭제 처리`() {
        val messageId = "messageId"
        val userId = "userId"
        val chatRoomId = "chatRoomId"

        val chatMessage = TestDataFactory.createDeleteMessage(messageId, chatRoomId)
        val chatRoomMemberInfo = TestDataFactory.createChatRoomMemberInfo(userId, chatRoomId, 1, false)
        val chatRoomInfo = TestDataFactory.createChatRoomInfo(chatRoomId)

        every { chatLogService.deleteMessage(any(), any(), any()) } returns chatMessage
        every { roomService.getChatRoom(any()) } returns chatRoomInfo
        every { roomService.getChatRoomFriends(any(), any(), any()) } returns listOf(chatRoomMemberInfo)
        every { notificationService.handleOwnedMessageNotification(any()) } just Runs
        every { notificationService.handleMessagesNotification(any(), any(), any()) } just Runs

        chatFacade.processDelete(chatRoomId, userId, messageId)

        verify { notificationService.handleMessagesNotification(chatMessage, listOf(chatRoomMemberInfo.memberId), userId) }
    }

    @Test
    fun `답장 처리`() {
        val messageId = "messageId"
        val userId = "userId"
        val chatRoomId = "chatRoomId"
        val parentMessageId = "parentMessageId"

        val chatMessage = TestDataFactory.createReplyMessage(messageId, parentMessageId)
        val chatRoomMemberInfo = TestDataFactory.createChatRoomMemberInfo(userId, chatRoomId, 1, false)
        val chatRoomInfo = TestDataFactory.createChatRoomInfo(chatRoomId)

        every { chatLogService.replyMessage(any(), any(), any(), any()) } returns chatMessage
        every { roomService.activateChatRoom(any(), any(), any()) } returns chatRoomInfo
        every { roomService.getChatRoomFriends(any(), any(), any()) } returns listOf(chatRoomMemberInfo)
        every { notificationService.handleOwnedMessageNotification(any()) } just Runs
        every { notificationService.handleMessagesNotification(any(), any(), any()) } just Runs

        chatFacade.processReply(chatRoomId, userId, parentMessageId, "text")

        verify { notificationService.handleMessagesNotification(chatMessage, listOf(chatRoomMemberInfo.memberId), userId) }
    }

    @Test
    fun `폭탄 처리`() {
        val messageId = "messageId"
        val userId = "userId"
        val chatRoomId = "chatRoomId"

        val chatMessage = TestDataFactory.createBombMessage(messageId, chatRoomId)
        val chatRoomMemberInfo = TestDataFactory.createChatRoomMemberInfo(userId, chatRoomId, 1, false)
        val chatRoomInfo = TestDataFactory.createChatRoomInfo(chatRoomId)

        every { chatLogService.bombingMessage(any(), any(), any(), any()) } returns chatMessage
        every { roomService.activateChatRoom(any(), any(), any()) } returns chatRoomInfo
        every { roomService.getChatRoomFriends(any(), any(), any()) } returns listOf(chatRoomMemberInfo)
        every { notificationService.handleOwnedMessageNotification(any()) } just Runs
        every { notificationService.handleMessagesNotification(any(), any(), any()) } just Runs

        chatFacade.processBombing(chatRoomId, userId, "text", LocalDateTime.now())

        verify { notificationService.handleMessagesNotification(chatMessage, listOf(chatRoomMemberInfo.memberId), userId) }
    }

    @Test
    fun `일반 채팅 처리`() {
        val messageId = "messageId"
        val userId = "userId"
        val chatRoomId = "chatRoomId"

        val chatMessage = TestDataFactory.createNormalMessage(messageId, chatRoomId)
        val chatRoomMemberInfo = TestDataFactory.createChatRoomMemberInfo(userId, chatRoomId, 1, false)
        val chatRoomInfo = TestDataFactory.createChatRoomInfo(chatRoomId)

        every { chatLogService.chatNormalMessage(any(), any(), any()) } returns chatMessage
        every { roomService.activateChatRoom(any(), any(), any()) } returns chatRoomInfo
        every { roomService.getChatRoomFriends(any(), any(), any()) } returns listOf(chatRoomMemberInfo)
        every { notificationService.handleOwnedMessageNotification(any()) } just Runs
        every { notificationService.handleMessagesNotification(any(), any(), any()) } just Runs

        chatFacade.processCommon(chatRoomId, userId, "text")

        verify { notificationService.handleMessagesNotification(chatMessage, listOf(chatRoomMemberInfo.memberId), userId) }
    }
}
