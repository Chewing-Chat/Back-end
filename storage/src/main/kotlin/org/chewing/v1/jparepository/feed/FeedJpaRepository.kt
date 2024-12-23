package org.chewing.v1.jparepository.feed

import org.chewing.v1.jpaentity.feed.FeedJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

internal interface FeedJpaRepository : JpaRepository<FeedJpaEntity, String> {
    fun findAllByUserIdAndHideTrueOrderByCreatedAtAsc(userId: String): List<FeedJpaEntity>
    fun findAllByUserIdAndHideFalseOrderByCreatedAtAsc(userId: String): List<FeedJpaEntity>
    fun findAllByUserIdOrderByCreatedAtAsc(userId: String): List<FeedJpaEntity>
    fun deleteAllByUserId(userId: String)
    fun findAllByFeedIdInOrderByCreatedAtAsc(feedIds: List<String>): List<FeedJpaEntity>

    fun findAllByUserIdAndHideTrueAndCreatedAtAfterOrderByCreatedAtAsc(userId: String, createdAt: LocalDateTime): List<FeedJpaEntity>
}
