package org.chewing.v1.controller.user

import org.chewing.v1.dto.request.user.UserStatusRequest
import org.chewing.v1.dto.response.user.UserStatusesResponse
import org.chewing.v1.response.SuccessCreateResponse
import org.chewing.v1.response.SuccessOnlyResponse
import org.chewing.v1.service.user.UserStatusService
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user/status")
class UserStatusController(
    private val userStatusService: UserStatusService,
) {

    @GetMapping("")
    fun getUserStatus(
        @RequestAttribute("userId") userId: String,
    ): SuccessResponseEntity<UserStatusesResponse> {
        val userStatuses = userStatusService.getUserStatuses(userId)
        return ResponseHelper.success(UserStatusesResponse.of(userStatuses))
    }

    @DeleteMapping("")
    fun deleteProfileStatus(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: List<UserStatusRequest.Delete>,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        userStatusService.deleteUserStatuses(request.map { it.statusId })
        return ResponseHelper.successOnly()
    }

    @PostMapping("")
    fun addProfileStatus(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: UserStatusRequest.Add,
    ): SuccessResponseEntity<SuccessCreateResponse> {
        userStatusService.createUserStatus(userId, request.message, request.emoji)
        return ResponseHelper.successCreateOnly()
    }

    @PutMapping("/select")
    fun changeProfileSelectedStatus(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: UserStatusRequest.Update,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        userStatusService.selectUserStatus(userId, request.statusId)
        return ResponseHelper.successOnly()
    }

    @DeleteMapping("/select")
    fun deleteProfileSelectedStatus(
        @RequestAttribute("userId") userId: String,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        userStatusService.deleteSelectUserStatus(userId)
        return ResponseHelper.successOnly()
    }
}
