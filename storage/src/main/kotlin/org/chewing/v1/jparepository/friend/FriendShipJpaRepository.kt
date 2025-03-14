package org.chewing.v1.jparepository.friend

import org.chewing.v1.jpaentity.friend.FriendShipId
import org.chewing.v1.jpaentity.friend.FriendShipJpaEntity
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository

internal interface FriendShipJpaRepository : JpaRepository<FriendShipJpaEntity, FriendShipId> {

    fun findAllByIdUserId(userId: String, sort: Sort): List<FriendShipJpaEntity>
    fun findAllByIdUserIdAndFavorite(userId: String, favorite: Boolean): List<FriendShipJpaEntity>
    fun findAllByIdIn(friendShipIds: List<FriendShipId>): List<FriendShipJpaEntity>
}
