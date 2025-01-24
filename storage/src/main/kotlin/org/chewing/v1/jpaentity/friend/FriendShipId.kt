package org.chewing.v1.jpaentity.friend

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.chewing.v1.model.user.UserId
import java.io.Serializable

@Embeddable
data class FriendShipId(
    @Column(name = "user_id")
    val userId: String,

    @Column(name = "friend_id")
    val friendId: String,
) : Serializable {
    companion object {
        fun of(userId: UserId, friendId: UserId): FriendShipId {
            return FriendShipId(userId.id, friendId.id)
        }
    }
}
