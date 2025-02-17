package org.chewing.v1.service

class NotificationServiceTest {
//    private val userRepository: UserRepository = mockk()
//    private val pushNotificationRepository: PushNotificationRepository = mockk()
//    private val externalPushNotificationClient: ExternalPushNotificationClient = mockk()
//    private val externalChatNotificationClient: ExternalChatNotificationClient = mockk()
//    private val externalSessionClient: ExternalSessionClient = mockk()
//
//    private val userReader: UserReader = UserReader(userRepository, pushNotificationRepository)
//    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
//    private val asyncJobExecutor = AsyncJobExecutor(ioScope)
//    private val notificationSender: NotificationSender =
//        NotificationSender(externalPushNotificationClient, externalChatNotificationClient, asyncJobExecutor)
//    private val sessionProvider: SessionProvider = SessionProvider(externalSessionClient)
//    private val notificationGenerator = NotificationGenerator()
//    private val notificationService =
//        NotificationService(userReader, notificationGenerator, notificationSender, sessionProvider)

//    @Test
//    fun `채팅 메시지 웹소켓 알림 전송`() {
//        val messageId = "messageId"
//        val targetUserId = TestDataFactory.createTargetUserId()
//        val chatRoomId = ChatRoomId.of("chatRoomId")
//        val chatMessage = TestDataFactory.createNormalMessage(messageId, chatRoomId)
//        every { externalSessionClient.isOnline(targetUserId) } returns false
//
//        verify(exactly = 0) { externalChatNotificationClient.sendMessage(chatMessage, targetUserId) }
//    }
//
//    @Test
//    fun `채팅 메시지 푸시 알림 전송 - 일반 메시지`() {
//        val userId = TestDataFactory.createUserId()
//        val friendId = TestDataFactory.createFriendId()
//        val user = TestDataFactory.createUserInfo(userId, AccessStatus.ACCESS)
//        val pushTokenId = "pushToken"
//        val pushToken = TestDataFactory.createPushToken(pushTokenId)
//        val chatRoomId = ChatRoomId.of("chatRoomId")
//        val chatMessage = TestDataFactory.createNormalMessage("messageId", chatRoomId)
//        val notificationSlot = slot<Notification>()
//
//        every { userRepository.read(userId, AccessStatus.ACCESS) } returns user
//        every { pushNotificationRepository.reads(friendId) } returns listOf(pushToken)
//        every { externalSessionClient.isOnline(friendId) } returns false
//        coEvery { externalPushNotificationClient.sendFcmNotification(capture(notificationSlot)) } just Runs
//        // when
//        notificationService.handleMessagesNotification(chatMessage, listOf(friendId), userId)
//        // then
//
//        val notification = notificationSlot.captured
//        assertEquals(userId, notification.userInfo.userId)
//        assertEquals(pushToken, notification.pushToken)
//        assertEquals(chatMessage.text, notification.content)
//        assertEquals(chatMessage.chatRoomId.id, notification.targetId)
//    }
//
//    @Test
//    fun `채팅 메시지 푸시 알림 전송되지 않음 - 읽음 메시지`() {
//        val userId = TestDataFactory.createUserId()
//        val friendId = TestDataFactory.createFriendId()
//        val pushTokenId = "pushToken"
//        val user = TestDataFactory.createUserInfo(userId, AccessStatus.ACCESS)
//        val pushToken = TestDataFactory.createPushToken(pushTokenId)
//        val chatRoomId = ChatRoomId.of("chatRoomId")
//        val chatMessage = TestDataFactory.createReadMessage(chatRoomId)
//
//        every { userRepository.read(userId, AccessStatus.ACCESS) } returns user
//        every { pushNotificationRepository.reads(friendId) } returns listOf(pushToken)
//        every { externalSessionClient.isOnline(friendId) } returns false
//
//        // when
//        notificationService.handleMessagesNotification(chatMessage, listOf(friendId), userId)
//        // then
//        coVerify(exactly = 0) { externalPushNotificationClient.sendFcmNotification(any()) }
//    }
//
//    @Test
//    fun `채팅 메시지 푸시 알림 전송 - 댓긓 메시지`() {
//        val userId = TestDataFactory.createUserId()
//        val friendId = TestDataFactory.createFriendId()
//        val pushTokenId = "pushToken"
//        val user = TestDataFactory.createUserInfo(userId, AccessStatus.ACCESS)
//        val pushToken = TestDataFactory.createPushToken(pushTokenId)
//        val chatRoomId = ChatRoomId.of("chatRoomId")
//        val chatNormalMessage = TestDataFactory.createChatNormalLog("messageId", chatRoomId, userId)
//        val chatMessage = TestDataFactory.createReplyMessage("messageId", chatRoomId,chatNormalMessage )
//        val notificationSlot = slot<Notification>()
//
//        every { userRepository.read(userId, AccessStatus.ACCESS) } returns user
//        every { pushNotificationRepository.reads(friendId) } returns listOf(pushToken)
//        every { externalSessionClient.isOnline(friendId) } returns false
//        coEvery { externalPushNotificationClient.sendFcmNotification(capture(notificationSlot)) } just Runs
//
//        // when
//        notificationService.handleMessagesNotification(chatMessage, listOf(friendId), userId)
//        // then
//
//        coVerify { externalPushNotificationClient.sendFcmNotification(any()) }
//
//        val notification = notificationSlot.captured
//        assertEquals(userId, notification.userInfo.userId)
//        assertEquals(pushToken, notification.pushToken)
//        assertEquals(chatMessage.text, notification.content)
//        assertEquals(chatMessage.chatRoomId.id, notification.targetId)
//    }
//
//    @Test
//    fun `채팅 메시지 푸시 알림 전송 - 파일 메시지`() {
//        val userId = TestDataFactory.createUserId()
//        val friendId = TestDataFactory.createFriendId()
//        val user = TestDataFactory.createUserInfo(userId, AccessStatus.ACCESS)
//        val pushTokenId = "pushToken"
//        val pushToken = TestDataFactory.createPushToken(pushTokenId)
//        val chatRoomId = ChatRoomId.of("chatRoomId")
//        val chatMessage = TestDataFactory.createFileMessage("messageId", chatRoomId)
//        val notificationSlot = slot<Notification>()
//
//        every { userRepository.read(userId, AccessStatus.ACCESS) } returns user
//        every { pushNotificationRepository.reads(friendId) } returns listOf(pushToken)
//        every { externalSessionClient.isOnline(friendId) } returns false
//        coEvery { externalPushNotificationClient.sendFcmNotification(capture(notificationSlot)) } just Runs
//        // when
//        notificationService.handleMessagesNotification(chatMessage, listOf(friendId), userId)
//
//        val notification = notificationSlot.captured
//        assertEquals(userId, notification.userInfo.userId)
//        assertEquals(pushToken, notification.pushToken)
//        assertEquals(chatMessage.medias.first().url, notification.content)
//        assertEquals(chatMessage.chatRoomId.id, notification.targetId)
//    }
//
//    @Test
//    fun `채팅 메시지 푸시 알림 전송 - 초대 메시지`() {
//        val userId = TestDataFactory.createUserId()
//        val friendId = TestDataFactory.createFriendId()
//        val pushTokenId = "pushToken"
//        val user = TestDataFactory.createUserInfo(userId, AccessStatus.ACCESS)
//        val pushToken = TestDataFactory.createPushToken(pushTokenId)
//        val chatRoomId = ChatRoomId.of("chatRoomId")
//        val chatMessage = TestDataFactory.createInviteMessage("messageId", chatRoomId)
//        val notificationSlot = slot<Notification>()
//
//        every { userRepository.read(userId, AccessStatus.ACCESS) } returns user
//        every { pushNotificationRepository.reads(friendId) } returns listOf(pushToken)
//        every { externalSessionClient.isOnline(friendId) } returns false
//        coEvery { externalPushNotificationClient.sendFcmNotification(capture(notificationSlot)) } just Runs
//        // when
//        notificationService.handleMessagesNotification(chatMessage, listOf(friendId), userId)
//        // then
//
//        coVerify(exactly = 1) { externalPushNotificationClient.sendFcmNotification(any()) }
//
//        val notification = notificationSlot.captured
//        assertEquals(userId, notification.userInfo.userId)
//        assertEquals(pushToken, notification.pushToken)
//        assertEquals("", notification.content)
//        assertEquals(chatMessage.chatRoomId.id, notification.targetId)
//    }
//
//    @Test
//    fun `채팅 메시지 푸시 알림 전송 - 나가기 메시지`() {
//        // Given
//        val userId = TestDataFactory.createUserId()
//        val friendId = TestDataFactory.createFriendId()
//        val user = TestDataFactory.createUserInfo(userId, AccessStatus.ACCESS)
//        val pushTokenId = "pushToken"
//        val chatRoomId = ChatRoomId.of("chatRoomId")
//        val pushToken = TestDataFactory.createPushToken(pushTokenId)
//        val chatMessage = TestDataFactory.createLeaveMessage("messageId", chatRoomId)
//        val notificationSlot = slot<Notification>()
//
//        every { userRepository.read(userId, AccessStatus.ACCESS) } returns user
//        every { pushNotificationRepository.reads(friendId) } returns listOf(pushToken)
//        every { externalSessionClient.isOnline(friendId) } returns false
//        coEvery { externalPushNotificationClient.sendFcmNotification(capture(notificationSlot)) } just Runs
//
//        // When
//        notificationService.handleMessagesNotification(chatMessage, listOf(friendId), userId)
//
//        // Then
//        coVerify(exactly = 1) { externalPushNotificationClient.sendFcmNotification(any()) }
//
//        val notification = notificationSlot.captured
//        assertEquals(userId, notification.userInfo.userId)
//        assertEquals(pushToken, notification.pushToken)
//        assertEquals("", notification.content)
//        assertEquals(chatMessage.chatRoomId.id, notification.targetId)
//    }
}
