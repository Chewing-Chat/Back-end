package org.chewing.v1.controller

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.TestDataFactory
import org.chewing.v1.controller.chat.ChatRoomController
import org.chewing.v1.dto.request.chat.ChatRoomRequest
import org.chewing.v1.facade.ChatRoomFacade
import org.chewing.v1.model.chat.room.ChatRoomSortCriteria
import org.chewing.v1.service.chat.RoomService
import org.chewing.v1.util.converter.StringToChatRoomSortCriteriaConverter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.format.DateTimeFormatter

@ActiveProfiles("test")
class ChatRoomControllerTest : RestDocsTest() {

    private lateinit var chatRoomFacade: ChatRoomFacade
    private lateinit var roomService: RoomService
    private lateinit var chatRoomController: ChatRoomController

    @BeforeEach
    fun setUp() {
        chatRoomFacade = mockk()
        roomService = mockk()
        chatRoomController = ChatRoomController(chatRoomFacade, roomService)
        mockMvc = mockControllerWithCustomConverter(
            chatRoomController,
            StringToChatRoomSortCriteriaConverter(),
        )
    }

    @Test
    fun `채팅방 리스트 가져오기`() {
        val userId = "userId"
        val sort = "date"
        val chatRoom = TestDataFactory.createChatRoom()
        val formatLatestMessageTime =
            chatRoom.latestMessageTime.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))

        every { chatRoomFacade.getChatRooms(userId, ChatRoomSortCriteria.DATE) } returns listOf(chatRoom)

        mockMvc.perform(
            post("/api/chatRoom/list")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId)
                .param("sort", sort),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.chatRooms[0].chatRoomId").value(chatRoom.chatRoomId))
            .andExpect(jsonPath("$.data.chatRooms[0].favorite").value(chatRoom.favorite))
            .andExpect(jsonPath("$.data.chatRooms[0].groupChatRoom").value(chatRoom.groupChatRoom))
            .andExpect(jsonPath("$.data.chatRooms[0].latestMessage").value(chatRoom.latestMessage))
            .andExpect(jsonPath("$.data.chatRooms[0].latestMessageTime").value(formatLatestMessageTime))
            .andExpect(jsonPath("$.data.chatRooms[0].totalUnReadMessage").value(chatRoom.totalUnReadMessage))
            .andExpect(jsonPath("$.data.chatRooms[0].latestPage").value(chatRoom.latestPage))
            .andExpect(jsonPath("$.data.chatRooms[0].latestSeqNumber").value(chatRoom.latestSeqNumber))
            .andExpect(jsonPath("$.data.chatRooms[0].members[0].memberId").value(chatRoom.chatRoomMemberInfos[0].memberId))
            .andExpect(jsonPath("$.data.chatRooms[0].members[0].readSeqNumber").value(chatRoom.chatRoomMemberInfos[0].readSeqNumber))
            .andExpect(jsonPath("$.data.chatRooms[0].members[0].owned").value(chatRoom.chatRoomMemberInfos[0].isOwned))
    }

    @Test
    fun `채팅방 삭제`() {
        val chatRoomIds = listOf("chatRoomId")

        val requestBody = ChatRoomRequest.Delete(
            chatRoomIds = chatRoomIds,
        )

        val userId = "userId"

        every { roomService.deleteChatRoom(any(), any()) } just Runs

        val result = mockMvc.perform(
            post("/api/chatRoom/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId)
                .content(jsonBody(requestBody)),
        )
        performCommonSuccessResponse(result)
    }

    @Test
    fun `그룹 채팅방 삭제`() {
        val chatRoomIds = listOf("chatRoomId")

        val requestBody = ChatRoomRequest.Delete(
            chatRoomIds = chatRoomIds,
        )

        val userId = "userId"

        every { chatRoomFacade.leavesChatRoom(any(), any()) } just Runs

        val result = mockMvc.perform(
            post("/api/chatRoom/delete/group")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId)
                .content(jsonBody(requestBody)),
        )
        performCommonSuccessResponse(result)
    }

    @Test
    fun `채팅방 생성`() {
        val userId = "userId"
        val friendId = "friendId"

        val requestBody = ChatRoomRequest.Create(
            friendId = friendId,
        )

        every { roomService.createChatRoom(userId, friendId) } returns "chatRoomId"

        mockMvc.perform(
            post("/api/chatRoom/create")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId)
                .content(jsonBody(requestBody)),
        ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.status").value(201))
            .andExpect(jsonPath("$.data.chatRoomId").value("chatRoomId"))
    }

    @Test
    fun `그룹 채팅방 생성`() {
        val userId = "userId"
        val friendIds = listOf("friendId")

        val requestBody = ChatRoomRequest.CreateGroup(
            friendIds = friendIds,
        )

        every { chatRoomFacade.createGroupChatRoom(any(), any()) } returns "chatRoomId"

        mockMvc.perform(
            post("/api/chatRoom/create/group")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId)
                .content(jsonBody(requestBody)),
        ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.status").value(201))
            .andExpect(jsonPath("$.data.chatRoomId").value("chatRoomId"))
    }

    @Test
    fun `채팅방 초대`() {
        val userId = "userId"
        val chatRoomId = "chatRoomId"
        val friendId = "friendId"

        val requestBody = ChatRoomRequest.Invite(
            chatRoomId = chatRoomId,
            friendId = friendId,
        )

        every { chatRoomFacade.inviteChatRoom(any(), any(), any()) } just Runs

        val result = mockMvc.perform(
            post("/api/chatRoom/invite")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId)
                .content(jsonBody(requestBody)),
        )
        performCommonSuccessResponse(result)
    }

    @Test
    fun `즐겨찾기 변경`() {
        val userId = "userId"
        val chatRoomId = "chatRoomId"
        val favorite = true

        val requestBody = ChatRoomRequest.Favorite(
            chatRoomId = chatRoomId,
            favorite = favorite,
        )

        every { roomService.favoriteChatRoom(userId, chatRoomId, favorite) } just Runs

        val result = mockMvc.perform(
            post("/api/chatRoom/favorite")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId)
                .content(jsonBody(requestBody)),
        )
        performCommonSuccessResponse(result)
    }
}
