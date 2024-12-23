package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jpaentity.user.FeedLikeId
import org.chewing.v1.jparepository.feed.FeedLikesJpaRepository
import org.chewing.v1.repository.jpa.feed.FeedLikesRepositoryImpl
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

internal class FeedLikesRepositoryTest : JpaContextTest() {
    @Autowired
    private lateinit var feedLikesJpaRepository: FeedLikesJpaRepository

    @Autowired
    private lateinit var feedLikesRepositoryImpl: FeedLikesRepositoryImpl

    @Test
    fun `좋아요를 추가해야 한다`() {
        val feedId = generateFeedId()
        val userId = generateUserId()
        feedLikesRepositoryImpl.likes(feedId, userId)
        val result = feedLikesJpaRepository.findById(FeedLikeId(userId, feedId))
        assert(result.isPresent)
    }

    @Test
    fun `좋아요를 취소해야 한다`() {
        val feedId = generateFeedId()
        val userId = generateUserId()
        feedLikesRepositoryImpl.likes(feedId, userId)
        feedLikesRepositoryImpl.unlikes(feedId, userId)
        val result = feedLikesJpaRepository.findById(FeedLikeId(userId, feedId))
        assert(!result.isPresent)
    }

    @Test
    fun `좋아요를 확인해야 한다`() {
        val feedId = generateFeedId()
        val userId = generateUserId()
        feedLikesRepositoryImpl.likes(feedId, userId)
        assert(feedLikesRepositoryImpl.checkLike(feedId, userId))
    }

    @Test
    fun `좋아요를 확인하지 않아야 한다`() {
        val feedId = generateFeedId()
        val userId = generateUserId()
        assert(!feedLikesRepositoryImpl.checkLike(feedId, userId))
    }

    @Test
    fun `좋아요를 모두 취소해야 한다`() {
        val feedId = generateFeedId()
        val userId = generateUserId()
        feedLikesRepositoryImpl.likes(feedId, userId)
        feedLikesRepositoryImpl.unlikeAll(listOf(feedId))
        val result = feedLikesJpaRepository.findById(FeedLikeId(userId, feedId))
        assert(!result.isPresent)
    }

    private fun generateFeedId(): String = UUID.randomUUID().toString()
    private fun generateUserId(): String = UUID.randomUUID().toString()
}
