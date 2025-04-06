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
import org.chewing.v1.TestDataFactory
import org.chewing.v1.external.ExternalChatNotificationClient
import org.chewing.v1.external.ExternalPushNotificationClient
import org.chewing.v1.external.ExternalSessionClient
import org.chewing.v1.implementation.friend.friendship.FriendShipReader
import org.chewing.v1.implementation.notification.NotificationGenerator
import org.chewing.v1.implementation.notification.NotificationProducer
import org.chewing.v1.implementation.notification.NotificationSender
import org.chewing.v1.implementation.session.SessionProvider
import org.chewing.v1.implementation.user.UserReader
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.friend.FriendShipStatus
import org.chewing.v1.model.notification.Notification
import org.chewing.v1.model.notification.NotificationType
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.repository.friend.FriendShipRepository
import org.chewing.v1.repository.user.PushNotificationRepository
import org.chewing.v1.repository.user.UserRepository
import org.chewing.v1.service.notification.NotificationService
import org.chewing.v1.util.AsyncJobExecutor
import org.junit.jupiter.api.Test

class NotificationServiceTest {
    private val userRepository: UserRepository = mockk()
    private val pushNotificationRepository: PushNotificationRepository = mockk()
    private val externalPushNotificationClient: ExternalPushNotificationClient = mockk()
    private val externalChatNotificationClient: ExternalChatNotificationClient = mockk()
    private val externalSessionClient: ExternalSessionClient = mockk()
    private val friendShipRepository: FriendShipRepository = mockk()
    private val friendShipReader: FriendShipReader = FriendShipReader(friendShipRepository)

    private val userReader: UserReader = UserReader(userRepository, pushNotificationRepository)
    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private val asyncJobExecutor = AsyncJobExecutor(ioScope)
    private val notificationSender: NotificationSender =
        NotificationSender(externalPushNotificationClient, externalChatNotificationClient, asyncJobExecutor)
    private val sessionProvider: SessionProvider = SessionProvider(externalSessionClient)
    private val notificationProducer = NotificationProducer(friendShipReader, userReader)
    private val notificationGenerator = NotificationGenerator()
    private val notificationService =
        NotificationService(notificationGenerator, notificationSender, sessionProvider, notificationProducer)

    @Test
    fun `웹소켓 을 통한 전송`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val chatRoomId = ChatRoomId.of("chatRoomId")

        val chatMessage = TestDataFactory.createNormalMessage("messageId", chatRoomId)

        every { externalSessionClient.isOnline(friendId) } returns true
        every { externalChatNotificationClient.sendMessage(chatMessage, friendId) } just Runs

        // when
        notificationService.handleMessagesNotification(chatMessage, listOf(friendId), userId)

        // then
        verify { externalChatNotificationClient.sendMessage(chatMessage, friendId) }
    }

    @Test
    fun `채팅 메시지 푸시 알림 전송 - 단건 전송`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val chatRoomId = ChatRoomId.of("chatRoomId")
        val user = TestDataFactory.createUserInfo(userId, AccessStatus.ACCESS)
        val pushToken = TestDataFactory.createPushToken("pushToken", friendId)
        val friendShip = TestDataFactory.createFriendShip(userId, friendId, FriendShipStatus.FRIEND)
        val chatMessage = TestDataFactory.createNormalMessage("messageId", chatRoomId)
        val notificationSlot = slot<List<Notification>>()
        val friendUser = TestDataFactory.createUserInfo(friendId, AccessStatus.ACCESS)

        every { userRepository.read(userId, AccessStatus.ACCESS) } returns user
        every { userRepository.read(friendId, AccessStatus.ACCESS) } returns friendUser
        every { friendShipRepository.readByRelation(friendId, userId) } returns friendShip
        every { pushNotificationRepository.readAll(friendId) } returns listOf(pushToken)
        every { externalSessionClient.isOnline(friendId) } returns false
        coEvery { externalPushNotificationClient.sendPushNotifications(capture(notificationSlot)) } just Runs

        // when
        notificationService.handleMessageNotification(chatMessage, friendId, userId)

        // then
        coVerify(exactly = 1) { externalPushNotificationClient.sendPushNotifications(any()) }
    }

    @Test
    fun `웹소켓 을 통한 단건 알림 전송`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val chatRoomId = ChatRoomId.of("chatRoomId")

        val chatMessage = TestDataFactory.createNormalMessage("messageId", chatRoomId)

        every { externalSessionClient.isOnline(friendId) } returns true
        every { externalChatNotificationClient.sendMessage(chatMessage, friendId) } just Runs

        // when
        notificationService.handleMessageNotification(chatMessage, friendId, userId)

        // then
        verify { externalChatNotificationClient.sendMessage(chatMessage, friendId) }
    }

    @Test
    fun `채팅 메시지 푸시 알림 전송 - 일반 메시지`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val chatRoomId = ChatRoomId.of("chatRoomId")

        val user = TestDataFactory.createUserInfo(userId, AccessStatus.ACCESS)
        val pushToken = TestDataFactory.createPushToken("pushToken", friendId)
        val friendShip = TestDataFactory.createFriendShip(userId, friendId, FriendShipStatus.FRIEND)
        val chatMessage = TestDataFactory.createNormalMessage("messageId", chatRoomId)
        val notificationSlot = slot<List<Notification>>()
        val friendUser = TestDataFactory.createUserInfo(friendId, AccessStatus.ACCESS)

        every { userRepository.read(friendId, AccessStatus.ACCESS) } returns friendUser
        every { userRepository.read(userId, AccessStatus.ACCESS) } returns user
        every { friendShipRepository.readsByRelation(listOf(friendId), userId) } returns listOf(friendShip)
        every { pushNotificationRepository.readsAll(listOf(friendId)) } returns listOf(pushToken)
        every { externalSessionClient.isOnline(friendId) } returns false
        coEvery { externalPushNotificationClient.sendPushNotifications(capture(notificationSlot)) } just Runs
        // when
        notificationService.handleMessagesNotification(chatMessage, listOf(friendId), userId)
        // then

        val notifications = notificationSlot.captured

        notifications.forEach {
            assert(it.friendShip == friendShip)
            assert(it.pushInfo == pushToken)
            assert(it.type == NotificationType.DIRECT_CHAT_NORMAL)
            assert(it.targetId == chatRoomId.id)
            assert(it.content == chatMessage.text)
        }
    }

    @Test
    fun `채팅 메시지 푸시 알림 전송 - 읽음 메시지는 전송되지 않아야 함 빈리스트`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val pushTokenId = "pushToken"
        val friendShip = TestDataFactory.createFriendShip(userId, friendId, FriendShipStatus.FRIEND)
        val user = TestDataFactory.createUserInfo(userId, AccessStatus.ACCESS)
        val pushToken = TestDataFactory.createPushToken(pushTokenId, friendId)
        val chatRoomId = ChatRoomId.of("chatRoomId")
        val chatMessage = TestDataFactory.createReadMessage(chatRoomId)
        val notificationSlot = slot<List<Notification>>()
        val friendUser = TestDataFactory.createUserInfo(friendId, AccessStatus.ACCESS)

        every { userRepository.read(userId, AccessStatus.ACCESS) } returns user
        every { userRepository.read(friendId, AccessStatus.ACCESS) } returns friendUser
        every { friendShipRepository.readsByRelation(listOf(friendId), userId) } returns listOf(friendShip)
        every { pushNotificationRepository.readsAll(listOf(friendId)) } returns listOf(pushToken)
        every { externalSessionClient.isOnline(friendId) } returns false
        coEvery { externalPushNotificationClient.sendPushNotifications(capture(notificationSlot)) } just Runs

        // when
        notificationService.handleMessagesNotification(chatMessage, listOf(friendId), userId)

        coVerify(exactly = 1) {
            externalPushNotificationClient.sendPushNotifications(any())
        }
        val notifications = notificationSlot.captured
        assert(notifications.isEmpty())
    }

    @Test
    fun `채팅 메시지 푸시 알림 전송 - 댓긓 메시지`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val pushTokenId = "pushToken"
        val friendShip = TestDataFactory.createFriendShip(userId, friendId, FriendShipStatus.FRIEND)
        val user = TestDataFactory.createUserInfo(userId, AccessStatus.ACCESS)
        val pushToken = TestDataFactory.createPushToken(pushTokenId, friendId)
        val chatRoomId = ChatRoomId.of("chatRoomId")
        val chatNormalMessage = TestDataFactory.createChatNormalLog("messageId", chatRoomId, userId)
        val chatMessage = TestDataFactory.createReplyMessage("messageId", chatRoomId, chatNormalMessage)
        val notificationSlot = slot<List<Notification>>()
        val friendUser = TestDataFactory.createUserInfo(friendId, AccessStatus.ACCESS)

        every { userRepository.read(userId, AccessStatus.ACCESS) } returns user
        every { friendShipRepository.readsByRelation(listOf(friendId), userId) } returns listOf(friendShip)
        every { pushNotificationRepository.readsAll(listOf(friendId)) } returns listOf(pushToken)
        every { userRepository.read(friendId, AccessStatus.ACCESS) } returns friendUser
        every { externalSessionClient.isOnline(friendId) } returns false
        coEvery { externalPushNotificationClient.sendPushNotifications(capture(notificationSlot)) } just Runs

        // when
        notificationService.handleMessagesNotification(chatMessage, listOf(friendId), userId)
        // then

        coVerify { externalPushNotificationClient.sendPushNotifications(any()) }

        val notifications = notificationSlot.captured
        notifications.forEach {
            assert(it.friendShip == friendShip)
            assert(it.pushInfo == pushToken)
            assert(it.type == NotificationType.DIRECT_CHAT_REPLY)
            assert(it.targetId == chatRoomId.id)
            assert(it.content == chatMessage.text)
        }
    }

    @Test
    fun `채팅 메시지 푸시 알림 전송 - 파일 메시지`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val user = TestDataFactory.createUserInfo(userId, AccessStatus.ACCESS)
        val pushTokenId = "pushToken"
        val friendShip = TestDataFactory.createFriendShip(userId, friendId, FriendShipStatus.FRIEND)
        val pushToken = TestDataFactory.createPushToken(pushTokenId, friendId)
        val chatRoomId = ChatRoomId.of("chatRoomId")
        val chatMessage = TestDataFactory.createFileMessage("messageId", chatRoomId)
        val notificationSlot = slot<List<Notification>>()
        val friendUser = TestDataFactory.createUserInfo(friendId, AccessStatus.ACCESS)

        every { userRepository.read(userId, AccessStatus.ACCESS) } returns user
        every { userRepository.read(friendId, AccessStatus.ACCESS) } returns friendUser
        every { friendShipRepository.readsByRelation(listOf(friendId), userId) } returns listOf(friendShip)
        every { pushNotificationRepository.readsAll(listOf(friendId)) } returns listOf(pushToken)
        every { externalSessionClient.isOnline(friendId) } returns false
        coEvery { externalPushNotificationClient.sendPushNotifications(capture(notificationSlot)) } just Runs
        // when
        notificationService.handleMessagesNotification(chatMessage, listOf(friendId), userId)

        val notifications = notificationSlot.captured
        notifications.forEach {
            assert(it.friendShip == friendShip)
            assert(it.pushInfo == pushToken)
            assert(it.type == NotificationType.DIRECT_CHAT_FILE)
            assert(it.targetId == chatRoomId.id)
            assert(it.content == chatMessage.medias.first().url)
        }
    }

    @Test
    fun `채팅 메시지 푸시 알림 전송 - 초대 메시지`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val pushTokenId = "pushToken"
        val user = TestDataFactory.createUserInfo(userId, AccessStatus.ACCESS)
        val friendShip = TestDataFactory.createFriendShip(userId, friendId, FriendShipStatus.FRIEND)
        val pushToken = TestDataFactory.createPushToken(pushTokenId, friendId)
        val chatRoomId = ChatRoomId.of("chatRoomId")
        val chatMessage = TestDataFactory.createInviteMessage("messageId", chatRoomId)
        val notificationSlot = slot<List<Notification>>()
        val friendUser = TestDataFactory.createUserInfo(friendId, AccessStatus.ACCESS)

        every { userRepository.read(userId, AccessStatus.ACCESS) } returns user
        every { userRepository.read(friendId, AccessStatus.ACCESS) } returns friendUser
        every { externalSessionClient.isOnline(friendId) } returns false
        every { friendShipRepository.readsByRelation(listOf(friendId), userId) } returns listOf(friendShip)
        every { pushNotificationRepository.readsAll(listOf(friendId)) } returns listOf(pushToken)

        coEvery { externalPushNotificationClient.sendPushNotifications(capture(notificationSlot)) } just Runs
        // when
        notificationService.handleMessagesNotification(chatMessage, listOf(friendId), userId)
        // then

        coVerify(exactly = 1) { externalPushNotificationClient.sendPushNotifications(any()) }

        val notifications = notificationSlot.captured
        notifications.forEach {
            assert(it.friendShip == friendShip)
            assert(it.pushInfo == pushToken)
            assert(it.type == NotificationType.GROUP_CHAT_INVITE)
            assert(it.targetId == chatRoomId.id)
            assert(it.content == "${friendShip.friendName}님이 초대했습니다.")
        }
    }

    @Test
    fun `채팅 메시지 푸시 알림 전송 - 나가기 메시지`() {
        // Given
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val user = TestDataFactory.createUserInfo(userId, AccessStatus.ACCESS)
        val pushTokenId = "pushToken"
        val friendShip = TestDataFactory.createFriendShip(userId, friendId, FriendShipStatus.FRIEND)
        val chatRoomId = ChatRoomId.of("chatRoomId")
        val pushToken = TestDataFactory.createPushToken(pushTokenId, friendId)
        val chatMessage = TestDataFactory.createLeaveMessage("messageId", chatRoomId)
        val notificationSlot = slot<List<Notification>>()
        val friendUser = TestDataFactory.createUserInfo(friendId, AccessStatus.ACCESS)

        every { userRepository.read(userId, AccessStatus.ACCESS) } returns user
        every { externalSessionClient.isOnline(friendId) } returns false
        every { userRepository.read(friendId, AccessStatus.ACCESS) } returns friendUser
        coEvery { externalPushNotificationClient.sendPushNotifications(capture(notificationSlot)) } just Runs
        every { friendShipRepository.readsByRelation(listOf(friendId), userId) } returns listOf(friendShip)
        every { pushNotificationRepository.readsAll(listOf(friendId)) } returns listOf(pushToken)

        // When

        notificationService.handleMessagesNotification(chatMessage, listOf(friendId), userId)

        // Then
        coVerify(exactly = 1) { externalPushNotificationClient.sendPushNotifications(any()) }

        val notifications = notificationSlot.captured

        notifications.forEach {
            assert(it.friendShip == friendShip)
            assert(it.pushInfo == pushToken)
            assert(it.type == NotificationType.GROUP_CHAT_LEAVE)
            assert(it.targetId == chatRoomId.id)
            assert(it.content == "${friendShip.friendName}님이 나갔습니다.")
        }
    }
}
