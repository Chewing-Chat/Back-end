package org.chewing.v1.controller

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.chewing.v1.RestDocsTest
import org.chewing.v1.RestDocsUtils.requestAccessTokenFields
import org.chewing.v1.RestDocsUtils.requestPreprocessor
import org.chewing.v1.RestDocsUtils.requestRefreshTokenFields
import org.chewing.v1.RestDocsUtils.responseErrorFields
import org.chewing.v1.RestDocsUtils.responsePreprocessor
import org.chewing.v1.RestDocsUtils.responseSuccessFields
import org.chewing.v1.TestDataFactory.createJwtToken
import org.chewing.v1.TestDataFactory.createUser
import org.chewing.v1.controller.auth.AuthController
import org.chewing.v1.dto.request.auth.LoginRequest
import org.chewing.v1.dto.request.auth.SignUpRequest
import org.chewing.v1.dto.request.auth.VerificationRequest
import org.chewing.v1.dto.request.auth.VerifyOnlyRequest
import org.chewing.v1.error.AuthorizationException
import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.facade.AccountFacade
import org.chewing.v1.model.auth.CredentialTarget
import org.chewing.v1.model.auth.LoginInfo
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.service.auth.AuthService
import org.chewing.v1.util.handler.GlobalExceptionHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("test")
class AuthControllerTest : RestDocsTest() {
    private lateinit var authController: AuthController
    private lateinit var authService: AuthService
    private lateinit var accountFacade: AccountFacade
    private lateinit var exceptionHandler: GlobalExceptionHandler

    @BeforeEach
    fun setUp() {
        authService = mockk()
        accountFacade = mockk()
        exceptionHandler = GlobalExceptionHandler()
        authController = AuthController(authService, accountFacade)
        mockMvc = mockController(authController, exceptionHandler)
    }

    @Test
    @DisplayName("생성을 위한 휴대폰 인증번호 전송")
    fun sendPhoneVerification() {
        val requestBody = VerificationRequest.Phone(
            countryCode = "82",
            phoneNumber = "01012345678",
        )

        every { accountFacade.registerCredential(any(), CredentialTarget.SIGN_UP) } just Runs

        val result = mockMvc.perform(
            post("/api/auth/create/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),
        ).andDo(
            document(
                "{class-name}/{method-name}",
                requestPreprocessor(),
                responsePreprocessor(),
                requestFields(
                    fieldWithPath("countryCode").description("국가 코드)"),
                    fieldWithPath("phoneNumber").description("휴대폰 번호"),
                ),
                responseSuccessFields(),
            ),
        )

        performCommonSuccessResponse(result)
        verify { accountFacade.registerCredential(any(), CredentialTarget.SIGN_UP) }
    }

    @Test
    @DisplayName("생성을 위한 휴대폰 인증번호 전송 실패 - 이미 생성된 계정")
    fun sendPhoneVerificationAlreadyCreated() {
        val requestBody = VerificationRequest.Phone(
            countryCode = "82",
            phoneNumber = "01012345678",
        )

        every {
            accountFacade.registerCredential(
                any(),
                CredentialTarget.SIGN_UP,
            )
        } throws ConflictException(ErrorCode.USER_ALREADY_CREATED)

        val result = mockMvc.perform(
            post("/api/auth/create/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),
        ).andDo(
            document(
                "{class-name}/{method-name}",
                requestPreprocessor(),
                responsePreprocessor(),
                responseErrorFields(HttpStatus.CONFLICT, ErrorCode.USER_ALREADY_CREATED, "계정이 이미 생성되었음으로 로그인으로 유도 해야함"),
            ),
        )

        performErrorResponse(result, HttpStatus.CONFLICT, ErrorCode.USER_ALREADY_CREATED)
    }

    @Test
    @DisplayName("비밀번호 초기화를 위한 휴대폰 인증번호 전송")
    fun sendPhoneVerificationReset() {
        val requestBody = VerificationRequest.Phone(
            countryCode = "82",
            phoneNumber = "01012345678",
        )

        every { accountFacade.registerCredential(any(), CredentialTarget.RESET) } just Runs

        val result = mockMvc.perform(
            post("/api/auth/reset/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),
        )
            .andDo(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestFields(
                        fieldWithPath("countryCode").description("국가 코드)"),
                        fieldWithPath("phoneNumber").description("휴대폰 번호"),
                    ),
                    responseSuccessFields(),
                ),
            )
        performCommonSuccessResponse(result)
        verify { accountFacade.registerCredential(any(), CredentialTarget.RESET) }
    }

    @Test
    @DisplayName("비밀번호 초기화를 위한 휴대폰 인증번호 전송 실패 - 생성되지 않은 계정")
    fun sendPhoneVerificationResetNotCreated() {
        val requestBody = VerificationRequest.Phone(
            countryCode = "82",
            phoneNumber = "01012345678",
        )

        every {
            accountFacade.registerCredential(
                any(),
                CredentialTarget.RESET,
            )
        } throws ConflictException(ErrorCode.USER_NOT_CREATED)

        val result = mockMvc.perform(
            post("/api/auth/reset/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),
        )
            .andDo(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.CONFLICT,
                        ErrorCode.USER_NOT_CREATED,
                        "비밀번호 초기화를 하려고 했으나 계정이 생성되지 않았음으로 가입을 유도 해야함",
                    ),
                ),
            )
        performErrorResponse(result, HttpStatus.CONFLICT, ErrorCode.USER_NOT_CREATED)
        verify { accountFacade.registerCredential(any(), CredentialTarget.RESET) }
    }

    @Test
    @DisplayName("휴대폰 인증번호 확인")
    fun signUp() {
        val jwtToken = createJwtToken()
        val user = createUser(AccessStatus.NEED_CREATE_PASSWORD)
        val loginInfo = LoginInfo.of(jwtToken, user)

        val requestBody = SignUpRequest.Phone(
            phoneNumber = "01012345678",
            countryCode = "82",
            verificationCode = "123",
            deviceId = "testDeviceId",
            provider = "ios",
            appToken = "testToken",
            userName = "testName",
        )

        every { accountFacade.createUser(any(), any(), any(), any(), any()) } returns loginInfo

        mockMvc.perform(
            post("/api/auth/create/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),

        )
            .andDo(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestFields(
                        fieldWithPath("phoneNumber").description("휴대폰 번호"),
                        fieldWithPath("countryCode").description("국가 코드"),
                        fieldWithPath("verificationCode").description("인증번호"),
                        fieldWithPath("deviceId").description("디바이스 아이디(디바이스 식별을 위한 정보)"),
                        fieldWithPath("provider").description("플랫폼(ios, android)"),
                        fieldWithPath("appToken").description("앱 토큰(푸시 토큰)"),
                        fieldWithPath("userName").description("사용자 이름"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),
                        fieldWithPath("data.token.accessToken").description("액세스 토큰"),
                        fieldWithPath("data.token.refreshToken").description("리프레시 토큰"),
                        fieldWithPath("data.access").description(
                            "액세스 상태로 로그인이 가능한지 여부 - need_create_password 로 반환됨 (정상) 다른거 (비정상)- 비밀번호 설정으로 진행",
                        ),
                    ),
                ),
            )
            .andExpect {
                status().isOk
                jsonPath("$.status").value(200)
                jsonPath("$.data.token.accessToken").value(jwtToken.accessToken)
                jsonPath("$.data.token.refreshToken").value(jwtToken.refreshToken.token)
                jsonPath("$.data.access").value(AccessStatus.NEED_CREATE_PASSWORD.toString().lowercase())
            }

        verify { accountFacade.createUser(any(), any(), any(), any(), any()) }
    }

    @Test
    @DisplayName("휴대폰 인증번호 확인 실패 - 잘못된 인증번호")
    fun signUpWrongVerificationCode() {
        val requestBody = SignUpRequest.Phone(
            phoneNumber = "01012345678",
            countryCode = "82",
            verificationCode = "123",
            deviceId = "testDeviceId",
            provider = "ios",
            appToken = "testToken",
            userName = "testName",
        )

        every { accountFacade.createUser(any(), any(), any(), any(), any()) } throws AuthorizationException(ErrorCode.WRONG_VERIFICATION_CODE)

        val result = mockMvc.perform(
            post("/api/auth/create/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),

        )
            .andDo(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.UNAUTHORIZED,
                        ErrorCode.WRONG_VERIFICATION_CODE,
                        "휴대폰 인증번호가 잘못되었음 - 재인증 요청으로 유도해야함",
                    ),
                ),
            )
        performErrorResponse(result, HttpStatus.UNAUTHORIZED, ErrorCode.WRONG_VERIFICATION_CODE)

        verify { accountFacade.createUser(any(), any(), any(), any(), any()) }
    }

    @Test
    @DisplayName("휴대폰 인증번호 확인 실패 - 잘못된 인증번호")
    fun signUpExpiredVerificationCode() {
        val requestBody = SignUpRequest.Phone(
            phoneNumber = "01012345678",
            countryCode = "82",
            verificationCode = "123",
            deviceId = "testDeviceId",
            provider = "ios",
            appToken = "testToken",
            userName = "testName",
        )

        every { accountFacade.createUser(any(), any(), any(), any(), any()) } throws AuthorizationException(ErrorCode.EXPIRED_VERIFICATION_CODE)

        val result = mockMvc.perform(
            post("/api/auth/create/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),

        )
            .andDo(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.UNAUTHORIZED,
                        ErrorCode.EXPIRED_VERIFICATION_CODE,
                        "휴대폰 인증번호가 만료됨 - 재인증 요청으로 유도해야함",
                    ),
                ),
            )
        performErrorResponse(result, HttpStatus.UNAUTHORIZED, ErrorCode.EXPIRED_VERIFICATION_CODE)

        verify { accountFacade.createUser(any(), any(), any(), any(), any()) }
    }

    @Test
    @DisplayName("휴대폰 인증번호 확인 실패 - 이미 생성된 계정")
    fun signUpAlreadyCreated() {
        val requestBody = SignUpRequest.Phone(
            phoneNumber = "01012345678",
            countryCode = "82",
            verificationCode = "123",
            deviceId = "testDeviceId",
            provider = "ios",
            appToken = "testToken",
            userName = "testName",
        )

        every { accountFacade.createUser(any(), any(), any(), any(), any()) } throws ConflictException(ErrorCode.USER_ALREADY_CREATED)

        val result = mockMvc.perform(
            post("/api/auth/create/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),

        )
            .andDo(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.CONFLICT,
                        ErrorCode.USER_ALREADY_CREATED,
                        "계정이 이미 생성되었음으로 로그인으로 유도 해야함. 다만, 전화번호 인증 요청시 이미 검증이 되었는데 발생한 잘못된 접근 임.",
                    ),
                ),
            )
        performErrorResponse(result, HttpStatus.CONFLICT, ErrorCode.USER_ALREADY_CREATED)

        verify { accountFacade.createUser(any(), any(), any(), any(), any()) }
    }

    @Test
    @DisplayName("비밀번호 초기화를 위한 휴대폰 인증번호 확인")
    fun resetCredential() {
        val jwtToken = createJwtToken()
        val user = createUser(AccessStatus.ACCESS)
        val loginInfo = LoginInfo.of(jwtToken, user)

        val requestBody = VerifyOnlyRequest(
            phoneNumber = "01012345678",
            countryCode = "82",
            verificationCode = "123",
        )

        every { accountFacade.resetCredential(any(), any()) } returns loginInfo
        mockMvc.perform(
            post("/api/auth/reset/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),
        )
            .andDo(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestFields(
                        fieldWithPath("phoneNumber").description("휴대폰 번호"),
                        fieldWithPath("countryCode").description("국가 코드"),
                        fieldWithPath("verificationCode").description("인증번호"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),
                        fieldWithPath("data.token.accessToken").description("액세스 토큰"),
                        fieldWithPath("data.token.refreshToken").description("리프레시 토큰"),
                        fieldWithPath("data.access").description(
                            "액세스 상태로 로그인이 가능한지 여부 - access (정상) 다른거 (비정상)- 비밀번호 설정으로 진행",
                        ),
                    ),
                ),
            )
            .andExpect {
                status().isOk
                jsonPath("$.status").value(200)
                jsonPath("$.data.token.accessToken").value(jwtToken.accessToken)
                jsonPath("$.data.token.refreshToken").value(jwtToken.refreshToken.token)
                jsonPath("$.data.access").value(AccessStatus.ACCESS.toString().lowercase())
            }

        verify { accountFacade.resetCredential(any(), any()) }
    }

    @Test
    @DisplayName("비밀번호 초기화를 위한 휴대폰 인증번호 확인 실패 - 잘못된 인증번호")
    fun resetCredentialWrongVerificationCode() {
        val requestBody = VerifyOnlyRequest(
            phoneNumber = "01012345678",
            countryCode = "82",
            verificationCode = "123",
        )

        every { accountFacade.resetCredential(any(), any()) } throws AuthorizationException(ErrorCode.WRONG_VERIFICATION_CODE)

        val result = mockMvc.perform(
            post("/api/auth/reset/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),

        )
            .andDo(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.UNAUTHORIZED,
                        ErrorCode.WRONG_VERIFICATION_CODE,
                        "휴대폰 인증번호가 잘못되었음 - 재인증 요청으로 유도해야함",
                    ),
                ),
            )
        performErrorResponse(result, HttpStatus.UNAUTHORIZED, ErrorCode.WRONG_VERIFICATION_CODE)
    }

    @Test
    @DisplayName("비밀번호 초기화를 위한 휴대폰 인증번호 확인 실패 - 존재하지 않는 계정")
    fun resetCredentialUserNotFound() {
        val requestBody = VerifyOnlyRequest(
            phoneNumber = "01012345678",
            countryCode = "82",
            verificationCode = "123",
        )

        every { accountFacade.resetCredential(any(), any()) } throws NotFoundException(ErrorCode.USER_NOT_FOUND)

        val result = mockMvc.perform(
            post("/api/auth/reset/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),
        )
            .andDo(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.USER_NOT_FOUND,
                        "계정이 존재하지 않음으로 회원가입을 유도해야함 - 비밀번호 초기화시 휴대폰 인증 할때 이미 존재 유무를 검증하는데 이때 오류 발생시 잘못된 접근",
                    ),
                ),
            )
        performErrorResponse(result, HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND)

        verify { accountFacade.resetCredential(any(), any()) }
    }

    @Test
    @DisplayName("비밀번호 초기화를 위한 휴대폰 인증번호 확인 실패 - 인증 만료")
    fun resetCredentialExpiredVerificationCode() {
        val requestBody = VerifyOnlyRequest(
            phoneNumber = "01012345678",
            countryCode = "82",
            verificationCode = "123",
        )

        every { accountFacade.resetCredential(any(), any()) } throws AuthorizationException(ErrorCode.EXPIRED_VERIFICATION_CODE)

        val result = mockMvc.perform(
            post("/api/auth/reset/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),

        )
            .andDo(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.UNAUTHORIZED,
                        ErrorCode.EXPIRED_VERIFICATION_CODE,
                        "휴대폰 인증번호가 만료됨 - 재인증 요청으로 유도해야함",
                    ),
                ),
            )
        performErrorResponse(result, HttpStatus.UNAUTHORIZED, ErrorCode.EXPIRED_VERIFICATION_CODE)
    }

    @Test
    @DisplayName("비밀번호 변경")
    fun changePassword() {
        val userId = "testUserId"
        val requestBody = SignUpRequest.Password(
            password = "testPassword",
        )

        every { accountFacade.changePassword(any(), any()) } just Runs
        val result = mockMvc.perform(
            post("/api/auth/change/password")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId)
                .content(jsonBody(requestBody))
                .header("Authorization", "Bearer access-token"),
        )
            .andDo(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    requestFields(
                        fieldWithPath("password").description("변경할 비밀번호"),
                    ),
                    responseSuccessFields(),
                ),
            )
        performCommonSuccessResponse(result)
        verify { accountFacade.changePassword(any(), any()) }
    }

    @Test
    @DisplayName("비밀번호 생성")
    fun createPassword() {
        val userId = "testUserId"
        val requestBody = SignUpRequest.Password(
            password = "testPassword",
        )

        every { accountFacade.changePassword(any(), any()) } just Runs
        val result = mockMvc.perform(
            post("/api/auth/create/password")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId)
                .content(jsonBody(requestBody))
                .header("Authorization", "Bearer access-token"),
        )
            .andDo(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    requestFields(
                        fieldWithPath("password").description("생성할 비밀번호"),
                    ),
                    responseSuccessFields(),
                ),
            )
        performCommonSuccessCreateResponse(result)
        verify { accountFacade.changePassword(any(), any()) }
    }

    @Test
    @DisplayName("로그인")
    fun login() {
        val jwtToken = createJwtToken()
        val user = createUser(AccessStatus.ACCESS)
        val loginInfo = LoginInfo.of(jwtToken, user)

        val requestBody = LoginRequest(
            password = "testPassword",
            countryCode = "82",
            phoneNumber = "01012345678",
            deviceId = "testDeviceId",
            provider = "ios",
            appToken = "testToken",
        )

        every { accountFacade.login(any(), any(), any(), any()) } returns loginInfo
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),
        )
            .andDo(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestFields(
                        fieldWithPath("phoneNumber").description("휴대폰 번호"),
                        fieldWithPath("countryCode").description("국가 코드"),
                        fieldWithPath("password").description("비밀번호"),
                        fieldWithPath("deviceId").description("디바이스 아이디(디바이스 식별을 위한 정보)"),
                        fieldWithPath("provider").description("플랫폼(ios, android)"),
                        fieldWithPath("appToken").description("앱 토큰(푸시 토큰)"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),
                        fieldWithPath("data.token.accessToken").description("액세스 토큰"),
                        fieldWithPath("data.token.refreshToken").description("리프레시 토큰"),
                        fieldWithPath("data.access").description(
                            "액세스 상태로 로그인이 가능한지 여부 - access (정상) need_create_password(비정상)- 비밀번호 설정으로 진행",
                        ),
                    ),
                ),
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
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    fun loginWrongPassword() {
        val requestBody = LoginRequest(
            password = "testPassword",
            countryCode = "82",
            phoneNumber = "01012345678",
            deviceId = "testDeviceId",
            provider = "ios",
            appToken = "testToken",
        )

        every { accountFacade.login(any(), any(), any(), any()) } throws AuthorizationException(ErrorCode.WRONG_PASSWORD)
        val result = mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),
        )
            .andDo(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.UNAUTHORIZED,
                        ErrorCode.WRONG_PASSWORD,
                        "비밀번호가 잘못되었음 - 재입력 요청으로 유도해야함",
                    ),
                ),
            )

        performErrorResponse(result, HttpStatus.UNAUTHORIZED, ErrorCode.WRONG_PASSWORD)

        verify { accountFacade.login(any(), any(), any(), any()) }
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 계정")
    fun loginNotFoundUser() {
        val requestBody = LoginRequest(
            password = "testPassword",
            countryCode = "82",
            phoneNumber = "01012345678",
            deviceId = "testDeviceId",
            provider = "ios",
            appToken = "testToken",
        )

        every { accountFacade.login(any(), any(), any(), any()) } throws NotFoundException(ErrorCode.USER_NOT_FOUND)
        val result = mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),
        )
            .andDo(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.USER_NOT_FOUND,
                        "계정이 존재하지 않음으로 회원가입을 유도해야함 - 로그인시 휴대폰 인증 할때 이미 존재 유무를 검증하는데 이때 오류 발생시 잘못된 접근",
                    ),
                ),
            )

        performErrorResponse(result, HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND)

        verify { accountFacade.login(any(), any(), any(), any()) }
    }

    @Test
    @DisplayName("로그아웃")
    fun logout() {
        every { authService.logout(any()) } just Runs

        val result = mockMvc.perform(
            delete("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer refresh-token"),
        )
            .andDo(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestRefreshTokenFields(),
                    responseSuccessFields(),
                ),
            )
        performCommonSuccessResponse(result)
    }

    @Test
    @DisplayName("토큰 갱신")
    fun refreshAccessToken() {
        val jwtToken = createJwtToken()
        every { authService.refreshJwtToken(any()) } returns jwtToken
        mockMvc.perform(
            get("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token"),
        )
            .andDo(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestHeaders(
                        headerWithName("Authorization").description("리프레시 토큰"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),
                        fieldWithPath("data.accessToken").description("액세스 토큰"),
                        fieldWithPath("data.refreshToken").description("리프레시 토큰"),
                    ),
                ),
            )
            .andExpect {
                status().isOk
                jsonPath("$.status").value(200)
                jsonPath("$.data.accessToken").value(jwtToken.accessToken)
                jsonPath("$.data.refreshToken").value(jwtToken.refreshToken.token)
            }
    }
}
