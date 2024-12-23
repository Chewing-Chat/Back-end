package org.chewing.v1.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.chewing.v1.TestDataFactory
import org.chewing.v1.config.IntegrationTest
import org.chewing.v1.implementation.auth.JwtTokenProvider
import org.chewing.v1.model.auth.LoginInfo
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
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    private lateinit var jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint

    @Test
    @DisplayName("휴대폰 인증번호 전송 - 인증 없이 통과해야함")
    fun sendPhoneVerification() {
        val requestBody = mapOf(
            "countryCode" to "82",
            "phoneNumber" to "010-1234-5678",
        )
        every { authService.createCredential(any()) } just Runs
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/phone/create/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)),
        ).andExpect(status().isOk)
    }

    @Test
    @DisplayName("이메일 인증번호 전송 - 인증 없이 통과해야함")
    fun sendEmailVerification() {
        val requestBody = mapOf(
            "email" to "test@Example.com",
        )
        every { authService.createCredential(any()) } just Runs
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/email/create/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)),
        ).andExpect(status().isOk)
    }

    @Test
    @DisplayName("휴대폰 인증번호 확인- 인증 없이 통과해야함")
    fun verifyPhone() {
        val jwtToken = TestDataFactory.createJwtToken()
        val user = TestDataFactory.createUser()

        val requestBody = mapOf(
            "countryCode" to "82",
            "phoneNumber" to "010-1234-5678",
            "verificationCode" to "123456",
            "appToken" to "testToken",
            "deviceId" to "testDeviceId",
            "provider" to "IOS",
        )
        every { accountFacade.loginAndCreateUser(any(), any(), any(), any()) } returns LoginInfo.of(jwtToken, user)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/phone/create/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)),

        )
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("이메일 인증번호 확인 - 인증 없이 통과해야함")
    fun verifyEmail() {
        val jwtToken = TestDataFactory.createJwtToken()
        val user = TestDataFactory.createUser()
        val loginInfo = LoginInfo.of(jwtToken, user)
        val requestBody = mapOf(
            "email" to "test@example.com",
            "verificationCode" to "123456",
            "appToken" to "testToken",
            "deviceId" to "testDeviceId",
            "provider" to "ANDROID",
        )
        every { accountFacade.loginAndCreateUser(any(), any(), any(), any()) } returns loginInfo
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/email/create/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)),
        )
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("로그아웃 - 인증 없이 통과해야함")
    fun logout() {
        every { authService.logout(any()) } just Runs
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token"),
        ).andExpect(status().isOk)
    }

    @Test
    @DisplayName("토큰 갱신 - 인증 없이 통과해야함")
    fun refreshAccessToken() {
        val jwtToken = TestDataFactory.createJwtToken()
        every { authService.refreshJwtToken(any()) } returns jwtToken
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token"),
        )
            .andExpect(status().isOk)
    }
}
