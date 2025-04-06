package org.chewing.v1.controller.user

import org.chewing.v1.dto.request.user.UserRequest
import org.chewing.v1.dto.response.user.AccountResponse
import org.chewing.v1.dto.response.user.PushResponse
import org.chewing.v1.facade.AccountFacade
import org.chewing.v1.model.auth.PushInfo
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserId
import org.chewing.v1.response.SuccessOnlyResponse
import org.chewing.v1.service.user.UserService
import org.chewing.v1.util.helper.FileHelper
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.chewing.v1.util.security.CurrentUser
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
        @CurrentUser userId: UserId,
    ): SuccessResponseEntity<AccountResponse> {
        val user = userService.getUser(userId, AccessStatus.ACCESS)
        return ResponseHelper.success(AccountResponse.of(user))
    }

    /**
     * @param file: 프로파일 이미지를 MultipartFile로 받습니다.
     * 로그인한 사용자의 프로파일 이미지를 변경합니다.
     */
    @PostMapping("/image")
    fun changeUserImage(
        @RequestPart("file") file: MultipartFile,
        @CurrentUser userId: UserId,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        val convertedFile = FileHelper.convertMultipartFileToFileData(file)
        userService.updateFile(convertedFile, userId, FileCategory.PROFILE)
        return ResponseHelper.successOnly()
    }

    @DeleteMapping("")
    fun deleteUser(
        @CurrentUser userId: UserId,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        accountFacade.deleteAccount(userId)
        return ResponseHelper.successOnly()
    }

    @PutMapping("/status/message")
    fun changeStatusMessage(
        @CurrentUser userId: UserId,
        @RequestBody statusMessage: UserRequest.UpdateStatusMessage,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        userService.updateStatusMessage(userId, statusMessage.toStatusMessage())
        return ResponseHelper.successOnly()
    }

    @GetMapping("/push/notification/{deviceId}")
    fun getPushNotification(
        @CurrentUser userId: UserId,
        @PathVariable("deviceId") deviceId: String,
    ): SuccessResponseEntity<PushResponse> {
        val pushInfo = userService.getPushInfo(userId, deviceId)
        return ResponseHelper.success(PushResponse.of(pushInfo))
    }

    @PutMapping("/push/notification/chat")
    fun updateChatPushNotification(
        @CurrentUser userId: UserId,
        @RequestBody pushInfo: UserRequest.UpdateNotification,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        userService.updatePushNotification(userId, pushInfo.toDeviceId(), pushInfo.toNotification(), PushInfo.PushType.CHAT)
        return ResponseHelper.successOnly()
    }

    @PutMapping("/push/notification/schedule")
    fun updateSchedulePushNotification(
        @CurrentUser userId: UserId,
        @RequestBody pushInfo: UserRequest.UpdateNotification,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        userService.updatePushNotification(userId, pushInfo.toDeviceId(), pushInfo.toNotification(), PushInfo.PushType.SCHEDULE)
        return ResponseHelper.successOnly()
    }
}
