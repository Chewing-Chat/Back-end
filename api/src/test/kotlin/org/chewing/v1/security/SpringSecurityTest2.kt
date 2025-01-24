package org.chewing.v1.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.chewing.v1.TestDataFactory
import org.chewing.v1.config.IntegrationTest
import org.chewing.v1.util.security.JwtTokenUtil
import org.chewing.v1.util.security.JwtAuthenticationEntryPoint
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("test")
class SpringSecurityTest2 : IntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var jwtTokenUtil: JwtTokenUtil

    @Autowired
    private lateinit var jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint

    @Test
    @DisplayName("휴대폰 인증번호 전송 - 인증 없이 통과해야함")
    fun sendPhoneVerification() {
        val requestBody = mapOf(
            "countryCode" to "82",
            "phoneNumber" to "010-1234-5678",
        )
        every { accountFacade.registerCredential(any(), any()) } just Runs
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/create/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)),
        ).andExpect(status().isOk)
    }

    @Test
    @DisplayName("휴대폰 인증번호 확인- 인증 없이 통과해야함")
    fun verifyPhone() {
        val userId = TestDataFactory.createUserId()

        val requestBody = mapOf(
            "countryCode" to "82",
            "phoneNumber" to "010-1234-5678",
            "verificationCode" to "123456",
            "appToken" to "testToken",
            "deviceId" to "testDeviceId",
            "provider" to "IOS",
            "userName" to "testUserName",
        )
        every { accountFacade.createUser(any(), any(), any(), any(), any()) } returns userId
        every { authService.createLoginInfo(any(), any()) } just Runs

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/create/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)),

        )
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("로그아웃 - 인증 없이 통과해야함")
    fun logout() {
        val userId = TestDataFactory.createUserId()
        val jwtToken = jwtTokenUtil.createRefreshToken(userId)
        every { authService.logout(any()) } just Runs
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ${jwtToken.token}"),
        ).andExpect(status().isOk)
    }

    @Test
    @DisplayName("토큰 갱신 - 인증 없이 통과해야함")
    fun refreshAccessToken() {
        val userId = TestDataFactory.createUserId()
        val jwtToken = jwtTokenUtil.createRefreshToken(userId)
        every { authService.updateLoginInfo(any(), any(), any()) } just Runs
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ${jwtToken.token}"),
        )
            .andExpect(status().isOk)
    }
}
