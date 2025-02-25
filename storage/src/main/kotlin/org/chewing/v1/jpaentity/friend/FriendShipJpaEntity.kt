package org.chewing.v1.jpaentity.friend

import jakarta.persistence.*
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.friend.FriendShipStatus
import org.chewing.v1.model.user.UserId
import org.hibernate.annotations.DynamicInsert

@DynamicInsert
@Entity
@Table(name = "friend_ship", schema = "chewing")
internal class FriendShipJpaEntity(
    @EmbeddedId
    private val id: FriendShipId,
    private var favorite: Boolean,
    private var name: String,
    @Enumerated(EnumType.STRING)
    private var status: FriendShipStatus,
) : BaseEntity() {
    companion object {
        fun generate(userId: UserId, targetUserId: UserId, name: String, status: FriendShipStatus): FriendShipJpaEntity {
            return FriendShipJpaEntity(
                id = FriendShipId(userId.id, targetUserId.id),
                favorite = false,
                name = name,
                status = status,
            )
        }
    }

    fun updateFavorite(favorite: Boolean) {
        this.favorite = favorite
    }

    fun updateName(name: String) {
        this.name = name
    }

    fun allowedFriend() {
        this.status = FriendShipStatus.FRIEND
    }

    fun toFriendShip(): FriendShip {
        return FriendShip.of(
            userId = UserId.of(id.userId),
            friendId = UserId.of(id.friendId),
            friendName = name,
            isFavorite = favorite,
            status = status,
        )
    }

    fun updateBlock() {
        this.status = FriendShipStatus.BLOCK
    }

    fun updateBlocked() {
        this.status = FriendShipStatus.BLOCKED
    }
    fun updateDelete() {
        this.status = FriendShipStatus.DELETE
    }
}
