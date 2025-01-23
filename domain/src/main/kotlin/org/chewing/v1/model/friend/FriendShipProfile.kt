package org.chewing.v1.model.friend

import org.chewing.v1.model.contact.LocalPhoneNumber

class FriendShipProfile private constructor(
    val localPhoneNumber: LocalPhoneNumber,
    val friendName: String,
) {
    companion object {
        fun of(
            localPhoneNumber: LocalPhoneNumber,
            friendName: String,
        ): FriendShipProfile {
            return FriendShipProfile(
                localPhoneNumber = localPhoneNumber,
                friendName = friendName,
            )
        }
    }
}
