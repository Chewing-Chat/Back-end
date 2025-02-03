package org.chewing.v1.security

import org.chewing.v1.config.IntegrationTest
import org.chewing.v1.model.user.UserId
import org.chewing.v1.util.security.JwtTokenUtil
import org.chewing.v1.util.security.JwtAuthenticationEntryPoint
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("test")
class SpringSecurityTest : IntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jwtTokenUtil: JwtTokenUtil

    @Autowired
    private lateinit var jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint

    @Test
    fun `토큰 없이 들어온다면 에러 발생`() {
        mockMvc.perform(
            get("/api/private")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `토큰이 유효하지 않은 토큰이라면 에러 방생`() {
        mockMvc.perform(
            get("/api/private")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid_token")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `토큰이 유효한 토큰이라면 통과`() {
        val userId = UserId.of("testUserId")
        val token = jwtTokenUtil.createAccessToken(userId)
        mockMvc.perform(
            post("/api/private")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)
    }
}
