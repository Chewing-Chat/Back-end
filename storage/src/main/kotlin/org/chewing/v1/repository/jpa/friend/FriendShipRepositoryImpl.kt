package org.chewing.v1.repository.jpa.friend

import org.chewing.v1.jpaentity.friend.FriendShipId
import org.chewing.v1.jpaentity.friend.FriendShipJpaEntity
import org.chewing.v1.jparepository.friend.FriendShipJpaRepository
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.friend.FriendSortCriteria
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserName
import org.chewing.v1.repository.friend.FriendShipRepository
import org.springframework.stereotype.Repository

@Repository
internal class FriendShipRepositoryImpl(
    private val friendShipJpaRepository: FriendShipJpaRepository,
) : FriendShipRepository {
    override fun readsAccess(userId: String, accessStatus: AccessStatus, sort: FriendSortCriteria): List<FriendShip> =
        when (sort) {
            FriendSortCriteria.NAME ->
                friendShipJpaRepository
                    .findAllByIdUserIdAndTypeOrderByFirstNameAscLastNameAsc(userId, accessStatus)
                    .map { it.toFriendShip() }

            FriendSortCriteria.FAVORITE ->
                friendShipJpaRepository
                    .findAllByIdUserIdAndTypeOrderByFavoriteAscFirstNameAscLastNameAsc(userId, accessStatus)
                    .map { it.toFriendShip() }
        }

    override fun reads(
        friendIds: List<String>,
        userId: String,
        accessStatus: AccessStatus,
    ): List<FriendShip> =
        friendShipJpaRepository.findAllByIdInAndType(friendIds.map { FriendShipId(userId, it) }, accessStatus)
            .map { it.toFriendShip() }

    override fun append(userId: String, targetUserId: String, targetUserName: UserName) {
        friendShipJpaRepository.save(FriendShipJpaEntity.generate(userId, targetUserId, targetUserName))
    }

    override fun remove(userId: String, friendId: String): String? =
        friendShipJpaRepository.findById(FriendShipId(userId, friendId)).map {
            it.updateDelete()
            friendShipJpaRepository.save(it)
            userId
        }.orElse(null)

    override fun block(userId: String, friendId: String): String? =
        friendShipJpaRepository.findById(FriendShipId(userId, friendId)).map {
            it.updateBlock()
            friendShipJpaRepository.save(it)
            userId
        }.orElse(null)

    override fun blocked(userId: String, friendId: String): String? =
        friendShipJpaRepository.findById(FriendShipId(userId, friendId)).map {
            it.updateBlocked()
            friendShipJpaRepository.save(it)
            userId
        }.orElse(null)

    override fun read(userId: String, friendId: String): FriendShip? =
        friendShipJpaRepository.findById(FriendShipId(userId, friendId))
            .orElse(null)?.toFriendShip()

    override fun updateFavorite(userId: String, friendId: String, favorite: Boolean): String? =
        friendShipJpaRepository.findById(FriendShipId(userId, friendId)).map {
            it.updateFavorite(favorite)
            friendShipJpaRepository.save(it)
            userId
        }.orElse(null)

    override fun updateName(userId: String, friendId: String, friendName: UserName): String? =
        friendShipJpaRepository.findById(FriendShipId(userId, friendId)).map {
            it.updateName(friendName)
            friendShipJpaRepository.save(it)
            userId
        }.orElse(null)
}
