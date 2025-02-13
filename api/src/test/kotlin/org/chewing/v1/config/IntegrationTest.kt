package org.chewing.v1.config

import com.ninjasquad.springmockk.MockkBean
import org.chewing.v1.facade.AccountFacade
import org.chewing.v1.facade.DirectChatFacade
import org.chewing.v1.facade.GroupChatFacade
import org.chewing.v1.service.auth.AuthService
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(WebSocketConfig::class, WebConfig::class, SecurityConfig::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
abstract class IntegrationTest {
    @MockkBean
    protected lateinit var directChatFacade: DirectChatFacade

    @MockkBean
    protected lateinit var groupChatFacade: GroupChatFacade

    @MockkBean
    protected lateinit var authService: AuthService

    @MockkBean
    protected lateinit var accountFacade: AccountFacade
}
