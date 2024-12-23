package org.chewing.v1.controller.user

import org.chewing.v1.dto.request.user.UserRequest
import org.chewing.v1.dto.response.user.AccountResponse
import org.chewing.v1.facade.AccountFacade
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.response.SuccessOnlyResponse
import org.chewing.v1.service.user.UserService
import org.chewing.v1.util.helper.FileHelper
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService,
    private val accountFacade: AccountFacade,
) {
    @GetMapping("/profile")
    fun getAccount(
        @RequestAttribute("userId") userId: String,
    ): SuccessResponseEntity<AccountResponse> {
        val account = accountFacade.getAccount(userId)
        return ResponseHelper.success(AccountResponse.of(account))
    }

    @PostMapping("/access")
    fun makeAccess(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: UserRequest.UpdateProfile,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        userService.makeAccess(userId, request.toUserContent())
        return ResponseHelper.successOnly()
    }

    /**
     * @param file: 프로파일 이미지를 MultipartFile로 받습니다.
     * 로그인한 사용자의 프로파일 이미지를 변경합니다.
     */
    @PostMapping("/image")
    fun changeUserImage(
        @RequestPart("file") file: MultipartFile,
        @RequestAttribute("userId") userId: String,
        @RequestParam("category") category: FileCategory,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        val convertedFile = FileHelper.convertMultipartFileToFileData(file)
        userService.updateFile(convertedFile, userId, category)
        return ResponseHelper.successOnly()
    }

    @PutMapping("/name")
    fun changeName(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: UserRequest.UpdateName,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        userService.updateName(userId, request.toUserName())
        return ResponseHelper.successOnly()
    }

    @PutMapping("birth")
    fun changeBirth(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: UserRequest.UpdateBirth,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        userService.updateBirth(userId, request.toBirth())
        return ResponseHelper.successOnly()
    }

    @DeleteMapping("")
    fun deleteUser(
        @RequestAttribute("userId") userId: String,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        accountFacade.deleteAccount(userId)
        return ResponseHelper.successOnly()
    }

    @PostMapping("/tts")
    fun changeTTS(
        @RequestAttribute("userId") userId: String,
        @RequestPart("file") file: MultipartFile,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        val convertedFile = FileHelper.convertMultipartFileToFileData(file)
        userService.updateFile(convertedFile, userId, FileCategory.TTS)
        return ResponseHelper.successOnly()
    }
}
