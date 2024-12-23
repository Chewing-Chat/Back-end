package org.chewing.v1.controller

import io.mockk.every
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.TestDataFactory.createFriend
import org.chewing.v1.TestDataFactory.createUser
import org.chewing.v1.TestDataFactory.createUserStatus
import org.chewing.v1.controller.main.MainController
import org.chewing.v1.facade.MainFacade
import org.chewing.v1.util.converter.StringToFriendSortCriteriaConverter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ActiveProfiles("test")
class MainControllerTest : RestDocsTest() {

    private lateinit var mainFacade: MainFacade
    private lateinit var mainController: MainController

    @BeforeEach
    fun setUp() {
        mainFacade = mockk()
        mainController = MainController(mainFacade)
        mockMvc = mockControllerWithCustomConverter(
            mainController,
            StringToFriendSortCriteriaConverter(),
        )
    }

    @Test
    @DisplayName("메인페이지 조회")
    fun getMainPage() {
        val user = createUser()
        val friends = listOf(createFriend())
        val status = createUserStatus()
        every { mainFacade.getMainPage(any(), any()) }.returns(
            Triple(
                user,
                status,
                friends,
            ),
        )
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/main")
                .contentType(MediaType.APPLICATION_JSON)
                .param("sort", "name")
                .requestAttr("userId", "testUserId"),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.friends[0].friendId").value(friends[0].user.userId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.friends[0].firstName").value(friends[0].name.firstName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.friends[0].lastName").value(friends[0].name.lastName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.friends[0].imageUrl").value(friends[0].user.image.url))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.friends[0].imageType").value("image/png"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.friends[0].statusMessage").value(friends[0].status.message),
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.friends[0].favorite").value(friends[0].isFavorite.toString()),
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.friends[0].access")
                    .value(friends[0].user.status.name.lowercase()),
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.friends[0].backgroundImageUrl")
                    .value(friends[0].user.backgroundImage.url),
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.friends[0].backgroundImageType").value("image/png"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.friends[0].statusEmoji").value(friends[0].status.emoji))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalFriends").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.user.firstName").value(user.name.firstName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.user.lastName").value(user.name.lastName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.user.imageUrl").value(user.image.url))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.user.imageType").value("image/png"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.user.statusEmoji").value(status.emoji))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.user.statusMessage").value(status.message))
    }
}
