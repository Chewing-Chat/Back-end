package org.chewing.v1.jparepository.feed

import org.chewing.v1.jpaentity.user.FeedLikeId
import org.chewing.v1.jpaentity.user.UserFeedLikesJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface FeedLikesJpaRepository : JpaRepository<UserFeedLikesJpaEntity, FeedLikeId> {
    fun deleteAllByFeedLikeIdFeedIdIn(feedIds: List<String>)
}
