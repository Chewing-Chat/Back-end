package org.chewing.v1.jpaentity.friend

import jakarta.persistence.*
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserName
import org.hibernate.annotations.DynamicInsert

@DynamicInsert
@Entity
@Table(name = "friend_ship", schema = "chewing")
internal class FriendShipJpaEntity(
    @EmbeddedId
    private val id: FriendShipId,
    private var favorite: Boolean,
    private var firstName: String,
    private var lastName: String,
    @Enumerated(EnumType.STRING)
    private var type: AccessStatus,
) : BaseEntity() {
    companion object {
        fun generate(userId: String, targetUserId: String, targetUserName: UserName): FriendShipJpaEntity {
            return FriendShipJpaEntity(
                id = FriendShipId(userId, targetUserId),
                favorite = false,
                firstName = targetUserName.firstName(),
                lastName = targetUserName.lastName(),
                type = AccessStatus.ACCESS,
            )
        }
    }

    fun updateFavorite(favorite: Boolean) {
        this.favorite = favorite
    }

    fun updateName(friendName: UserName) {
        this.firstName = friendName.firstName()
        this.lastName = friendName.lastName()
    }

    fun toFriendShip(): FriendShip {
        return FriendShip.of(
            friendId = id.friendId,
            friendName = UserName.of(firstName, lastName),
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
