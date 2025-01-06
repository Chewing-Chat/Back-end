package org.chewing.v1.jparepository.feed

import org.chewing.v1.jpaentity.feed.FeedVisibilityEntity
import org.chewing.v1.jpaentity.feed.FeedVisibilityId
import org.springframework.data.jpa.repository.JpaRepository

internal interface FeedVisibilityJpaRepository : JpaRepository<FeedVisibilityEntity, FeedVisibilityId> {
    fun findAllByIdIn(feedVisibilityIds: List<FeedVisibilityId>): List<FeedVisibilityEntity>
    fun deleteAllByIdFeedIdIn(feedIds: List<String>)
}
