package org.chewing.v1.controller

import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.controller.emoticon.EmoticonController
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.emoticon.EmoticonService
import org.chewing.v1.util.handler.GlobalExceptionHandler
import org.chewing.v1.util.security.UserArgumentResolver
import org.junit.jupiter.api.BeforeEach
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class EmoticonControllerTest : RestDocsTest() {
    private lateinit var emoticonService: EmoticonService
    private lateinit var emoticonController: EmoticonController
    private lateinit var exceptionHandler: GlobalExceptionHandler
    private lateinit var userArgumentResolver: UserArgumentResolver

    @BeforeEach
    fun setUp() {
        emoticonService = mockk()
        exceptionHandler = GlobalExceptionHandler()
        userArgumentResolver = UserArgumentResolver()
        emoticonController = EmoticonController(emoticonService)
        mockMvc = mockController(emoticonController, exceptionHandler, userArgumentResolver)
        val userId = UserId.of("testUserId")
        val authentication = UsernamePasswordAuthenticationToken(userId, null)
        SecurityContextHolder.getContext().authentication = authentication
    }

//    @Test
//    fun `소유 하고 있는 이모티콘 팩 목록 가져오기`() {
//        val userId = "userId"
//        val emoticonId = "emoticonId"
//        val emoticonPackId = "emoticonPackId"
//        val emoticon = TestDataFactory.createEmoticon(emoticonId)
//        val emoticonPack = TestDataFactory.createEmoticonPack(emoticonPackId, listOf(emoticon))
//
//        every { emoticonService.fetchUserEmoticonPacks(userId) } returns listOf(emoticonPack)
//
//        mockMvc.perform(
//            MockMvcRequestBuilders.get("/api/emoticon/list")
//                .contentType(MediaType.APPLICATION_JSON)
//                .requestAttr("userId", userId),
//
//        ).andExpect(status().isOk)
//            .andExpect(jsonPath("$.status").value(200))
//            .andExpect(jsonPath("$.data.emoticonPacks[0].emoticonPackId").value(emoticonPackId))
//            .andExpect(jsonPath("$.data.emoticonPacks[0].fileUrl").value(emoticonPack.media.url))
//            .andExpect(jsonPath("$.data.emoticonPacks[0].fileType").value("image/png"))
//            .andExpect(jsonPath("$.data.emoticonPacks[0].emoticons[0].emoticonId").value(emoticonId))
//            .andExpect(jsonPath("$.data.emoticonPacks[0].emoticons[0].name").value(emoticon.name))
//            .andExpect(jsonPath("$.data.emoticonPacks[0].emoticons[0].fileUrl").value(emoticon.media.url))
//            .andExpect(jsonPath("$.data.emoticonPacks[0].emoticons[0].fileType").value("image/png"))
//            .andReturn()
//    }
}
