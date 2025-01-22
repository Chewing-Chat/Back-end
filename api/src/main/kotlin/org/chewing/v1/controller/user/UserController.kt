package org.chewing.v1.controller.user

import org.chewing.v1.dto.request.user.UserRequest
import org.chewing.v1.dto.response.user.AccountResponse
import org.chewing.v1.facade.AccountFacade
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.user.UserId
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
    fun getProfile(
        @RequestAttribute("userId") userId: String,
    ): SuccessResponseEntity<AccountResponse> {
        val user = userService.getUser(UserId.of(userId))
        return ResponseHelper.success(AccountResponse.of(user))
    }

    /**
     * @param file: 프로파일 이미지를 MultipartFile로 받습니다.
     * 로그인한 사용자의 프로파일 이미지를 변경합니다.
     */
    @PostMapping("/image")
    fun changeUserImage(
        @RequestPart("file") file: MultipartFile,
        @RequestAttribute("userId") userId: String,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        val convertedFile = FileHelper.convertMultipartFileToFileData(file)
        userService.updateFile(convertedFile, UserId.of(userId), FileCategory.PROFILE)
        return ResponseHelper.successOnly()
    }

    @DeleteMapping("")
    fun deleteUser(
        @RequestAttribute("userId") userId: String,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        accountFacade.deleteAccount(UserId.of(userId))
        return ResponseHelper.successOnly()
    }

    @PutMapping("/status/message")
    fun changeStatusMessage(
        @RequestAttribute("userId") userId: String,
        @RequestBody statusMessage: UserRequest.UpdateStatusMessage,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        userService.updateStatusMessage(UserId.of(userId), statusMessage.toStatusMessage())
        return ResponseHelper.successOnly()
    }
}
