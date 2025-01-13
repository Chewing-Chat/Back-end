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
import org.chewing.v1.dto.request.auth.SignUpRequest
import org.chewing.v1.dto.request.auth.VerificationRequest
import org.chewing.v1.dto.request.auth.VerifyOnlyRequest
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

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
    @DisplayName("생성을 위한 휴대폰 인증번호 전송")
    fun sendPhoneVerification() {
        val requestBody = VerificationRequest.Phone(
            countryCode = "82",
            phoneNumber = "010-1234-5678",
        )

        every { authService.createCredential(any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/create/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),
        )
        performCommonSuccessResponse(result)
        verify { authService.createCredential(any()) }
    }

    @Test
    @DisplayName("비밀번호 초기화를 위한 휴대폰 인증번호 전송")
    fun sendPhoneVerificationReset() {
        val requestBody = VerificationRequest.Phone(
            countryCode = "82",
            phoneNumber = "010-1234-5678",
        )

        every { authService.createCredential(any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/reset/send")
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

        val requestBody = SignUpRequest.Phone(
            phoneNumber = "010-1234-5678",
            countryCode = "82",
            verificationCode = "123",
            deviceId = "testDeviceId",
            provider = "ios",
            appToken = "testToken",
            userName = "testName",
        )

        every { accountFacade.createUser(any(), any(), any(), any(), any()) } returns loginInfo
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/create/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),

        ).andExpect {
            status().isOk
            jsonPath("$.status").value(200)
            jsonPath("$.data.token.accessToken").value(jwtToken.accessToken)
            jsonPath("$.data.token.refreshToken").value(jwtToken.refreshToken.token)
            jsonPath("$.data.access").value(AccessStatus.ACCESS.toString().lowercase())
        }

        verify { accountFacade.createUser(any(), any(), any(), any(), any()) }
    }

    @Test
    @DisplayName("휴대폰 인증번호만 확인")
    fun verifyPhoneOnly() {
        val jwtToken = createJwtToken()
        val user = createUser()
        val loginInfo = LoginInfo.of(jwtToken, user)

        val requestBody = VerifyOnlyRequest(
            phoneNumber = "010-1234-5678",
            countryCode = "82",
            verificationCode = "123",
        )

        every { accountFacade.verifyPhoneOnly(any(), any()) } returns loginInfo
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/reset/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),
        )
            .andExpect {
                status().isOk
                jsonPath("$.status").value(200)
                jsonPath("$.data.token.accessToken").value(jwtToken.accessToken)
                jsonPath("$.data.token.refreshToken").value(jwtToken.refreshToken.token)
                jsonPath("$.data.access").value(AccessStatus.ACCESS.toString().lowercase())
            }

        verify { accountFacade.verifyPhoneOnly(any(), any()) }
    }

    @Test
    @DisplayName("비밀번호 변경")
    fun changePassword() {
        val userId = "testUserId"
        val requestBody = SignUpRequest.Password(
            password = "testPassword",
        )

        every { accountFacade.makePassword(any(), any()) } just Runs
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/change/password")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId)
                .content(jsonBody(requestBody)),
        )
        performCommonSuccessResponse(result)
        verify { accountFacade.makePassword(any(), any()) }
    }

    @Test
    @DisplayName("비밀번호 생성")
    fun makePassword() {
        val userId = "testUserId"
        val requestBody = SignUpRequest.Password(
            password = "testPassword",
        )

        every { accountFacade.makePassword(any(), any()) } just Runs
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/create/password")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId)
                .content(jsonBody(requestBody)),
        )
        performCommonSuccessResponse(result)
        verify { accountFacade.makePassword(any(), any()) }
    }

    @Test
    @DisplayName("비밀번호로 로그인")
    fun login() {
        val jwtToken = createJwtToken()
        val user = createUser()
        val loginInfo = LoginInfo.of(jwtToken, user)

        val requestBody = LoginRequest(
            password = "testPassword",
            countryCode = "82",
            phoneNumber = "010-1234-5678",
            deviceId = "testDeviceId",
            provider = "ios",
            appToken = "testToken",
        )

        every { accountFacade.login(any(), any(), any(), any()) } returns loginInfo
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),
        )
            .andExpect {
                status().isOk
                jsonPath("$.status").value(200)
                jsonPath("$.data.token.accessToken").value(jwtToken.accessToken)
                jsonPath("$.data.token.refreshToken").value(jwtToken.refreshToken.token)
                jsonPath("$.data.access").value(AccessStatus.ACCESS.toString().lowercase())
            }

        verify { accountFacade.login(any(), any(), any(), any()) }
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
            .andExpect {
                status().isOk
                jsonPath("$.status").value(200)
                jsonPath("$.data.accessToken").value(jwtToken.accessToken)
                jsonPath("$.data.refreshToken").value(jwtToken.refreshToken.token)
            }
    }
}
