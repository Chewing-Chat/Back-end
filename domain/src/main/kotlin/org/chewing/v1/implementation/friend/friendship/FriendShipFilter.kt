package org.chewing.v1.implementation.friend.friendship

import org.chewing.v1.model.friend.FriendShipProfile
import org.chewing.v1.model.user.User
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Component

@Component
class FriendShipFilter {
    fun filterTargetFriend(
        userId: UserId,
        targetUsers: List<User>,
        friendShipProfiles: List<FriendShipProfile>,
    ): List<Pair<User, FriendShipProfile>> {
        return friendShipProfiles.mapNotNull { profile ->
            val matchingUser = targetUsers
                .filter { it.info.userId != userId }
                .find { user ->
                    user.localPhoneNumber.number == profile.localPhoneNumber.number &&
                        user.localPhoneNumber.countryCode == profile.localPhoneNumber.countryCode
                }
            matchingUser?.let { it to profile }
        }
    }
}
