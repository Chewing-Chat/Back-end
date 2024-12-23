package org.chewing.v1.controller

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.chewing.v1.RestDocsTest
import org.chewing.v1.TestDataFactory.createJwtToken
import org.chewing.v1.TestDataFactory.createUser
import org.chewing.v1.controller.auth.AuthController
import org.chewing.v1.dto.request.auth.LoginRequest
import org.chewing.v1.dto.request.auth.VerificationCheckRequest
import org.chewing.v1.dto.request.auth.VerificationRequest
import org.chewing.v1.facade.AccountFacade
import org.chewing.v1.model.auth.LoginInfo
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.service.auth.AuthService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ActiveProfiles("test")
class AuthControllerTest : RestDocsTest() {
    private lateinit var authController: AuthController
    private lateinit var authService: AuthService
    private lateinit var accountFacade: AccountFacade

    @BeforeEach
    fun setUp() {
        authService = mockk()
        accountFacade = mockk()
        authController = AuthController(authService, accountFacade)
        mockMvc = mockController(authController)
    }

    @Test
    @DisplayName("휴대폰 인증번호 전송")
    fun sendPhoneVerification() {
        val requestBody = VerificationRequest.Phone(
            countryCode = "82",
            phoneNumber = "010-1234-5678",
        )

        every { authService.createCredential(any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/phone/create/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),
        )
        performCommonSuccessResponse(result)
        verify { authService.createCredential(any()) }
    }

    @Test
    @DisplayName("이메일 인증번호 전송")
    fun sendEmailVerification() {
        val requestBody = VerificationRequest.Email(
            email = "test@Example.com",
        )
        every { authService.createCredential(any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/email/create/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),
        )
        performCommonSuccessResponse(result)
        verify { authService.createCredential(any()) }
    }

    @Test
    @DisplayName("휴대폰 인증번호 확인")
    fun verifyPhone() {
        val jwtToken = createJwtToken()
        val user = createUser()
        val loginInfo = LoginInfo.of(jwtToken, user)

        val requestBody = LoginRequest.Phone(
            phoneNumber = "010-1234-5678",
            countryCode = "82",
            verificationCode = "123",
            deviceId = "testDeviceId",
            provider = "ios",
            appToken = "testToken",
        )

        every { accountFacade.loginAndCreateUser(any(), any(), any(), any()) } returns loginInfo
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/phone/create/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),

        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.token.accessToken").value(jwtToken.accessToken))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.token.refreshToken").value(jwtToken.refreshToken.token))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.access").value(AccessStatus.ACCESS.toString().lowercase()),
            )

        verify { accountFacade.loginAndCreateUser(any(), any(), any(), any()) }
    }

    @Test
    @DisplayName("이메일 인증번호 확인")
    fun verifyEmail() {
        val jwtToken = createJwtToken()
        val user = createUser()
        val loginInfo = LoginInfo.of(jwtToken, user)
        val requestBody = LoginRequest.Email(
            email = "test@example.com",
            verificationCode = "123456",
            deviceId = "testDeviceId",
            provider = "android",
            appToken = "testToken",
        )
        every { accountFacade.loginAndCreateUser(any(), any(), any(), any()) } returns loginInfo
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/email/create/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.token.accessToken").value(jwtToken.accessToken))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.token.refreshToken").value(jwtToken.refreshToken.token))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.access").value(AccessStatus.ACCESS.toString().lowercase()),
            )
        verify { accountFacade.loginAndCreateUser(any(), any(), any(), any()) }
    }

    @Test
    @DisplayName("휴대폰 변경을 위한 인증번호 전송")
    fun sendPhoneVerificationForUpdate() {
        val requestBody = VerificationRequest.Phone(
            countryCode = "82",
            phoneNumber = "010-1234-5678",
        )

        every { authService.createCredentialNotUsed(any(), any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/phone/update/send")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", "testUserId")
                .content(jsonBody(requestBody)),
        )
        performCommonSuccessResponse(result)
    }

    @Test
    @DisplayName("이메일 변경을 위한 인증번호 전송")
    fun sendEmailVerificationForUpdate() {
        val requestBody = VerificationRequest.Email(
            email = "test@example.com",
        )
        every { authService.createCredentialNotUsed(any(), any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/email/update/send")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", "testUserId")
                .content(jsonBody(requestBody)),
        )
        performCommonSuccessResponse(result)
    }

    @Test
    @DisplayName("이메일 변경을 위한 인증번호 확인")
    fun verifyEmailForUpdate() {
        val requestBody = VerificationCheckRequest.Email(
            email = "test@example.com",
            verificationCode = "123456",
        )
        every { accountFacade.changeCredential(any(), any(), any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/email/update/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", "testUserId")
                .content(jsonBody(requestBody)),
        )
        performCommonSuccessResponse(result)
    }

    @Test
    @DisplayName("휴대폰 변경을 위한 인증번호 확인")
    fun verifyPhoneForUpdate() {
        every { accountFacade.changeCredential(any(), any(), any()) } just Runs

        val requestBody = VerificationCheckRequest.Phone(
            phoneNumber = "010-1234-5678",
            countryCode = "82",
            verificationCode = "123456",
        )
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/phone/update/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", "testUserId")
                .content(jsonBody(requestBody)),
        )
        performCommonSuccessResponse(result)
    }

    @Test
    @DisplayName("로그아웃")
    fun logout() {
        every { authService.logout(any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token"),
        )
        performCommonSuccessResponse(result)
    }

    @Test
    @DisplayName("토큰 갱신")
    fun refreshAccessToken() {
        val jwtToken = createJwtToken()
        every { authService.refreshJwtToken(any()) } returns jwtToken
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token"),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken").value(jwtToken.accessToken))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.refreshToken").value(jwtToken.refreshToken.token))
    }
}
