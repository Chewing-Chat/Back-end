package org.chewing.v1.controller.auth

import org.chewing.v1.dto.request.auth.LoginRequest
import org.chewing.v1.dto.request.auth.SignUpRequest
import org.chewing.v1.dto.request.auth.VerificationRequest
import org.chewing.v1.dto.request.auth.VerifyOnlyRequest
import org.chewing.v1.dto.response.auth.LogInfoResponse
import org.chewing.v1.dto.response.auth.TokenResponse
import org.chewing.v1.facade.AccountFacade
import org.chewing.v1.response.HttpResponse
import org.chewing.v1.response.SuccessOnlyResponse
import org.chewing.v1.service.auth.AuthService
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val accountFacade: AccountFacade,
) {

    @PostMapping("/create/send")
    fun sendCreatePhoneVerification(@RequestBody request: VerificationRequest.Phone): ResponseEntity<HttpResponse<SuccessOnlyResponse>> {
        authService.createCredential(request.toPhoneNumber())
        return ResponseHelper.successOnly()
    }

    @PostMapping("/reset/send")
    fun sendResetPhoneVerification(@RequestBody request: VerificationRequest.Phone): ResponseEntity<HttpResponse<SuccessOnlyResponse>> {
        authService.createCredential(request.toPhoneNumber())
        return ResponseHelper.successOnly()
    }

    @PostMapping("/create/verify")
    fun signUpPhone(
        @RequestBody request: SignUpRequest.Phone,
    ): SuccessResponseEntity<LogInfoResponse> {
        val loginInfo = accountFacade.createUser(
            request.toPhoneNumber(),
            request.toVerificationCode(),
            request.toAppToken(),
            request.toDevice(),
            request.toUserName(),
        )
        return ResponseHelper.success(LogInfoResponse.of(loginInfo))
    }

    @PostMapping("/reset/verify")
    fun verifyPhoneOnly(
        @RequestBody request: VerifyOnlyRequest,
    ): SuccessResponseEntity<LogInfoResponse> {
        val loginInfo = accountFacade.verifyPhoneOnly(
            request.toPhoneNumber(),
            request.toVerificationCode(),
        )
        return ResponseHelper.success(LogInfoResponse.of(loginInfo))
    }

    @PostMapping("/change/password")
    fun changePassword(
        @RequestBody request: SignUpRequest.Password,
        @RequestAttribute("userId") userId: String,
    ): ResponseEntity<HttpResponse<SuccessOnlyResponse>> {
        accountFacade.makePassword(
            userId,
            request.password,
        )
        return ResponseHelper.successOnly()
    }

    @PostMapping("/create/password")
    fun makePassword(
        @RequestBody request: SignUpRequest.Password,
        @RequestAttribute("userId") userId: String,
    ): ResponseEntity<HttpResponse<SuccessOnlyResponse>> {
        accountFacade.makePassword(
            userId,
            request.password,
        )
        return ResponseHelper.successOnly()
    }

    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequest,
    ): SuccessResponseEntity<LogInfoResponse> {
        val loginInfo = accountFacade.login(request.toPhoneNumber(), request.toPassword())
        return ResponseHelper.success(LogInfoResponse.of(loginInfo))
    }

    @DeleteMapping("/logout")
    fun logout(
        @RequestHeader("Authorization") refreshToken: String,
    ): ResponseEntity<HttpResponse<SuccessOnlyResponse>> {
        authService.logout(refreshToken)
        return ResponseHelper.successOnly()
    }

    @GetMapping("/refresh")
    fun refreshJwtToken(@RequestHeader("Authorization") refreshToken: String): SuccessResponseEntity<TokenResponse> {
        val token = authService.refreshJwtToken(refreshToken)
        return ResponseHelper.success(TokenResponse.of(token))
    }
}
