package org.chewing.v1.repository.jpa.user

import org.chewing.v1.jpaentity.friend.UserSearchJpaEntity
import org.chewing.v1.jparepository.user.UserSearchJpaRepository
import org.chewing.v1.model.friend.UserSearch
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.user.UserSearchRepository
import org.springframework.stereotype.Repository

@Repository
internal class UserSearchRepositoryImpl(
    private val userSearchJpaRepository: UserSearchJpaRepository,
) : UserSearchRepository {
    override fun appendHistory(userId: UserId, keyword: String) {
        userSearchJpaRepository.save(UserSearchJpaEntity.fromFriendSearch(userId, keyword))
    }

    override fun readSearchHistory(userId: UserId): List<UserSearch> = userSearchJpaRepository.findAllByUserIdOrderByCreatedAt(userId.id).map {
        it.toFriendSearch()
    }
}
