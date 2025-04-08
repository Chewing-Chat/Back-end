package org.chewing.v1.controller

import io.mockk.every
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.RestDocsUtils.requestAccessTokenFields
import org.chewing.v1.RestDocsUtils.requestPreprocessor
import org.chewing.v1.RestDocsUtils.responsePreprocessor
import org.chewing.v1.TestDataFactory
import org.chewing.v1.TestDataFactory.createFeed
import org.chewing.v1.controller.main.MainController
import org.chewing.v1.facade.DirectChatFacade
import org.chewing.v1.facade.FriendFacade
import org.chewing.v1.facade.FriendFeedFacade
import org.chewing.v1.facade.GroupChatFacade
import org.chewing.v1.model.chat.log.ChatCommentLog
import org.chewing.v1.model.chat.log.ChatFileLog
import org.chewing.v1.model.chat.log.ChatInviteLog
import org.chewing.v1.model.chat.log.ChatLeaveLog
import org.chewing.v1.model.chat.log.ChatNormalLog
import org.chewing.v1.model.chat.log.ChatReplyLog
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.feed.FeedType
import org.chewing.v1.model.friend.FriendShipStatus
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.user.UserService
import org.chewing.v1.util.handler.GlobalExceptionHandler
import org.chewing.v1.util.security.UserArgumentResolver
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import java.time.format.DateTimeFormatter

@ActiveProfiles("test")
class MainControllerTest : RestDocsTest() {

    private lateinit var userService: UserService
    private lateinit var friendFacade: FriendFacade
    private lateinit var directChatFacade: DirectChatFacade
    private lateinit var groupChatFacade: GroupChatFacade
    private lateinit var friendFeedFacade: FriendFeedFacade
    private lateinit var userArgumentResolver: UserArgumentResolver
    private lateinit var mainController: MainController
    private lateinit var exceptionHandler: GlobalExceptionHandler

    @BeforeEach
    fun setUp() {
        userService = mockk()
        friendFacade = mockk()
        directChatFacade = mockk()
        groupChatFacade = mockk()
        friendFeedFacade = mockk()
        exceptionHandler = GlobalExceptionHandler()
        mainController = MainController(userService, friendFacade, directChatFacade, groupChatFacade, friendFeedFacade)
        userArgumentResolver = UserArgumentResolver()
        mockMvc = mockController(
            mainController,
            exceptionHandler,
            userArgumentResolver,
        )
        val userId = UserId.of("testUserId")
        val authentication = UsernamePasswordAuthenticationToken(userId, null)
        SecurityContextHolder.getContext().authentication = authentication
    }

    @Test
    @DisplayName("메인 페이지 조회")
    fun getMainPage() {
        val directChatRoomId = ChatRoomId.of("directChatRoomId")
        val groupChatRoomId = ChatRoomId.of("groupChatRoomId")
        val userId = UserId.of("testUserId")
        val user = TestDataFactory.createUser(
            userId.id,
            AccessStatus.ACCESS,
        )

        val friends = listOf(
            TestDataFactory.createFriend("testUserId", FriendShipStatus.FRIEND),
            TestDataFactory.createFriend("testUserId", FriendShipStatus.DELETE),
            TestDataFactory.createFriend("testUserId", FriendShipStatus.BLOCK),
            TestDataFactory.createFriend("testUserId", FriendShipStatus.FRIEND),
        )
        val feedId = FeedId.of("testFeedId")
        val directChatRoom = TestDataFactory.createDirectChatRoom(directChatRoomId)
        val directChatLogs = TestDataFactory.createDirectChatLogs(directChatRoomId)
        val directChat = listOf(Pair(directChatRoom, directChatLogs))
        val groupChatRoom = TestDataFactory.createGroupChatRoom(groupChatRoomId)
        val groupChatLogs = TestDataFactory.createGroupChatLogs(groupChatRoomId)
        val groupChat = listOf(Pair(groupChatRoom, groupChatLogs))
        val feeds = listOf(
            createFeed(feedId, FeedType.FILE),
            createFeed(feedId, FeedType.TEXT_SKY),
            createFeed(feedId, FeedType.TEXT_BLUE),
        )
        every { userService.getUser(any(), any()) } returns user
        every { friendFacade.getFriends(any()) } returns friends
        every { directChatFacade.processUnreadDirectChatLog(any()) } returns directChat
        every { groupChatFacade.processUnreadGroupChatLog(any()) } returns groupChat
        every { friendFeedFacade.getOneDayFeeds(userId) } returns feeds
        // when
        given()
            .setupAuthenticatedJsonRequest()
            .get("/api/main")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(200))
            .apply {
                body("data.user.userId", equalTo(user.info.userId.id))
                body("data.user.name", equalTo(user.info.name))
                body("data.user.statusMessage", equalTo(user.info.statusMessage))
                body("data.user.imageUrl", equalTo(user.info.image.url))
                body("data.user.imageType", equalTo(user.info.image.type.value().lowercase()))
                body("data.totalFriends", equalTo(friends.size))
                body("data.user.birthday", equalTo(user.info.birthday.toString()))
                friends.forEachIndexed { index, s ->
                    body("data.friends[$index].phoneNumber", equalTo(s.user.localPhoneNumber.number))
                    body("data.friends[$index].countryCode", equalTo(s.user.localPhoneNumber.countryCode))
                    body("data.friends[$index].name", equalTo(s.name))
                    body("data.friends[$index].favorite", equalTo(s.isFavorite))
                    body("data.friends[$index].status", equalTo(s.status.name.lowercase()))
                    body("data.friends[$index].friendId", equalTo(s.user.info.userId.id))
                    body("data.friends[$index].profileImageUrl", equalTo(s.user.info.image.url))
                    body("data.friends[$index].profileImageType", equalTo(s.user.info.image.type.value()))
                    body("data.friends[$index].statusMessage", equalTo(s.user.info.statusMessage))
                    body("data.friends[$index].birthday", equalTo(s.user.info.birthday.toString()))
                }
                groupChat.forEachIndexed { index, (groupChatRoom, logs) ->
                    val friendIds = groupChatRoom.memberInfos
                        .filter { it.memberId != userId }
                        .map { it.memberId.id }
                    body("data.groupChatRooms[$index].chatRoomId", equalTo(groupChatRoom.roomInfo.chatRoomId.id))
                    body("data.groupChatRooms[$index].friendIds", equalTo(friendIds))
                    logs.forEachIndexed { logIndex, log ->
                        // 응답 JSON Path 편하게 재사용하기 위한 변수
                        val path = "data.groupChatRooms[$index].chatLogs[$logIndex]"
                        val formattedTime = log.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

                        // (1) 공통 필드 검증
                        body("$path.messageId", equalTo(log.messageId))
                        body("$path.type", equalTo((log.type.name.lowercase())))
                        body("$path.senderId", equalTo(log.senderId.id))
                        body("$path.timestamp", equalTo(formattedTime))
                        body("$path.seqNumber", equalTo(log.roomSequence.sequence))
                        // (2) 타입별 검증
                        when (log) {
                            is ChatReplyLog -> {
                                body("$path.parentMessageId", equalTo(log.parentMessageId))
                                body("$path.parentSeqNumber", equalTo(log.parentSeqNumber))
                                body("$path.parentMessageText", equalTo(log.parentMessageText))
                                body("$path.parentMessageType", equalTo(log.parentMessageType.name.lowercase()))
                                body("$path.text", equalTo(log.text))
                            }

                            is ChatLeaveLog -> {
                            }

                            is ChatFileLog -> {
                                log.medias.forEachIndexed { mediaIndex, media ->
                                    val mediaPath = "$path.files[$mediaIndex]"
                                    body("$mediaPath.fileUrl", equalTo(media.url))
                                    body("$mediaPath.fileType", equalTo(media.type.value().lowercase()))
                                    body("$mediaPath.index", equalTo(media.index))
                                }
                            }

                            is ChatInviteLog -> {
                                body("$path.targetUserIds", equalTo(log.targetUserIds.map { it.id }))
                            }

                            is ChatNormalLog -> {
                                body("$path.text", equalTo(log.text))
                            }

                            else -> {}
                        }
                    }
                }
                directChat.forEachIndexed { directChatIndex, (room, logs) ->
                    // 먼저 chatRoomId, friendId 등 공통 필드 검증
                    body("data.directChatRooms[$directChatIndex].chatRoomId", equalTo(room.roomInfo.chatRoomId.id))
                    body("data.directChatRooms[$directChatIndex].friendId", equalTo(room.roomInfo.friendId.id))

                    // 이제 실제 로그 목록 검증
                    logs.forEachIndexed { logIndex, log ->
                        // 응답 JSON Path 편하게 재사용하기 위한 변수
                        val path = "data.directChatRooms[$directChatIndex].chatLogs[$logIndex]"
                        val formattedTime = log.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

                        // (1) 공통 필드 검증
                        body("$path.messageId", equalTo(log.messageId))
                        body("$path.type", equalTo((log.type.name.lowercase())))
                        body("$path.senderId", equalTo(log.senderId.id))
                        body("$path.timestamp", equalTo(formattedTime))
                        // (2) 타입별 검증
                        when (log) {
                            is ChatReplyLog -> {
                                body("$path.parentMessageId", equalTo(log.parentMessageId))
                                body("$path.parentSeqNumber", equalTo(log.parentSeqNumber))
                                body("$path.parentMessageText", equalTo(log.parentMessageText))
                                body("$path.parentMessageType", equalTo(log.parentMessageType.name.lowercase()))
                                body("$path.text", equalTo(log.text))
                            }

                            is ChatLeaveLog -> null

                            is ChatFileLog -> {
                                log.medias.forEachIndexed { mediaIndex, media ->
                                    val mediaPath = "$path.files[$mediaIndex]"
                                    body("$mediaPath.fileUrl", equalTo(media.url))
                                    body("$mediaPath.fileType", equalTo(media.type.value().lowercase()))
                                    body("$mediaPath.index", equalTo(media.index))
                                }
                            }

                            is ChatInviteLog -> null
                            is ChatNormalLog -> {
                                body("$path.text", equalTo(log.text))
                            }

                            is ChatCommentLog -> {
                                body("$path.comment", equalTo(log.comment))
                                body("$path.feedId", equalTo(log.feedId.id))
                                body("$path.feedType", equalTo(log.feedType.name.lowercase()))
                                body("$path.content", equalTo(log.content))
                                log.medias.forEachIndexed { mediaIndex, media ->
                                    val mediaPath = "$path.files[$mediaIndex]"
                                    body("$mediaPath.fileUrl", equalTo(media.url))
                                    body("$mediaPath.fileType", equalTo(media.type.value().lowercase()))
                                    body("$mediaPath.index", equalTo(media.index))
                                }
                            }
                        }
                    }
                }
                feeds.forEachIndexed { index, feed ->
                    val formattedUploadTime =
                        feed.feed.uploadAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    body("data.oneDayFeeds[$index].feedId", equalTo(feed.feed.feedId.id))
                    body("data.oneDayFeeds[$index].feedType", equalTo(feed.feed.type.name.lowercase()))
                    body("data.oneDayFeeds[$index].uploadAt", equalTo(formattedUploadTime))
                    body("data.oneDayFeeds[$index].ownerId", equalTo(feed.feed.userId.id))
                    body("data.oneDayFeeds[$index].content", equalTo(feed.feed.content))
                    when (feed.feed.type) {
                        FeedType.FILE -> {
                            body("data.oneDayFeeds[$index].thumbnailFileUrl", equalTo(feed.feedDetails[0].media.url))
                            body("data.oneDayFeeds[$index].count", equalTo(feed.feedDetails.size))
                        }

                        FeedType.TEXT_BLUE -> {
                        }

                        FeedType.TEXT_SKY -> {
                        }
                    }
                }
            }
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),
                        // data.user
                        fieldWithPath("data.user").description("사용자 정보"),
                        fieldWithPath("data.user.userId").description("사용자 ID"),
                        fieldWithPath("data.user.name").description("사용자 이름"),
                        fieldWithPath("data.user.statusMessage").description("사용자 상태 메시지"),
                        fieldWithPath("data.user.imageUrl").description("사용자 프로필 이미지 URL"),
                        fieldWithPath("data.user.imageType").description("프로필 이미지 타입 (예: 'default' / 'uploaded')"),
                        fieldWithPath("data.user.birthday").description("사용자 생일 (yyyy-MM-dd 형식) 없다면 빈칸으로 넘어옴"),

                        // data.friends
                        fieldWithPath("data.totalFriends").description("전체 친구 수"),
                        fieldWithPath("data.friends[]").description("친구 목록"),
                        fieldWithPath("data.friends[].phoneNumber").description("친구 전화번호"),
                        fieldWithPath("data.friends[].countryCode").description("친구 국가 코드"),
                        fieldWithPath("data.friends[].name").description("친구 이름"),
                        fieldWithPath("data.friends[].favorite").description("친구 즐겨찾기 여부"),
                        fieldWithPath("data.friends[].status").description("친구 상태 (friend, delete, block 등)"),
                        fieldWithPath("data.friends[].friendId").description("친구 사용자 ID"),
                        fieldWithPath("data.friends[].profileImageUrl").description("친구 프로필 이미지 URL"),
                        fieldWithPath("data.friends[].profileImageType").description("친구 프로필 이미지 타입"),
                        fieldWithPath("data.friends[].statusMessage").description("친구 상태 메시지"),
                        fieldWithPath("data.friends[].birthday").description("친구 생일 (yyyy-MM-dd 형식) 없다면 빈칸으로 넘어옴"),

                        // data.groupChatRooms
                        fieldWithPath("data.groupChatRooms[]").description("그룹 채팅방 목록"),
                        fieldWithPath("data.groupChatRooms[].chatRoomId").description("그룹 채팅방 ID"),
                        fieldWithPath("data.groupChatRooms[].friendIds[]").description("그룹 채팅방에 참여 중인 친구 ID 목록(본인 제외)"),
                        fieldWithPath("data.groupChatRooms[].chatLogs").description("해당 채팅방 채팅 로그 목록"),
                        fieldWithPath("data.groupChatRooms[].chatLogs[].messageId").description("메시지 ID"),
                        fieldWithPath("data.groupChatRooms[].chatLogs[].type").description("메시지 타입 (reply, file, normal, invite, leave 등)"),
                        fieldWithPath("data.groupChatRooms[].chatLogs[].senderId").description("메시지 전송자 ID"),
                        fieldWithPath("data.groupChatRooms[].chatLogs[].timestamp").description("메시지 전송 시각 (yy-MM-dd HH:mm:ss)"),
                        fieldWithPath("data.groupChatRooms[].chatLogs[].seqNumber").description("메시지 시퀀스 넘버 (1부터 시작)"),

                        // 타입별로 optional 필드 (Reply, Normal)
                        fieldWithPath("data.groupChatRooms[].chatLogs[].parentMessageId")
                            .optional().description("답장 대상 메시지 ID (reply 타입일 때만)"),
                        fieldWithPath("data.groupChatRooms[].chatLogs[].parentSeqNumber")
                            .optional().description("답장 대상 메시지 시퀀스 번호 (reply 타입일 때만)"),
                        fieldWithPath("data.groupChatRooms[].chatLogs[].parentMessageText")
                            .optional().description("답장 대상 메시지 내용 (reply 타입일 때만)"),
                        fieldWithPath("data.groupChatRooms[].chatLogs[].parentMessageType")
                            .optional().description("답장 대상 메시지 타입 (reply 타입일 때만)"),
                        fieldWithPath("data.groupChatRooms[].chatLogs[].text")
                            .optional().description("메시지 텍스트 (reply, normal 타입일 때만)"),

                        // 파일(File) 타입
                        fieldWithPath("data.groupChatRooms[].chatLogs[].files")
                            .optional().description("파일 목록 (file 타입일 때만)"),
                        fieldWithPath("data.groupChatRooms[].chatLogs[].files[].fileUrl")
                            .optional().description("파일 URL"),
                        fieldWithPath("data.groupChatRooms[].chatLogs[].files[].fileType")
                            .optional().description("파일 타입 (image 등)"),
                        fieldWithPath("data.groupChatRooms[].chatLogs[].files[].index")
                            .optional().description("파일 순서 (0부터 시작)"),

                        // 초대(Invite) 타입
                        fieldWithPath("data.groupChatRooms[].chatLogs[].targetUserIds")
                            .optional().description("초대 대상 사용자 ID 목록 (invite 타입일 때만)"),

                        // data.directChatRooms
                        fieldWithPath("data.directChatRooms[]").description("1:1 채팅방 목록"),
                        fieldWithPath("data.directChatRooms[].chatRoomId").description("1:1 채팅방 ID"),
                        fieldWithPath("data.directChatRooms[].friendId").description("상대방 친구 ID"),
                        fieldWithPath("data.directChatRooms[].chatLogs").description("해당 채팅방 채팅 로그 목록"),
                        fieldWithPath("data.directChatRooms[].chatLogs[].messageId").description("메시지 ID"),
                        fieldWithPath("data.directChatRooms[].chatLogs[].type").description("메시지 타입"),
                        fieldWithPath("data.directChatRooms[].chatLogs[].senderId").description("메시지 전송자 ID"),
                        fieldWithPath("data.directChatRooms[].chatLogs[].timestamp").description("메시지 전송 시각"),
                        fieldWithPath("data.directChatRooms[].chatLogs[].seqNumber").description("메시지 시퀀스 넘버(1부터 시작)"),

                        // 타입별 필드 (direct)
                        fieldWithPath("data.directChatRooms[].chatLogs[].parentMessageId")
                            .optional().description("답장 대상 메시지 ID (reply 타입)"),
                        fieldWithPath("data.directChatRooms[].chatLogs[].parentSeqNumber")
                            .optional().description("답장 대상 메시지 시퀀스 번호 (reply 타입)"),
                        fieldWithPath("data.directChatRooms[].chatLogs[].parentMessageText")
                            .optional().description("답장 대상 메시지 내용 (reply 타입)"),
                        fieldWithPath("data.directChatRooms[].chatLogs[].parentMessageType")
                            .optional().description("답장 대상 메시지 타입 (reply 타입)"),
                        fieldWithPath("data.directChatRooms[].chatLogs[].text")
                            .optional().description("메시지 텍스트 (reply, normal 타입)"),

                        fieldWithPath("data.directChatRooms[].chatLogs[].files")
                            .optional().description("파일 목록 (file 타입)"),
                        fieldWithPath("data.directChatRooms[].chatLogs[].files[].fileUrl")
                            .optional().description("파일 URL"),
                        fieldWithPath("data.directChatRooms[].chatLogs[].files[].fileType")
                            .optional().description("파일 타입"),
                        fieldWithPath("data.directChatRooms[].chatLogs[].files[].index")
                            .optional().description("파일 순서(0 부터 시작)"),

                        fieldWithPath("data.oneDayFeeds[].feedId").description("피드 아이디"),
                        fieldWithPath("data.oneDayFeeds[].uploadAt").description("피드 업로드 시간 - 형식 yyyy-MM-dd HH:mm:ss"),
                        fieldWithPath("data.oneDayFeeds[].feedType").description("피드 타입(TEXT_BLUE, TEXT_SKY, FILE)"),
                        fieldWithPath("data.oneDayFeeds[].ownerId").description("피드 소유자 ID"),
                        fieldWithPath("data.oneDayFeeds[].content").description("피드 내용"),

                        // FILE 타입에 대한 설명
                        fieldWithPath("data.oneDayFeeds[].thumbnailFileUrl").optional().description("썸네일 파일 URL (파일 타입일 경우)"),
                        fieldWithPath("data.oneDayFeeds[].fileType").optional().description("미디어 타입(image/png, image/jpeg, image/jpg, image/png) (파일 타입일 경우)"),
                        fieldWithPath("data.oneDayFeeds[].count").optional().description("파일 개수 (파일 타입일 경우)"),
                    ),
                ),
            )
    }
}
