package org.chewing.v1.facade

import org.chewing.v1.model.auth.Credential
import org.chewing.v1.model.user.UserName
import org.chewing.v1.service.auth.AuthService
import org.chewing.v1.service.friend.FriendShipService
import org.chewing.v1.service.user.UserService
import org.springframework.stereotype.Service

@Service
class FriendFacade(
    private val friendShipService: FriendShipService,
    private val userService: UserService,
    private val authService: AuthService,
) {

    fun addFriend(
        userId: String,
        friendName: UserName,
        targetContact: Credential,
    ) {
        // 저장할 친구 정보를 읽어옴
        val contact = authService.getContact(targetContact)
        // 유저 정보 가져오기
        val targetUser = userService.getUserByContact(contact)
        // 나의 정보를 읽어온다.
        val userAccount = userService.getUserAccount(userId)
        // 친구 추가
        friendShipService.createFriendShip(userId, userAccount.user.name, targetUser.userId, friendName)
    }
}
