package org.chewing.v1.repository.jpa.friend

import org.chewing.v1.jpaentity.friend.FriendShipId
import org.chewing.v1.jpaentity.friend.FriendShipJpaEntity
import org.chewing.v1.jparepository.friend.FriendShipJpaRepository
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.friend.FriendShipStatus
import org.chewing.v1.model.friend.FriendSortCriteria
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.friend.FriendShipRepository
import org.springframework.stereotype.Repository

@Repository
internal class FriendShipRepositoryImpl(
    private val friendShipJpaRepository: FriendShipJpaRepository,
) : FriendShipRepository {
    override fun readsSorted(userId: UserId, sort: FriendSortCriteria): List<FriendShip> =
        when (sort) {
            FriendSortCriteria.NAME ->
                friendShipJpaRepository
                    .findAllByIdUserIdOrderByName(userId.id)
                    .map { it.toFriendShip() }

            FriendSortCriteria.FAVORITE ->
                friendShipJpaRepository
                    .findAllByIdUserIdOrderByFavoriteAscName(userId.id)
                    .map { it.toFriendShip() }
        }

    override fun appendIfNotExist(userId: UserId, targetUserId: UserId, targetUserName: String, status: FriendShipStatus): FriendShip {
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

    override fun updateStatus(userId: UserId, friendId: UserId, friendName: String, status: FriendShipStatus): UserId? =
        friendShipJpaRepository.findById(FriendShipId.of(userId, friendId)).map {
            it.updateStatus(status)
            friendShipJpaRepository.save(it)
            userId
        }.orElse(null)
}
