package org.chewing.v1.implementation.friend.friendship

import org.chewing.v1.model.friend.FriendShipProfile
import org.chewing.v1.model.user.User
import org.springframework.stereotype.Component

@Component
class FriendShipFilter {
    fun filterTargetFriend(
        targetUsers: List<User>,
        friendShipProfiles: List<FriendShipProfile>,
    ): List<Pair<User, FriendShipProfile>> {
        return friendShipProfiles.mapNotNull { profile ->
            val matchingUser = targetUsers.find { user ->
                user.localPhoneNumber.number == profile.localPhoneNumber.number &&
                    user.localPhoneNumber.countryCode == profile.localPhoneNumber.countryCode
            }
            matchingUser?.let { it to profile }
        }
    }
}
