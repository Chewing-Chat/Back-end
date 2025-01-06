package org.chewing.v1.service

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.chewing.v1.TestDataFactory
import org.chewing.v1.external.ExternalChatNotificationClient
import org.chewing.v1.external.ExternalPushNotificationClient
import org.chewing.v1.external.ExternalSessionClient
import org.chewing.v1.implementation.notification.NotificationGenerator
import org.chewing.v1.implementation.notification.NotificationSender
import org.chewing.v1.implementation.session.SessionProvider
import org.chewing.v1.implementation.user.user.UserReader
import org.chewing.v1.model.notification.Notification
import org.chewing.v1.repository.user.PushNotificationRepository
import org.chewing.v1.repository.user.UserRepository
import org.chewing.v1.service.notification.NotificationService
import org.chewing.v1.util.AsyncJobExecutor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NotificationServiceTest {
    private val userRepository: UserRepository = mockk()
    private val pushNotificationRepository: PushNotificationRepository = mockk()
    private val externalPushNotificationClient: ExternalPushNotificationClient = mockk()
    private val externalChatNotificationClient: ExternalChatNotificationClient = mockk()
    private val externalSessionClient: ExternalSessionClient = mockk()

    private val userReader: UserReader = UserReader(userRepository, pushNotificationRepository)
    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private val asyncJobExecutor = AsyncJobExecutor(ioScope)
    private val notificationSender: NotificationSender =
        NotificationSender(externalPushNotificationClient, externalChatNotificationClient, asyncJobExecutor)
    private val sessionProvider: SessionProvider = SessionProvider(externalSessionClient)
    private val notificationGenerator = NotificationGenerator()
    private val notificationService =
        NotificationService(userReader, notificationGenerator, notificationSender, sessionProvider)

    @Test
    fun `코멘트 앱 푸시 알림 전송시 알림이 재대로 생성 되어야 함`() {
        // Given
        val userId = "userId"
        val feedId = "feedId"
        val comment = "comment"
        val user = TestDataFactory.createUser(userId)
        val pushToken = TestDataFactory.createPushToken(userId)
        val notificationSlot = mutableListOf<Notification>()

        every { userRepository.read(userId) } returns user
        every { pushNotificationRepository.reads(userId) } returns listOf(pushToken, pushToken)
        coEvery { externalPushNotificationClient.sendFcmNotification(any()) } answers {
            notificationSlot.add(it.invocation.args[0] as Notification)
        }

        // When
        notificationService.handleCommentNotification(userId, feedId, comment)

        // Then
        coVerify(exactly = 2) { externalPushNotificationClient.sendFcmNotification(any()) }

        assertEquals(2, notificationSlot.size)
        notificationSlot.forEach { notification ->
            assertEquals(userId, notification.user.userId)
            assertEquals(pushToken, notification.pushToken)
            assertEquals(comment, notification.content)
            assertEquals(feedId, notification.targetId)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `알림 전송이 병렬적,비동기로 이루어져야 함`() = runTest {
        // Given
        val userId = "userId"
        val feedId = "feedId"
        val comment = "comment"
        val user = TestDataFactory.createUser(userId)
        val pushToken = TestDataFactory.createPushToken(userId)
        val pushTokens = listOf(pushToken, pushToken)

        every { userRepository.read(userId) } returns user
        every { pushNotificationRepository.reads(userId) } returns pushTokens

        coEvery { externalPushNotificationClient.sendFcmNotification(any()) } coAnswers {
            launch {
                delay(100)
            }
        }

        notificationService.handleCommentNotification(userId, feedId, comment)

        advanceUntilIdle()

        coVerify(exactly = pushTokens.size) { externalPushNotificationClient.sendFcmNotification(any()) }
        assert(currentTime == 100L)
    }

    @Test
    fun `채팅 메시지 웹소켓 푸시 전송자에게 알림 전송 성공`() {
        val messageId = "messageId"
        val chatRoomId = "chatRoomId"
        val userId = "userId"
        // given

        every { externalChatNotificationClient.sendMessage(any(), any()) } just Runs

        val chatMessage = TestDataFactory.createChatNormalMessage(messageId, chatRoomId, userId)
        // when

        notificationService.handleOwnedMessageNotification(chatMessage)

        verify(exactly = 1) { externalChatNotificationClient.sendMessage(any(), any()) }
    }

    @Test
    fun `채팅 메시지 웹소켓 친구에게 알림 전송 성공`() {
        val messageId = "messageId"
        val chatRoomId = "chatRoomId"
        val userId = "userId"
        val targetUserId = "targetId"

        val chatMessage = TestDataFactory.createChatNormalMessage(messageId, chatRoomId, userId)
        every { externalSessionClient.isOnline(targetUserId) } returns true

        verify(exactly = 0) { externalChatNotificationClient.sendMessage(chatMessage, targetUserId) }
    }

    @Test
    fun `채팅 메시지 웹소켓 알림 전송`() {
        val messageId = "messageId"
        val chatRoomId = "chatRoomId"
        val userId = "userId"
        val targetUserId = "targetId"

        val chatMessage = TestDataFactory.createChatNormalMessage(messageId, chatRoomId, userId)
        every { externalSessionClient.isOnline(targetUserId) } returns false

        verify(exactly = 0) { externalChatNotificationClient.sendMessage(chatMessage, targetUserId) }
    }

    @Test
    fun `채팅 메시지 푸시 알림 전송 - 일반 메시지`() {
        val userId = "userId"
        val friendId = "friendId"
        val user = TestDataFactory.createUser(userId)
        val pushToken = TestDataFactory.createPushToken(friendId)
        val chatMessage = TestDataFactory.createChatNormalMessage("messageId", "chatRoomId", userId)
        val notificationSlot = slot<Notification>()

        every { userRepository.read(userId) } returns user
        every { pushNotificationRepository.reads(friendId) } returns listOf(pushToken)
        every { externalSessionClient.isOnline(friendId) } returns false
        coEvery { externalPushNotificationClient.sendFcmNotification(capture(notificationSlot)) } just Runs
        // when
        notificationService.handleMessagesNotification(chatMessage, listOf(friendId), userId)
        // then

        val notification = notificationSlot.captured
        assertEquals(userId, notification.user.userId)
        assertEquals(pushToken, notification.pushToken)
        assertEquals(chatMessage.text, notification.content)
        assertEquals(chatMessage.chatRoomId, notification.targetId)
    }

    @Test
    fun `채팅 메시지 푸시 알림 전송 - 폭탄 메시지`() {
        val userId = "userId"
        val friendId = "friendId"
        val user = TestDataFactory.createUser(userId)
        val pushToken = TestDataFactory.createPushToken(friendId)
        val chatMessage = TestDataFactory.createBombMessage("messageId", "chatRoomId")
        val notificationSlot = slot<Notification>()

        every { userRepository.read(userId) } returns user
        every { pushNotificationRepository.reads(friendId) } returns listOf(pushToken)
        every { externalSessionClient.isOnline(friendId) } returns false
        coEvery { externalPushNotificationClient.sendFcmNotification(capture(notificationSlot)) } just Runs
        // when
        notificationService.handleMessagesNotification(chatMessage, listOf(friendId), userId)
        // then
        val notification = notificationSlot.captured
        assertEquals(userId, notification.user.userId)
        assertEquals(pushToken, notification.pushToken)
        assertEquals(chatMessage.text, notification.content)
        assertEquals(chatMessage.chatRoomId, notification.targetId)
    }

    @Test
    fun `채팅 메시지 푸시 알림 전송되지 않음 - 읽음 메시지`() {
        val userId = "userId"
        val friendId = "friendId"
        val user = TestDataFactory.createUser(userId)
        val pushToken = TestDataFactory.createPushToken(friendId)
        val chatMessage = TestDataFactory.createReadMessage("chatRoomId")

        every { userRepository.read(userId) } returns user
        every { pushNotificationRepository.reads(friendId) } returns listOf(pushToken)
        every { externalSessionClient.isOnline(friendId) } returns false

        // when
        notificationService.handleMessagesNotification(chatMessage, listOf(friendId), userId)
        // then
        coVerify(exactly = 0) { externalPushNotificationClient.sendFcmNotification(any()) }
    }

    @Test
    fun `채팅 메시지 푸시 알림 전송 - 댓긓 메시지`() {
        val userId = "userId"
        val friendId = "friendId"
        val user = TestDataFactory.createUser(userId)
        val pushToken = TestDataFactory.createPushToken(friendId)
        val chatMessage = TestDataFactory.createReplyMessage("messageId", "chatRoomId")
        val notificationSlot = slot<Notification>()

        every { userRepository.read(userId) } returns user
        every { pushNotificationRepository.reads(friendId) } returns listOf(pushToken)
        every { externalSessionClient.isOnline(friendId) } returns false
        coEvery { externalPushNotificationClient.sendFcmNotification(capture(notificationSlot)) } just Runs

        // when
        notificationService.handleMessagesNotification(chatMessage, listOf(friendId), userId)
        // then

        coVerify { externalPushNotificationClient.sendFcmNotification(any()) }

        val notification = notificationSlot.captured
        assertEquals(userId, notification.user.userId)
        assertEquals(pushToken, notification.pushToken)
        assertEquals(chatMessage.text, notification.content)
        assertEquals(chatMessage.chatRoomId, notification.targetId)
    }

    @Test
    fun `채팅 메시지 푸시 알림 전송 - 파일 메시지`() {
        val userId = "userId"
        val friendId = "friendId"
        val user = TestDataFactory.createUser(userId)
        val pushToken = TestDataFactory.createPushToken(friendId)
        val chatMessage = TestDataFactory.createFileMessage("messageId", "chatRoomId")
        val notificationSlot = slot<Notification>()

        every { userRepository.read(userId) } returns user
        every { pushNotificationRepository.reads(friendId) } returns listOf(pushToken)
        every { externalSessionClient.isOnline(friendId) } returns false
        coEvery { externalPushNotificationClient.sendFcmNotification(capture(notificationSlot)) } just Runs
        // when
        notificationService.handleMessagesNotification(chatMessage, listOf(friendId), userId)

        val notification = notificationSlot.captured
        assertEquals(userId, notification.user.userId)
        assertEquals(pushToken, notification.pushToken)
        assertEquals(chatMessage.medias.first().url, notification.content)
        assertEquals(chatMessage.chatRoomId, notification.targetId)
    }

    @Test
    fun `채팅 메시지 푸시 알림 전송 - 초대 메시지`() {
        val userId = "userId"
        val friendId = "friendId"
        val user = TestDataFactory.createUser(userId)
        val pushToken = TestDataFactory.createPushToken(friendId)
        val chatMessage = TestDataFactory.createInviteMessage("messageId", "chatRoomId")
        val notificationSlot = slot<Notification>()

        every { userRepository.read(userId) } returns user
        every { pushNotificationRepository.reads(friendId) } returns listOf(pushToken)
        every { externalSessionClient.isOnline(friendId) } returns false
        coEvery { externalPushNotificationClient.sendFcmNotification(capture(notificationSlot)) } just Runs
        // when
        notificationService.handleMessagesNotification(chatMessage, listOf(friendId), userId)
        // then

        coVerify(exactly = 1) { externalPushNotificationClient.sendFcmNotification(any()) }

        val notification = notificationSlot.captured
        assertEquals(userId, notification.user.userId)
        assertEquals(pushToken, notification.pushToken)
        assertEquals("", notification.content)
        assertEquals(chatMessage.chatRoomId, notification.targetId)
    }

    @Test
    fun `채팅 메시지 푸시 알림 전송 - 나가기 메시지`() {
        // Given
        val userId = "userId"
        val friendId = "friendId"
        val user = TestDataFactory.createUser(userId)
        val pushToken = TestDataFactory.createPushToken(friendId)
        val chatMessage = TestDataFactory.createLeaveMessage("messageId", "chatRoomId")
        val notificationSlot = slot<Notification>()

        every { userRepository.read(userId) } returns user
        every { pushNotificationRepository.reads(friendId) } returns listOf(pushToken)
        every { externalSessionClient.isOnline(friendId) } returns false
        coEvery { externalPushNotificationClient.sendFcmNotification(capture(notificationSlot)) } just Runs

        // When
        notificationService.handleMessagesNotification(chatMessage, listOf(friendId), userId)

        // Then
        coVerify(exactly = 1) { externalPushNotificationClient.sendFcmNotification(any()) }

        val notification = notificationSlot.captured
        assertEquals(userId, notification.user.userId)
        assertEquals(pushToken, notification.pushToken)
        assertEquals("", notification.content)
        assertEquals(chatMessage.chatRoomId, notification.targetId)
    }
}
