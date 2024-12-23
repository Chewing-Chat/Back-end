package org.chewing.v1.controller.friend

import org.chewing.v1.dto.request.friend.FriendRequest
import org.chewing.v1.facade.FriendFacade
import org.chewing.v1.response.SuccessCreateResponse
import org.chewing.v1.response.SuccessOnlyResponse
import org.chewing.v1.service.friend.FriendShipService
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/friend")
class FriendController(
    private val friendFacade: FriendFacade,
    private val friendShipService: FriendShipService,
) {
    // 오류 관련 GlobalExceptionHandler 참조 404, 401, 409번만 사용
    @PostMapping("/email")
    fun addFriendWithEmail(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: FriendRequest.AddWithEmail,
    ): SuccessResponseEntity<SuccessCreateResponse> {
        friendFacade.addFriend(userId, request.toUserName(), request.toEmail())
        // 생성 완료 응답 201 반환
        return ResponseHelper.successCreateOnly()
    }

    @PostMapping("/phone")
    fun addFriendWithPhone(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: FriendRequest.AddWithPhone,
    ): SuccessResponseEntity<SuccessCreateResponse> {
        friendFacade.addFriend(userId, request.toUserName(), request.toPhoneNumber())
        // 생성 완료 응답 201 반환
        return ResponseHelper.successCreateOnly()
    }

    @PutMapping("/favorite")
    fun changeFavorite(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: FriendRequest.UpdateFavorite,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        val (friendId, favorite) = request
        friendShipService.changeFriendFavorite(userId, friendId, favorite)
        // 성공 응답 200 반환
        return ResponseHelper.successOnly()
    }

    @DeleteMapping("")
    fun deleteFriend(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: FriendRequest.Delete,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        val friendId = request.friendId
        friendShipService.removeFriendShip(userId, friendId)
        // 성공 응답 200 반환
        return ResponseHelper.successOnly()
    }

    @DeleteMapping("/block")
    fun blockFriend(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: FriendRequest.Block,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        val friendId = request.friendId
        friendShipService.blockFriendShip(userId, friendId)
        // 성공 응답 200 반환
        return ResponseHelper.successOnly()
    }

    @PutMapping("/name")
    fun changeFriendName(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: FriendRequest.UpdateName,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        friendShipService.changeFriendName(userId, request.toFriendId(), request.toFriendName())
        // 생성 완료 응답 201 반환
        return ResponseHelper.successOnly()
    }
}
