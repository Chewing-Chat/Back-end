package org.chewing.v1.jparepository.friend

import org.chewing.v1.jpaentity.friend.FriendShipId
import org.chewing.v1.jpaentity.friend.FriendShipJpaEntity
import org.chewing.v1.model.user.AccessStatus
import org.springframework.data.jpa.repository.JpaRepository

internal interface FriendShipJpaRepository : JpaRepository<FriendShipJpaEntity, FriendShipId> {

    fun findAllByIdUserIdAndTypeOrderByFirstNameAscLastNameAsc(userId: String, type: AccessStatus): List<FriendShipJpaEntity>
    fun findAllByIdUserIdAndTypeOrderByFavoriteAscFirstNameAscLastNameAsc(userId: String, type: AccessStatus): List<FriendShipJpaEntity>
    fun findAllByIdInAndType(friendShipIds: List<FriendShipId>, type: AccessStatus): List<FriendShipJpaEntity>
    fun findByIdAndType(id: FriendShipId, type: AccessStatus): FriendShipJpaEntity?
}
