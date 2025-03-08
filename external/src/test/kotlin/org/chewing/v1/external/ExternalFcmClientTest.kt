package org.chewing.v1.external

class ExternalFcmClientTest {
//    companion object {
//        private lateinit var mockWebServer: MockWebServer
//
//        @BeforeAll
//        @JvmStatic
//        fun setup() {
//            mockWebServer = MockWebServer()
//            mockWebServer.start()
//        }
//
//        @AfterAll
//        @JvmStatic
//        fun teardown() {
//            mockWebServer.shutdown()
//        }
//    }
//
//    private val mockServerUrl = mockWebServer.url("/").toString().removeSuffix("/")
//
//    private val mockResource: ClassPathResource = mockk()
//
//    private val mockAccessTokenValue = "mockAccessTokenValue"
//
//    private val objectMapper = jacksonObjectMapper().registerModule(
//        KotlinModule.Builder()
//            .build(),
//    )
//    private val pushConfig = object : PushConfig(
//    )
//
//    private val expoClient = ExpoClient(
//        pushConfig.expoWebClient(),
//    )
//
//    private val externalPushNotificationClientImpl = ExternalPushNotificationClientImpl(
//        expoClient,
//    )

//    @Test
//    fun `Fcm 테스트`() = runBlocking {
//        val notification = TestDataFactory.createNotification()
//
//        mockWebServer.enqueue(
//            MockResponse()
//                .setResponseCode(200)
//                .setBody("{\"success\": true}"),
//        )
//
//        externalPushNotificationClientImpl.sendFcmNotification(notification)
//
//        val recordedRequest = mockWebServer.takeRequest()
//        val headers = recordedRequest.headers
//        assert(headers["Authorization"] == "Bearer $mockAccessTokenValue")
//        assert(headers["Content-Type"] == "${ContentType.APPLICATION_JSON.mimeType};charset=UTF-8")
//
//        val requestBody = recordedRequest.body.readUtf8()
//        val fcmMessageDto = objectMapper.readValue(requestBody, FcmMessageDto::class.java)
//
//        assert(fcmMessageDto.message.token == notification.pushToken.fcmToken)
//        assert(fcmMessageDto.message.data["senderId"] == notification.userInfo.userId.id)
//        assert(fcmMessageDto.message.data["senderName"] == notification.userInfo.name)
//        assert(fcmMessageDto.message.data["type"] == notification.type.toLowerCase())
//        assert(fcmMessageDto.message.data["targetId"] == notification.targetId)
//        assert(fcmMessageDto.message.data["content"] == notification.content)
//    }
//
//    @Test
//    fun `Fcm 실패 테스트`() = runBlocking {
//        val notification = TestDataFactory.createNotification()
//
//        mockWebServer.enqueue(
//            MockResponse()
//                .setResponseCode(500)
//                .setBody("{\"error\": \"Internal Server Error\"}"),
//        )
//
//        val logger = LoggerFactory.getLogger(ExternalPushNotificationClientImpl::class.java) as Logger
//
//        val listAppender = ListAppender<ch.qos.logback.classic.spi.ILoggingEvent>()
//        listAppender.start()
//        logger.addAppender(listAppender)
//
//        externalPushNotificationClientImpl.sendFcmNotification(notification)
//
//        val logsList = listAppender.list
//        val warnLogs = logsList.filter { it.level == Level.WARN }
//        assert(warnLogs.any { it.formattedMessage.contains("Error response body") })
//    }
//
//    @Test
//    fun `Fcm 네트워크 오류 테스트`() = runBlocking {
//        val notification = TestDataFactory.createNotification()
//
//        val mockFcmClient = mockk<FcmClient>()
//        coEvery { mockFcmClient.execute(any()) } throws WebClientRequestException(
//            IOException("Network error"),
//            HttpMethod.POST,
//            URI.create(mockServerUrl),
//            HttpHeaders.EMPTY,
//        )
//
//        val externalPushNotificationClientImplWithMock = ExternalPushNotificationClientImpl(
//            fcmClient = mockFcmClient,
//        )
//
//        val logger = LoggerFactory.getLogger(ExternalPushNotificationClientImpl::class.java) as Logger
//
//        val listAppender = ListAppender<ch.qos.logback.classic.spi.ILoggingEvent>()
//        listAppender.start()
//        logger.addAppender(listAppender)
//
//        externalPushNotificationClientImplWithMock.sendFcmNotification(notification)
//
//        val logsList = listAppender.list
//        val errorLogs = logsList.filter { it.level == Level.ERROR }
//        assert(errorLogs.any { it.formattedMessage.contains("Failed to send FCM notification") })
//    }
}
