package org.chewing.v1.jparepository.feed

import org.chewing.v1.jpaentity.feed.FeedDetailJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface FeedDetailJpaRepository : JpaRepository<FeedDetailJpaEntity, String> {
    fun findAllByFeedIdOrderByFeedIndex(feedId: String): List<FeedDetailJpaEntity>
    fun findAllByFeedIdIn(feedIds: List<String>): List<FeedDetailJpaEntity>
    fun findByFeedIdInAndFeedIndex(feedId: List<String>, feedIndex: Int): List<FeedDetailJpaEntity>
    fun deleteAllByFeedIdIn(feedIds: List<String>)
    fun findAllByFeedIdInOrderByFeedIndexAsc(feedIds: List<String>): List<FeedDetailJpaEntity>
}
