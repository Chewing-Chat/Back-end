package org.chewing.v1.jparepository.feed

import org.chewing.v1.jpaentity.feed.FeedJpaEntity
import org.chewing.v1.model.feed.FeedStatus
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

internal interface FeedJpaRepository : JpaRepository<FeedJpaEntity, String> {
    fun findAllByUserId(userId: String): List<FeedJpaEntity>
    fun findAllByUserIdAndStatus(userId: String, status: FeedStatus, sort: Sort): List<FeedJpaEntity>
    fun existsByFeedIdInAndUserId(feedIds: List<String>, userId: String): Boolean
    fun findByFeedIdIn(feedIds: List<String>): List<FeedJpaEntity>
    fun findByFeedIdAndStatus(feedId: String, status: FeedStatus): FeedJpaEntity?
    fun findAllByUserIdInAndCreatedAtAfter(userIds: List<String>, createdAt: LocalDateTime, sort: Sort): List<FeedJpaEntity>
}
