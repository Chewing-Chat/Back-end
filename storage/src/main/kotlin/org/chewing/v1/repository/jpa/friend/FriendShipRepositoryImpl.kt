package org.chewing.v1.repository.jpa.friend

import org.chewing.v1.jpaentity.friend.FriendShipId
import org.chewing.v1.jpaentity.friend.FriendShipJpaEntity
import org.chewing.v1.jparepository.friend.FriendShipJpaRepository
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.friend.FriendShipStatus
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.friend.FriendShipRepository
import org.chewing.v1.util.SortType
import org.springframework.stereotype.Repository

@Repository
internal class FriendShipRepositoryImpl(
    private val friendShipJpaRepository: FriendShipJpaRepository,
) : FriendShipRepository {
    override fun reads(userId: UserId): List<FriendShip> =
        friendShipJpaRepository.findAllByIdUserId(
            userId.id,
            SortType.FAVORITE_NAME_ASC.toSort(),
        ).map { it.toFriendShip() }

    override fun appendIfNotExist(
        userId: UserId,
        targetUserId: UserId,
        targetUserName: String,
        status: FriendShipStatus,
    ): FriendShip {
        val friendShipId = FriendShipId.of(userId, targetUserId)

        return friendShipJpaRepository.findById(friendShipId)
            .map { it.toFriendShip() }
            .orElseGet {
                val newFriendShip = FriendShipJpaEntity.generate(userId, targetUserId, targetUserName, status)
                friendShipJpaRepository.save(newFriendShip).toFriendShip()
            }
    }

    override fun remove(userId: UserId, friendId: UserId): UserId? =
        friendShipJpaRepository.findById(FriendShipId(userId.id, friendId.id)).map {
            it.updateDelete()
            friendShipJpaRepository.save(it)
            userId
        }.orElse(null)

    override fun block(userId: UserId, friendId: UserId): UserId? =
        friendShipJpaRepository.findById(FriendShipId.of(userId, friendId)).map {
            it.updateBlock()
            friendShipJpaRepository.save(it)
            userId
        }.orElse(null)

    override fun unblock(userId: UserId, friendId: UserId): UserId? =
        friendShipJpaRepository.findById(FriendShipId.of(userId, friendId)).map {
            it.updateUnBlock()
            friendShipJpaRepository.save(it)
            userId
        }.orElse(null)

    override fun blocked(userId: UserId, friendId: UserId): UserId? =
        friendShipJpaRepository.findById(FriendShipId.of(userId, friendId)).map {
            it.updateBlocked()
            friendShipJpaRepository.save(it)
            userId
        }.orElse(null)

    override fun read(userId: UserId, friendId: UserId): FriendShip? =
        friendShipJpaRepository.findById(FriendShipId.of(userId, friendId))
            .orElse(null)?.toFriendShip()

    override fun readsFavorite(userId: UserId): List<FriendShip> =
        friendShipJpaRepository.findAllByIdUserIdAndFavorite(userId.id, true).map { it.toFriendShip() }

    override fun updateFavorite(userId: UserId, friendId: UserId, favorite: Boolean): UserId? =
        friendShipJpaRepository.findById(FriendShipId.of(userId, friendId)).map {
            it.updateFavorite(favorite)
            friendShipJpaRepository.save(it)
            userId
        }.orElse(null)

    override fun updateName(userId: UserId, friendId: UserId, friendName: String): UserId? =
        friendShipJpaRepository.findById(FriendShipId.of(userId, friendId)).map {
            it.updateName(friendName)
            friendShipJpaRepository.save(it)
            userId
        }.orElse(null)

    override fun allowedFriend(userId: UserId, friendId: UserId, friendName: String): UserId? =
        friendShipJpaRepository.findById(FriendShipId.of(userId, friendId)).map {
            it.allowedFriend()
            friendShipJpaRepository.save(it)
            userId
        }.orElse(null)
}
