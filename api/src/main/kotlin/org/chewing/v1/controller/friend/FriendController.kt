package org.chewing.v1.controller.friend

import org.chewing.v1.dto.request.friend.FriendRequest
import org.chewing.v1.dto.response.friend.FriendListResponse
import org.chewing.v1.dto.response.friend.FriendResponse
import org.chewing.v1.facade.FriendFacade
import org.chewing.v1.model.contact.LocalPhoneNumber
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserId
import org.chewing.v1.response.SuccessOnlyResponse
import org.chewing.v1.service.friend.FriendShipService
import org.chewing.v1.service.user.UserService
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.chewing.v1.util.security.CurrentUser
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/friend")
class FriendController(
    private val friendFacade: FriendFacade,
    private val friendShipService: FriendShipService,
    private val userService: UserService,
) {

    // 전화번호 관련

    // ✅ 가입된 사용자 확인 API
    @PostMapping("/check")
    fun checkRegisteredFriends(@RequestBody request: List<FriendRequest.Create>): SuccessResponseEntity<List<FriendResponse>> {
        println("📢 요청 받은 친구 리스트: $request") // 요청 데이터 로깅

        // 요청된 전화번호 리스트 생성
        val phoneNumbers = request.map { LocalPhoneNumber.of(it.phoneNumber, it.countryCode) }
        println("📢 변환된 전화번호 리스트: ${phoneNumbers.map { it.toString() }}")

        // 📌 DB에서 존재하는 유저 조회
        val users = userService.getUsersByContacts(phoneNumbers, AccessStatus.ACCESS)
        println("📢 DB에서 조회된 유저 수: ${users.size}")
        users.forEach { println("📢 DB에서 조회된 유저: ${it.info.name}, 전화번호: ${it.info.phoneNumber.e164PhoneNumber}") }

        // 📌 DB에 저장된 전화번호 리스트 (E.164 형식)
        val dbPhoneNumbers = users.map { it.info.phoneNumber.e164PhoneNumber }.toSet()
        // 📌 요청된 전화번호를 E.164 형식으로 변환 후 필터링
        val filteredRequests = request.filter {
            val normalizedPhone = normalizePhoneNumber(it.phoneNumber, it.countryCode) // 변환된 전화번호
            normalizedPhone in dbPhoneNumbers
        }

        println("📢 DB에 존재하는 전화번호 목록: $dbPhoneNumbers")
        println("📢 필터링된 요청된 친구 목록: $filteredRequests")

        // 📌 DB에 존재하는 친구만 응답으로 반환
        val friends = users.map { user ->
            FriendResponse(
                friendId = user.info.userId.id,
                name = user.info.name,
                profileImageUrl = user.info.image.url,
                profileImageType = user.info.image.type.value(),
                phoneNumber = user.info.phoneNumber.e164PhoneNumber, // 🔥 DB에서 조회된 전화번호 사용
                countryCode = user.localPhoneNumber.countryCode,
                statusMessage = user.info.statusMessage,
                favorite = false,
                status = user.info.status.name.lowercase()
            )
        }
        println("📢 최종 반환되는 친구 목록: $friends")  // 🔍 반환되는 친구 목록 확인

        return ResponseHelper.success(friends)
    }
    // ✅ 전화번호를 E.164 형식으로 변환하는 함수 추가
    fun normalizePhoneNumber(phoneNumber: String, countryCode: String): String {
        return if (phoneNumber.startsWith("0")) {
            countryCode + phoneNumber.substring(1) // 01012345678 → +821012345678
        } else {
            phoneNumber // 이미 국가 코드 포함된 경우 그대로 반환
        }
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
