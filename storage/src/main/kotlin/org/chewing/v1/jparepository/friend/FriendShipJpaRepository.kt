package org.chewing.v1.jparepository.friend

import org.chewing.v1.jpaentity.friend.FriendShipId
import org.chewing.v1.jpaentity.friend.FriendShipJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface FriendShipJpaRepository : JpaRepository<FriendShipJpaEntity, FriendShipId> {

    fun findAllByIdUserIdOrderByName(userId: String): List<FriendShipJpaEntity>
    fun findAllByIdUserIdOrderByFavoriteAscName(userId: String): List<FriendShipJpaEntity>
}
