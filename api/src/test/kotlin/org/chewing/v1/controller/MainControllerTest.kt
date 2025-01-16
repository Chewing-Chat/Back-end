package org.chewing.v1.controller

import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.controller.main.MainController
import org.chewing.v1.facade.MainFacade
import org.chewing.v1.util.converter.StringToFriendSortCriteriaConverter
import org.chewing.v1.util.handler.GlobalExceptionHandler
import org.junit.jupiter.api.BeforeEach
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class MainControllerTest : RestDocsTest() {

    private lateinit var mainFacade: MainFacade
    private lateinit var mainController: MainController

    @BeforeEach
    fun setUp() {
        mainFacade = mockk()
        mainController = MainController(mainFacade)
        mockMvc = mockControllerWithAdviceAndCustomConverter(
            mainController,
            GlobalExceptionHandler(),
            StringToFriendSortCriteriaConverter(),
        )
    }
//
//    @Test
//    @DisplayName("메인페이지 조회")
//    fun getMainPage() {
//        val user = createUser(AccessStatus.ACCESS)
//        val friends = listOf(createFriend())
//
//        every { mainFacade.getMainPage(any(), any()) }.returns(
//            Pair(
//                user,
//                friends,
//            ),
//        )
//
//        mockMvc.perform(
//            MockMvcRequestBuilders.get("/api/main")
//                .contentType(MediaType.APPLICATION_JSON)
//                .param("sort", "name")
//                .requestAttr("userId", "testUserId"),
//        )
//            .andExpect(MockMvcResultMatchers.status().isOk)
//            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.data.friends[0].friendId").value(friends[0].user.userId))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.data.friends[0].name").value(friends[0].name))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.data.friends[0].imageUrl").value(friends[0].user.image.url))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.data.friends[0].imageType").value("image/png"))
//            .andExpect(
//                MockMvcResultMatchers.jsonPath("$.data.friends[0].statusMessage").value(friends[0].user.statusMessage),
//            )
//            .andExpect(
//                MockMvcResultMatchers.jsonPath("$.data.friends[0].favorite").value(friends[0].isFavorite.toString()),
//            )
//            .andExpect(
//                MockMvcResultMatchers.jsonPath("$.data.friends[0].access")
//                    .value(friends[0].user.status.name.lowercase()),
//            )
//            .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalFriends").value(1))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.data.user.name").value(user.name))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.data.user.imageUrl").value(user.image.url))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.data.user.imageType").value("image/png"))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.data.user.statusMessage").value(user.statusMessage))
//    }
}
