package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jparepository.feed.FeedDetailJpaRepository
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.feed.FeedStatus
import org.chewing.v1.repository.jpa.feed.FeedDetailRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
import org.chewing.v1.repository.support.MediaProvider
import org.chewing.v1.util.SortType
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
        val result = feedDetailJpaRepository.findAllByFeedIdAndStatus(feedId.id, FeedStatus.ACTIVE, SortType.SMALLEST.toSort())
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
    fun `피드 상세를 피드 리스트 ID들로 조회해야 한다`() {
        val feedIds = generateFeedIdList()
        val feedDetails = feedIds.flatMap { feedId ->
            jpaDataGenerator.feedDetailEntityDataAsc(feedId)
        }
        val result = feedDetailRepositoryImpl.readsDetails(feedIds)
        assert(result.isNotEmpty())
        assert(result.size == feedDetails.size)
    }

    @Test
    fun `피드 상세를 삭제해야 한다`() {
        val feedIds = generateFeedIdList()
        feedIds.map { feedId ->
            jpaDataGenerator.feedDetailEntityDataAsc(feedId)
        }.flatten()
        feedDetailRepositoryImpl.removes(feedIds)
        val result2 = feedDetailJpaRepository.findAllByFeedIdInAndStatus(feedIds.map { it.id }, FeedStatus.ACTIVE, SortType.SMALLEST.toSort())
        assert(result2.isEmpty())
    }

    fun generateFeedId(): FeedId {
        return FeedId.of(UUID.randomUUID().toString())
    }

    private fun generateFeedIdList() = listOf(generateFeedId(), generateFeedId())
}
