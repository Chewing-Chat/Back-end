package org.chewing.v1.controller.auth

import org.chewing.v1.dto.request.auth.LoginRequest
import org.chewing.v1.dto.request.auth.SignUpRequest
import org.chewing.v1.dto.request.auth.VerificationRequest
import org.chewing.v1.dto.request.auth.VerifyOnlyRequest
import org.chewing.v1.dto.response.auth.TokenResponse
import org.chewing.v1.facade.AccountFacade
import org.chewing.v1.model.auth.CredentialTarget
import org.chewing.v1.model.user.UserId
import org.chewing.v1.response.HttpResponse
import org.chewing.v1.response.SuccessCreateResponse
import org.chewing.v1.response.SuccessOnlyResponse
import org.chewing.v1.service.auth.AuthService
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.chewing.v1.util.security.JwtTokenUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val accountFacade: AccountFacade,
    private val jwtTokenUtil: JwtTokenUtil,
) {

    @PostMapping("/create/send")
    fun sendCreatePhoneVerification(@RequestBody request: VerificationRequest.Phone): ResponseEntity<HttpResponse<SuccessOnlyResponse>> {
        accountFacade.registerCredential(request.toPhoneNumber(), CredentialTarget.SIGN_UP)
        return ResponseHelper.successOnly()
    }

    @PostMapping("/reset/send")
    fun sendResetPhoneVerification(@RequestBody request: VerificationRequest.Phone): ResponseEntity<HttpResponse<SuccessOnlyResponse>> {
        accountFacade.registerCredential(request.toPhoneNumber(), CredentialTarget.RESET)
        return ResponseHelper.successOnly()
    }

    @PostMapping("/create/verify")
    fun signUp(
        @RequestBody request: SignUpRequest.Phone,
    ): SuccessResponseEntity<TokenResponse> {
        val userId = accountFacade.createUser(
            request.toPhoneNumber(),
            request.toVerificationCode(),
            request.toAppToken(),
            request.toDevice(),
            request.toUserName(),
        )
        val jwtToken = jwtTokenUtil.createJwtToken(userId)
        authService.createLoginInfo(userId, jwtToken.refreshToken)
        return ResponseHelper.success(TokenResponse.of(jwtToken))
    }

    @PostMapping("/reset/verify")
    fun resetCredential(
        @RequestBody request: VerifyOnlyRequest,
    ): SuccessResponseEntity<TokenResponse> {
        val userId = accountFacade.resetCredential(
            request.toPhoneNumber(),
            request.toVerificationCode(),
        )
        val jwtToken = jwtTokenUtil.createJwtToken(userId)
        authService.createLoginInfo(userId, jwtToken.refreshToken)
        return ResponseHelper.success(TokenResponse.of(jwtToken))
    }

    @PostMapping("/change/password")
    fun changePassword(
        @RequestBody request: SignUpRequest.Password,
        @RequestAttribute("userId") userId: String,
    ): ResponseEntity<HttpResponse<SuccessOnlyResponse>> {
        accountFacade.changePassword(
            UserId.of(userId),
            request.password,
        )
        return ResponseHelper.successOnly()
    }

    @PostMapping("/create/password")
    fun makePassword(
        @RequestBody request: SignUpRequest.Password,
        @RequestAttribute("userId") userId: String,
    ): SuccessResponseEntity<SuccessCreateResponse> {
        accountFacade.changePassword(
            UserId.of(userId),
            request.password,
        )
        return ResponseHelper.successCreateOnly()
    }

    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequest,
    ): SuccessResponseEntity<TokenResponse> {
        val userId =
            accountFacade.login(request.toPhoneNumber(), request.toPassword(), request.toDevice(), request.toAppToken())
        val jwtToken = jwtTokenUtil.createJwtToken(userId)
        authService.createLoginInfo(userId, jwtToken.refreshToken)
        return ResponseHelper.success(TokenResponse.of(jwtToken))
    }

    @DeleteMapping("/logout")
    fun logout(
        @RequestHeader("Authorization") refreshToken: String,
    ): ResponseEntity<HttpResponse<SuccessOnlyResponse>> {
        jwtTokenUtil.validateRefreshToken(refreshToken)
        authService.logout(refreshToken)
        return ResponseHelper.successOnly()
    }

    @GetMapping("/refresh")
    fun refreshJwtToken(@RequestHeader("Authorization") refreshToken: String): SuccessResponseEntity<TokenResponse> {
        val (newToken, userId) = jwtTokenUtil.refresh(refreshToken)
        authService.updateLoginInfo(refreshToken, newToken.refreshToken, userId)
        return ResponseHelper.success(TokenResponse.of(newToken))
    }
}
