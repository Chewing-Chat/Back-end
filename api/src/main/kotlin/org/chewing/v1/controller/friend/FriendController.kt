package org.chewing.v1.controller.friend

import org.chewing.v1.dto.request.friend.FriendRequest
import org.chewing.v1.dto.response.friend.FriendListResponse
import org.chewing.v1.facade.FriendFacade
import org.chewing.v1.model.user.UserId
import org.chewing.v1.response.SuccessOnlyResponse
import org.chewing.v1.service.friend.FriendShipService
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.chewing.v1.util.security.CurrentUser
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/friend")
class FriendController(
    private val friendFacade: FriendFacade,
    private val friendShipService: FriendShipService,
) {

    @PostMapping("/check")
    fun checkRegisteredFriends(
        @CurrentUser userId: UserId,
        @RequestBody request: List<FriendRequest.Check>,
    ): SuccessResponseEntity<FriendListResponse> {
        val friends = friendFacade.findFriends(userId, request.map { it.toLocalPhoneNumber() })
        return ResponseHelper.success(FriendListResponse.of(friends))
    }

    @PostMapping("/list")
    fun createFriends(
        @CurrentUser userId: UserId,
        @RequestBody request: List<FriendRequest.Create>,
    ): SuccessResponseEntity<FriendListResponse> {
        val friends = friendFacade.createFriends(userId, request.map { it.toFriendShipProfile() })
        return ResponseHelper.successCreate(FriendListResponse.of(friends))
    }

    @PutMapping("/favorite")
    fun changeFavorite(
        @CurrentUser userId: UserId,
        @RequestBody request: FriendRequest.UpdateFavorite,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        val (friendId, favorite) = request
        friendShipService.changeFriendFavorite(userId, UserId.of(friendId), favorite)
        return ResponseHelper.successOnly()
    }

    @DeleteMapping("")
    fun deleteFriend(
        @CurrentUser userId: UserId,
        @RequestBody request: FriendRequest.Delete,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        val friendId = request.friendId
        friendShipService.removeFriendShip(userId, UserId.of(friendId))
        return ResponseHelper.successOnly()
    }

    @DeleteMapping("/block")
    fun blockFriend(
        @CurrentUser userId: UserId,
        @RequestBody request: FriendRequest.Block,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        val friendId = request.friendId
        friendShipService.blockFriendShip(userId, UserId.of(friendId))
        return ResponseHelper.successOnly()
    }

    @PutMapping("/unblock")
    fun unblockFriend(
        @CurrentUser userId: UserId,
        @RequestBody request: FriendRequest.Unblock,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        val friendId = request.friendId
        friendShipService.unblockFriendShip(userId, UserId.of(friendId))
        return ResponseHelper.successOnly()
    }

    @PutMapping("/name")
    fun changeFriendName(
        @CurrentUser userId: UserId,
        @RequestBody request: FriendRequest.UpdateName,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        friendShipService.changeFriendName(userId, request.toFriendId(), request.toFriendName())
        return ResponseHelper.successOnly()
    }

    @PutMapping("/allowed")
    fun changeFriendStatus(
        @CurrentUser userId: UserId,
        @RequestBody request: FriendRequest.UpdateStatus,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        friendFacade.allowedFriend(userId, request.toFriendId(), request.toFriendName())
        return ResponseHelper.successOnly()
    }
}
