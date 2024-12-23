package org.chewing.v1.controller.auth

import org.chewing.v1.dto.request.auth.LoginRequest
import org.chewing.v1.dto.request.auth.VerificationCheckRequest
import org.chewing.v1.dto.request.auth.VerificationRequest
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

    @PostMapping("/phone/create/send")
    fun sendPhoneVerification(@RequestBody request: VerificationRequest.Phone): ResponseEntity<HttpResponse<SuccessOnlyResponse>> {
        authService.createCredential(request.toPhoneNumber())
        return ResponseHelper.successOnly()
    }

    @PostMapping("/phone/create/verify")
    fun verifyPhone(
        @RequestBody request: LoginRequest.Phone,
    ): SuccessResponseEntity<LogInfoResponse> {
        val loginInfo = accountFacade.loginAndCreateUser(
            request.toPhoneNumber(),
            request.toVerificationCode(),
            request.toAppToken(),
            request.toDevice(),
        )
        return ResponseHelper.success(LogInfoResponse.of(loginInfo))
    }

    @PostMapping("/email/create/send")
    fun sendEmailVerification(@RequestBody request: VerificationRequest.Email): ResponseEntity<HttpResponse<SuccessOnlyResponse>> {
        authService.createCredential(request.toEmailAddress())
        return ResponseHelper.successOnly()
    }

    @PostMapping("/email/create/verify")
    fun verifyEmail(
        @RequestBody request: LoginRequest.Email,
    ): SuccessResponseEntity<LogInfoResponse> {
        val loginInfo = accountFacade.loginAndCreateUser(
            request.toEmailAddress(),
            request.toVerificationCode(),
            request.toAppToken(),
            request.toDevice(),
        )
        return ResponseHelper.success(LogInfoResponse.of(loginInfo))
    }

    @PostMapping("/phone/update/send")
    fun sendPhoneVerification(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: VerificationRequest.Phone,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        authService.createCredentialNotUsed(userId, request.toPhoneNumber())
        return ResponseHelper.successOnly()
    }

    @PostMapping("/email/update/send")
    fun sendEmailVerification(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: VerificationRequest.Email,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        authService.createCredentialNotUsed(userId, request.toEmailAddress())
        return ResponseHelper.successOnly()
    }

    @PostMapping("/phone/update/verify")
    fun changePhone(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: VerificationCheckRequest.Phone,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        accountFacade.changeCredential(userId, request.toPhoneNumber(), request.toVerificationCode())
        return ResponseHelper.successOnly()
    }

    @PostMapping("/email/update/verify")
    fun changeEmail(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: VerificationCheckRequest.Email,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        accountFacade.changeCredential(userId, request.toEmailAddress(), request.toVerificationCode())
        return ResponseHelper.successOnly()
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
