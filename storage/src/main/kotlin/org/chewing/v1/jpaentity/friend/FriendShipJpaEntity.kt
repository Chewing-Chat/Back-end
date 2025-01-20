package org.chewing.v1.jpaentity.friend

import jakarta.persistence.*
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.user.AccessStatus
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
    private var type: AccessStatus,
) : BaseEntity() {
    companion object {
        fun generate(userId: UserId, targetUserId: UserId, name: String): FriendShipJpaEntity {
            return FriendShipJpaEntity(
                id = FriendShipId(userId.id, targetUserId.id),
                favorite = false,
                name = name,
                type = AccessStatus.ACCESS,
            )
        }
    }

    fun updateFavorite(favorite: Boolean) {
        this.favorite = favorite
    }

    fun updateName(name: String) {
        this.name = name
    }

    fun toFriendShip(): FriendShip {
        return FriendShip.of(
            friendId = UserId.of(id.friendId),
            friendName = name,
            isFavorite = favorite,
            type = type,
        )
    }

    fun updateBlock() {
        this.type = AccessStatus.BLOCK
    }

    fun updateBlocked() {
        this.type = AccessStatus.BLOCKED
    }
    fun updateDelete() {
        this.type = AccessStatus.DELETE
    }

    fun getId(): FriendShipId {
        return id
    }
}
