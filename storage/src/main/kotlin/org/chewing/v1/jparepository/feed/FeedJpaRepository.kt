package org.chewing.v1.jparepository.feed

import org.chewing.v1.jpaentity.feed.FeedJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

internal interface FeedJpaRepository : JpaRepository<FeedJpaEntity, String> {
    fun deleteAllByUserId(userId: String)
    fun findAllByUserIdOrderByCreatedAtAsc(userId: String): List<FeedJpaEntity>

    fun findAllByUserIdAndCreatedAtAfterOrderByCreatedAtAsc(userId: String, createdAt: LocalDateTime): List<FeedJpaEntity>

    fun existsByFeedIdInAndUserId(feedIds: List<String>, userId: String): Boolean

    fun findAllByUserIdInAndCreatedAtAfterOrderByCreatedAtAsc(userIds: List<String>, createdAt: LocalDateTime): List<FeedJpaEntity>
}
