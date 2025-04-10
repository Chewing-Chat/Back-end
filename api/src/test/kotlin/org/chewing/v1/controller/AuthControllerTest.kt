package org.chewing.v1.controller

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.chewing.v1.RestDocsTest
import org.chewing.v1.RestDocsUtils.requestAccessTokenFields
import org.chewing.v1.RestDocsUtils.requestPreprocessor
import org.chewing.v1.RestDocsUtils.responseErrorFields
import org.chewing.v1.RestDocsUtils.responsePreprocessor
import org.chewing.v1.RestDocsUtils.responseSuccessFields
import org.chewing.v1.TestDataFactory.createJwtToken
import org.chewing.v1.TestDataFactory.createUserId
import org.chewing.v1.controller.auth.AuthController
import org.chewing.v1.dto.request.auth.LoginRequest
import org.chewing.v1.dto.request.auth.LogoutRequest
import org.chewing.v1.dto.request.auth.SignUpRequest
import org.chewing.v1.dto.request.auth.VerificationRequest
import org.chewing.v1.dto.request.auth.VerifyOnlyRequest
import org.chewing.v1.error.AuthorizationException
import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.facade.AccountFacade
import org.chewing.v1.model.auth.CredentialTarget
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.auth.AuthService
import org.chewing.v1.util.handler.GlobalExceptionHandler
import org.chewing.v1.util.security.JwtTokenUtil
import org.chewing.v1.util.security.UserArgumentResolver
import org.hamcrest.CoreMatchers.equalTo
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class AuthControllerTest : RestDocsTest() {
    private lateinit var authController: AuthController
    private lateinit var authService: AuthService
    private lateinit var accountFacade: AccountFacade
    private lateinit var exceptionHandler: GlobalExceptionHandler
    private lateinit var jwtTokenUtil: JwtTokenUtil
    private lateinit var userArgumentResolver: UserArgumentResolver

    @BeforeEach
    fun setUp() {
        authService = mockk()
        accountFacade = mockk()
        exceptionHandler = GlobalExceptionHandler()
        jwtTokenUtil = mockk()
        userArgumentResolver = UserArgumentResolver()
        authController = AuthController(authService, accountFacade, jwtTokenUtil)
        mockMvc = mockController(authController, exceptionHandler, userArgumentResolver)
        val userId = UserId.of("testUserId")
        val authentication = UsernamePasswordAuthenticationToken(userId, null)
        SecurityContextHolder.getContext().authentication = authentication
    }

    @Test
    @DisplayName("생성을 위한 휴대폰 인증번호 전송")
    fun sendPhoneVerification() {
        val requestBody = VerificationRequest.Phone(
            countryCode = "82",
            phoneNumber = "01012345678",
        )

        every { accountFacade.registerCredential(any(), CredentialTarget.SIGN_UP) } just Runs

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .post("/api/auth/create/send")
            .then()
            .assertCommonSuccessResponse()
            .apply(
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

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .post("/api/auth/create/send")
            .then()
            .assertErrorResponse(HttpStatus.CONFLICT, ErrorCode.USER_ALREADY_CREATED)
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.CONFLICT,
                        ErrorCode.USER_ALREADY_CREATED,
                        "계정이 이미 생성되었음으로 로그인으로 유도 해야함",
                    ),
                ),
            )
    }

    @Test
    @DisplayName("비밀번호 초기화를 위한 휴대폰 인증번호 전송")
    fun sendPhoneVerificationReset() {
        val requestBody = VerificationRequest.Phone(
            countryCode = "82",
            phoneNumber = "01012345678",
        )

        every { accountFacade.registerCredential(any(), CredentialTarget.RESET) } just Runs

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .post("/api/auth/reset/send")
            .then()
            .assertCommonSuccessResponse()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestFields(
                        fieldWithPath("countryCode").description("국가 코드"),
                        fieldWithPath("phoneNumber").description("휴대폰 번호"),
                    ),
                    responseSuccessFields(),
                ),
            )
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

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .post("/api/auth/reset/send")
            .then()
            .assertErrorResponse(HttpStatus.CONFLICT, ErrorCode.USER_NOT_CREATED)
            .apply(
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

        verify { accountFacade.registerCredential(any(), CredentialTarget.RESET) }
    }

    @Test
    @DisplayName("휴대폰 인증번호 확인")
    fun signUp() {
        val jwtToken = createJwtToken()
        val userId = createUserId()

        val requestBody = SignUpRequest.Phone(
            phoneNumber = "01012345678",
            countryCode = "82",
            verificationCode = "123",
            deviceId = "testDeviceId",
            provider = "ios",
            appToken = "testToken",
            userName = "testName",
        )

        every { accountFacade.createUser(any(), any(), any(), any(), any()) } returns userId
        every { jwtTokenUtil.createJwtToken(userId) } returns jwtToken
        every { authService.createLoginInfo(userId, jwtToken.refreshToken) } just Runs

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .post("/api/auth/create/verify")
            .then()
            .statusCode(200)
            .body("status", equalTo(200))
            .body("data.accessToken", equalTo(jwtToken.accessToken))
            .body("data.refreshToken", equalTo(jwtToken.refreshToken.token))
            .apply(
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
                        fieldWithPath("data.accessToken").description("액세스 토큰"),
                        fieldWithPath("data.refreshToken").description("리프레시 토큰"),
                    ),
                ),
            )

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

        every {
            accountFacade.createUser(
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } throws AuthorizationException(ErrorCode.WRONG_VERIFICATION_CODE)

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .post("/api/auth/create/verify")
            .then()
            .assertErrorResponse(HttpStatus.UNAUTHORIZED, ErrorCode.WRONG_VERIFICATION_CODE)
            .apply(
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

        every {
            accountFacade.createUser(
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } throws AuthorizationException(ErrorCode.EXPIRED_VERIFICATION_CODE)

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .post("/api/auth/create/verify")
            .then()
            .assertErrorResponse(HttpStatus.UNAUTHORIZED, ErrorCode.EXPIRED_VERIFICATION_CODE)
            .apply(
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

        every {
            accountFacade.createUser(
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } throws ConflictException(ErrorCode.USER_ALREADY_CREATED)

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .post("/api/auth/create/verify")
            .then()
            .assertErrorResponse(HttpStatus.CONFLICT, ErrorCode.USER_ALREADY_CREATED)
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.CONFLICT,
                        ErrorCode.USER_ALREADY_CREATED,
                        "계정이 이미 생성되었으므로 로그인으로 유도 해야함. 다만, 전화번호 인증 요청시 이미 검증이 되었는데 발생한 잘못된 접근 임.",
                    ),
                ),
            )

        verify { accountFacade.createUser(any(), any(), any(), any(), any()) }
    }

    @Test
    @DisplayName("비밀번호 초기화를 위한 휴대폰 인증번호 확인")
    fun resetCredential() {
        val jwtToken = createJwtToken()
        val userId = createUserId()

        val requestBody = VerifyOnlyRequest(
            phoneNumber = "01012345678",
            countryCode = "82",
            verificationCode = "123",
        )

        every { accountFacade.resetCredential(any(), any()) } returns userId
        every { jwtTokenUtil.createJwtToken(userId) } returns jwtToken
        every { authService.createLoginInfo(userId, jwtToken.refreshToken) } just Runs

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .post("/api/auth/reset/verify")
            .then()
            .statusCode(200)
            .body("status", equalTo(200))
            .body("data.accessToken", equalTo(jwtToken.accessToken))
            .body("data.refreshToken", equalTo(jwtToken.refreshToken.token))
            .apply(
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
                        fieldWithPath("data.accessToken").description("액세스 토큰"),
                        fieldWithPath("data.refreshToken").description("리프레시 토큰"),
                    ),
                ),
            )

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

        every {
            accountFacade.resetCredential(
                any(),
                any(),
            )
        } throws AuthorizationException(ErrorCode.WRONG_VERIFICATION_CODE)

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .post("/api/auth/reset/verify")
            .then()
            .assertErrorResponse(HttpStatus.UNAUTHORIZED, ErrorCode.WRONG_VERIFICATION_CODE)
            .apply(
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

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .post("/api/auth/reset/verify")
            .then()
            .assertErrorResponse(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND)
            .apply(
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

        every {
            accountFacade.resetCredential(
                any(),
                any(),
            )
        } throws AuthorizationException(ErrorCode.EXPIRED_VERIFICATION_CODE)

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .post("/api/auth/reset/verify")
            .then()
            .assertErrorResponse(HttpStatus.UNAUTHORIZED, ErrorCode.EXPIRED_VERIFICATION_CODE)
            .apply(
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
    }

    @Test
    @DisplayName("비밀번호 변경")
    fun changePassword() {
        val userId = "testUserId"
        val requestBody = SignUpRequest.Password(
            password = "testPassword",
        )

        every { accountFacade.changePassword(any(), any()) } just Runs
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .header("Authorization", "Bearer access-token")
            .attribute("userId", userId)
            .post("/api/auth/change/password")
            .then()
            .assertCommonSuccessResponse()
            .apply(
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
        verify { accountFacade.changePassword(any(), any()) }
    }

    @Test
    @DisplayName("비밀번호 생성")
    fun createPassword() {
        val userId = "testUserId"
        val requestBody = SignUpRequest.Password(
            password = "testPassword",
        )

        every { accountFacade.createPassword(any(), any()) } just Runs

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .header("Authorization", "Bearer access-token")
            .attribute("userId", userId)
            .post("/api/auth/create/password")
            .then()
            .assertCommonSuccessCreateResponse()
            .apply(
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
        verify { accountFacade.createPassword(any(), any()) }
    }

    @Test
    @DisplayName("로그인")
    fun login() {
        val jwtToken = createJwtToken()
        val userId = createUserId()

        val requestBody = LoginRequest(
            password = "testPassword",
            countryCode = "82",
            phoneNumber = "01012345678",
            deviceId = "testDeviceId",
            provider = "ios",
            appToken = "testToken",
        )

        every { accountFacade.login(any(), any(), any(), any()) } returns userId
        every { jwtTokenUtil.createJwtToken(userId) } returns jwtToken
        every { authService.createLoginInfo(userId, jwtToken.refreshToken) } just Runs

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .post("/api/auth/login")
            .then()
            .statusCode(200)
            .body("status", equalTo(200))
            .body("data.accessToken", equalTo(jwtToken.accessToken))
            .body("data.refreshToken", equalTo(jwtToken.refreshToken.token))
            .apply(
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
                        fieldWithPath("data.accessToken").description("액세스 토큰"),
                        fieldWithPath("data.refreshToken").description("리프레시 토큰"),
                    ),
                ),
            )

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

        every {
            accountFacade.login(
                any(),
                any(),
                any(),
                any(),
            )
        } throws AuthorizationException(ErrorCode.WRONG_PASSWORD)

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .post("/api/auth/login")
            .then()
            .assertErrorResponse(HttpStatus.UNAUTHORIZED, ErrorCode.WRONG_PASSWORD)
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.UNAUTHORIZED,
                        ErrorCode.WRONG_PASSWORD,
                        "비밀번호가 잘못되었음 - 새로운 인증 번호 요청으로 유도해야함",
                    ),
                ),
            )
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

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .post("/api/auth/login")
            .then()
            .assertErrorResponse(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND)
            .apply(
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
        verify { accountFacade.login(any(), any(), any(), any()) }
    }

    @Test
    @DisplayName("로그아웃")
    fun logout() {
        val requestBody = LogoutRequest(
            deviceId = "testDeviceId",
            provider = "ios",
        )
        every { accountFacade.logout(any(), any()) } just Runs
        every { jwtTokenUtil.validateRefreshToken(any()) } just Runs

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", "Bearer refreshToken")
            .body(requestBody)
            .delete("/api/auth/logout")
            .then()
            .assertCommonSuccessResponse()
            .statusCode(200)
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestHeaders(
                        headerWithName("Authorization").description("리프레시 토큰"),
                    ),
                    requestFields(
                        fieldWithPath("deviceId").description("디바이스 아이디(디바이스 식별을 위한 정보)"),
                        fieldWithPath("provider").description("플랫폼(ios, android)"),
                    ),
                    responseSuccessFields(),
                ),
            )
    }

    @Test
    @DisplayName("토큰 갱신")
    fun refreshAccessToken() {
        val jwtToken = createJwtToken()
        val userId = createUserId()
        every { jwtTokenUtil.refresh(any()) } returns Pair(jwtToken, userId)
        every { jwtTokenUtil.cleanedToken(any()) } returns jwtToken.refreshToken.token
        every { authService.updateLoginInfo(any(), jwtToken.refreshToken, userId) } just Runs

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", "Bearer RefreshToken")
            .get("/api/auth/refresh")
            .then()
            .statusCode(200)
            .body("status", equalTo(200))
            .body("data.accessToken", equalTo(jwtToken.accessToken))
            .body("data.refreshToken", equalTo(jwtToken.refreshToken.token))
            .apply(
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
    }
}
