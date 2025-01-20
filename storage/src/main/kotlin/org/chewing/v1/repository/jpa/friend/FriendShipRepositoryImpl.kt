package org.chewing.v1.repository.jpa.friend

import org.chewing.v1.jpaentity.friend.FriendShipId
import org.chewing.v1.jpaentity.friend.FriendShipJpaEntity
import org.chewing.v1.jparepository.friend.FriendShipJpaRepository
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.friend.FriendSortCriteria
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.friend.FriendShipRepository
import org.springframework.stereotype.Repository

@Repository
internal class FriendShipRepositoryImpl(
    private val friendShipJpaRepository: FriendShipJpaRepository,
) : FriendShipRepository {
    override fun readsAccess(userId: UserId, accessStatus: AccessStatus, sort: FriendSortCriteria): List<FriendShip> =
        when (sort) {
            FriendSortCriteria.NAME ->
                friendShipJpaRepository
                    .findAllByIdUserIdAndTypeOrderByName(userId.id, accessStatus)
                    .map { it.toFriendShip() }

            FriendSortCriteria.FAVORITE ->
                friendShipJpaRepository
                    .findAllByIdUserIdAndTypeOrderByFavoriteAscName(userId.id, accessStatus)
                    .map { it.toFriendShip() }
        }

    override fun reads(
        friendIds: List<UserId>,
        userId: UserId,
        accessStatus: AccessStatus,
    ): List<FriendShip> =
        friendShipJpaRepository.findAllByIdInAndType(friendIds.map { FriendShipId.of(userId, it) }, accessStatus)
            .map { it.toFriendShip() }

    override fun append(userId: UserId, targetUserId: UserId, targetUserName: String) {
        friendShipJpaRepository.save(FriendShipJpaEntity.generate(userId, targetUserId, targetUserName))
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
}
