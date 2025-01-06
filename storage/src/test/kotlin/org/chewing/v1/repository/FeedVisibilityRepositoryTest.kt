package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jpaentity.feed.FeedVisibilityId
import org.chewing.v1.jparepository.feed.FeedVisibilityJpaRepository
import org.chewing.v1.repository.jpa.feed.FeedVisibilityRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

internal class FeedVisibilityRepositoryTest : JpaContextTest() {
    @Autowired
    private lateinit var feedVisibilityJpaRepository: FeedVisibilityJpaRepository

    @Autowired
    private lateinit var jpaDataGenerator: JpaDataGenerator

    @Autowired
    private lateinit var feedVisibilityRepositoryImpl: FeedVisibilityRepositoryImpl

    @Test
    fun `피드의 공개범위를 추가해야 한다`() {
        val feedId = generateFeedId()
        val targetUserIds = listOf(generateUserId(), generateUserId())
        feedVisibilityRepositoryImpl.append(feedId, targetUserIds)
        val result1 = feedVisibilityJpaRepository.findById(FeedVisibilityId(feedId, targetUserIds[0]))
        val result2 = feedVisibilityJpaRepository.findById(FeedVisibilityId(feedId, targetUserIds[1]))
        assert(result1.isPresent)
        assert(result2.isPresent)
    }

    @Test
    fun `피드의 공개범위를 확인해야 한다`() {
        val feedId = generateFeedId()
        val targetUserIds = listOf(generateUserId(), generateUserId())
        val wrongUserId = generateUserId()
        val wrongFeedId = generateFeedId()
        jpaDataGenerator.feedVisibilityEntityDataList(feedId, targetUserIds)
        val result1 = feedVisibilityRepositoryImpl.isVisible(feedId, targetUserIds[0])
        val result2 = feedVisibilityRepositoryImpl.isVisible(feedId, targetUserIds[1])
        val result3 = feedVisibilityRepositoryImpl.isVisible(feedId, wrongUserId)
        val result4 = feedVisibilityRepositoryImpl.isVisible(wrongFeedId, targetUserIds[1])
        assert(result1 == true)
        assert(result2 == true)
        assert(result3 == false)
        assert(result4 == false)
    }

    @Test
    fun `피드 공개범위에 해당하는 피드 아이디를 가져와야 한다`() {
        val feedIds = listOf(generateFeedId(), generateFeedId())
        val wrongFeedIds = feedIds.plus(generateFeedId())
        val userId = generateUserId()
        feedIds.forEach {
            jpaDataGenerator.feedVisibilityEntityData(it, userId)
        }
        val result = feedVisibilityRepositoryImpl.readVisibleFeedIds(userId, wrongFeedIds)

        assert(result.size == feedIds.size)
        assert(result.containsAll(feedIds))
    }

    fun generateFeedId(): String {
        return UUID.randomUUID().toString()
    }

    fun generateUserId(): String {
        return UUID.randomUUID().toString()
    }

    @Test
    fun `피드 공개범위를 삭제해야 한다`() {
        val feedIds = listOf(generateFeedId(), generateFeedId())
        val userIds = listOf(generateUserId(), generateUserId())
        feedIds.forEach {
            jpaDataGenerator.feedVisibilityEntityDataList(it, userIds)
        }
        feedVisibilityRepositoryImpl.removes(feedIds)
        feedIds.forEach {
            userIds.forEach {
                val result = feedVisibilityJpaRepository.findById(FeedVisibilityId(it, it))
                assert(result.isEmpty)
            }
        }
    }
}
