package org.chewing.v1.facade

import org.chewing.v1.model.auth.Credential
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.friend.FriendShipService
import org.chewing.v1.service.user.UserService
import org.springframework.stereotype.Service

@Service
class FriendFacade(
    private val friendShipService: FriendShipService,
    private val userService: UserService,
) {

    fun addFriend(
        userId: UserId,
        friendName: String,
        targetCredential: Credential,
    ) {
        // 유저 정보 가져오기
        val targetUser = userService.getUserByCredential(targetCredential, AccessStatus.ACCESS)
        // 나의 정보를 읽어온다.
        val user = userService.getUser(userId)
        // 친구 추가
        friendShipService.createFriendShip(userId, user.name, targetUser.userId, friendName)
    }
}
