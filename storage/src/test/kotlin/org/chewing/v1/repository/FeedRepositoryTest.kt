package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jparepository.feed.FeedJpaRepository
import org.chewing.v1.model.feed.FeedStatus
import org.chewing.v1.model.feed.FeedType
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.jpa.feed.FeedRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
import org.chewing.v1.util.SortType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

internal class FeedRepositoryTest : JpaContextTest() {
    @Autowired
    private lateinit var feedJpaRepository: FeedJpaRepository

    @Autowired
    private lateinit var jpaDataGenerator: JpaDataGenerator

    @Autowired
    private lateinit var feedRepositoryImpl: FeedRepositoryImpl

    @Test
    fun `피드를 추가해야 한다`() {
        val userId = generateUserId()
        val content = "content"
        val result = feedRepositoryImpl.append(userId, content, FeedType.FILE)
        assert(result.id.isNotEmpty())
    }

    @Test
    fun `피드를 조회해야 한다`() {
        val userId = generateUserId()
        val feedInfo = jpaDataGenerator.feedEntityData(userId)
        val result = feedRepositoryImpl.read(feedInfo.feedId)
        assert(result != null)
        assert(result!!.content == feedInfo.content)
    }

    @Test
    fun `피드목록을 최신순으로 조회해야 한다`() {
        val userId = generateUserId()
        val feedInfoList = jpaDataGenerator.feedEntityDataList(userId)
        val result = feedRepositoryImpl.reads(userId)
        val sortedResult = result.sortedByDescending { it.uploadAt }
        assert(result.isNotEmpty())
        assert(result.size == feedInfoList.size)
        assert(result.map { it.uploadAt } == sortedResult.map { it.uploadAt })
    }

    @Test
    fun `피드를 삭제해야 한다`() {
        val userId = generateUserId()
        val feedInfoList = jpaDataGenerator.feedEntityDataList(userId)
        feedRepositoryImpl.removes(feedInfoList.map { it.feedId })
        val result = feedJpaRepository.findAllByUserIdAndStatus(userId.id, FeedStatus.ACTIVE, SortType.LATEST.toSort())
        assert(result.isEmpty())
    }

    @Test
    fun `소유자의 피드를 삭제해야 한다`() {
        val userId = generateUserId()
        jpaDataGenerator.feedEntityDataList(userId)
        feedRepositoryImpl.removesOwned(userId)
        val result = feedJpaRepository.findAllByUserIdAndStatus(userId.id, FeedStatus.ACTIVE, SortType.LATEST.toSort())
        assert(result.isEmpty())
    }

    @Test
    fun `피드의 소유자라면 true를 반환해야 한다`() {
        val userId = generateUserId()
        val feedInfoList = jpaDataGenerator.feedEntityDataList(userId)
        val result = feedRepositoryImpl.isOwners(feedInfoList.map { it.feedId }, userId)
        assert(result)
    }

    @Test
    fun `하나라도 소유자가 아니라면 false를 반환해야 한다`() {
        val userId = generateUserId()
        val feedInfoList = jpaDataGenerator.feedEntityDataList(userId)
        val result = feedRepositoryImpl.isOwners(feedInfoList.map { it.feedId }, generateUserId())
        assert(!result)
    }

    @Test
    fun `지난 1일 동안 생성된 피드만 최신순으로 조회해야 한다`() {
        val userId = generateUserId()
        val recentFeed = jpaDataGenerator.feedEntityData(userId)
        val targetUserIds = listOf(userId)

        val result = feedRepositoryImpl.readsOneDay(targetUserIds)

        assert(result.map { it.feedId }.contains(recentFeed.feedId))
    }

    private fun generateUserId() = UserId.of(UUID.randomUUID().toString())
}
