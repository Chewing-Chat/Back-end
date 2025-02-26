package org.chewing.v1.jparepository.feed

import org.chewing.v1.jpaentity.feed.FeedDetailJpaEntity
import org.chewing.v1.model.feed.FeedStatus
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository

internal interface FeedDetailJpaRepository : JpaRepository<FeedDetailJpaEntity, String> {
    fun findAllByFeedIdAndStatus(feedId: String, status: FeedStatus, sort: Sort): List<FeedDetailJpaEntity>
    fun findAllByFeedIdInAndStatus(feedIds: List<String>, status: FeedStatus, sort: Sort): List<FeedDetailJpaEntity>
    fun findByFeedIdInAndStatus(
        feedId: List<String>,
        status: FeedStatus,
        sort: Sort,
    ): List<FeedDetailJpaEntity>
}
