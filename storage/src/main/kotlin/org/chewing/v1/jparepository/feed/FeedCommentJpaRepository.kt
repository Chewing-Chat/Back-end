package org.chewing.v1.jparepository.feed

import org.chewing.v1.jpaentity.feed.FeedCommentJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface FeedCommentJpaRepository : JpaRepository<FeedCommentJpaEntity, String> {
    fun findAllByUserId(userId: String): List<FeedCommentJpaEntity>
    fun findAllByFeedId(feedId: String): List<FeedCommentJpaEntity>
    fun deleteAllByFeedIdIn(feedIds: List<String>)
    fun findAllByFeedCommentIdIn(feedCommentIds: List<String>): List<FeedCommentJpaEntity>
}
