package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jparepository.feed.FeedDetailJpaRepository
import org.chewing.v1.repository.jpa.feed.FeedDetailRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
import org.chewing.v1.repository.support.MediaProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class FeedDetailRepositoryTest : JpaContextTest() {
    @Autowired
    private lateinit var feedDetailJpaRepository: FeedDetailJpaRepository

    @Autowired
    private lateinit var jpaDataGenerator: JpaDataGenerator

    @Autowired
    private lateinit var feedDetailRepositoryImpl: FeedDetailRepositoryImpl

    @Test
    fun `피드 상세를 추가해야 한다`() {
        val feedId = generateFeedId()
        val medias = MediaProvider.buildFeedContents()
        feedDetailRepositoryImpl.append(medias, feedId)
        val result = feedDetailJpaRepository.findAllByFeedIdOrderByFeedIndex(feedId)
        assert(result.isNotEmpty())
        assert(result.size == medias.size)
    }

    @Test
    fun `피드 상세를 Index 기준으로 순서대로 조회해야 한다`() {
        val feedId = generateFeedId()
        val feedDetails = jpaDataGenerator.feedDetailEntityDataAsc(feedId)
        val result = feedDetailRepositoryImpl.read(feedId)
        assert(result.isNotEmpty())
        assert(result.size == feedDetails.size)
        result.forEachIndexed { index, feedDetail ->
            assert(feedDetail.media.index == index)
        }
    }

    @Test
    fun `피드 상세를 Index 기준으로 첫번째만 조회해야 한다`() {
        val feedIds = generateFeedIdList()
        feedIds.forEach { feedId ->
            jpaDataGenerator.feedDetailEntityDataAsc(feedId)
        }
        val result = feedDetailRepositoryImpl.readsFirstIndex(feedIds)
        assert(result.isNotEmpty())
        assert(result.size == feedIds.size)
        result.forEach { feedDetail ->
            assert(feedDetail.media.index == 0)
        }
    }

    @Test
    fun `피드 상세를 삭제해야 한다`() {
        val feedIds = generateFeedIdList()
        val feedDetails = feedIds.map { feedId ->
            jpaDataGenerator.feedDetailEntityDataAsc(feedId)
        }.flatten()
        val result = feedDetailRepositoryImpl.removes(feedIds)
        assert(result.isNotEmpty())
        assert(result.size == feedDetails.size)
        val result2 = feedDetailJpaRepository.findAllByFeedIdIn(feedIds)
        assert(result2.isEmpty())
    }

    private fun generateFeedId() = UUID.randomUUID().toString()
    private fun generateFeedIdList() = listOf(generateFeedId(), generateFeedId())
}
