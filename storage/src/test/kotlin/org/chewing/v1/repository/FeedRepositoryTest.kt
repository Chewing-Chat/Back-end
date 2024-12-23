package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jparepository.feed.FeedJpaRepository
import org.chewing.v1.model.feed.FeedStatus
import org.chewing.v1.model.feed.FeedTarget
import org.chewing.v1.repository.jpa.feed.FeedRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
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
        val topic = "topic"
        val result = feedRepositoryImpl.append(userId, topic)
        assert(result.isNotEmpty())
    }

    @Test
    fun `피드를 조회해야 한다`() {
        val userId = generateUserId()
        val feedInfo = jpaDataGenerator.feedEntityData(userId)
        val result = feedRepositoryImpl.read(feedInfo.feedId)
        assert(result != null)
        assert(result!!.topic == feedInfo.topic)
    }

    @Test
    fun `피드목록을 조회해야 한다`() {
        val userId = generateUserId()
        val feedInfoList = jpaDataGenerator.feedEntityDataList(userId)
        val result = feedRepositoryImpl.reads(feedInfoList.map { it.feedId })
        assert(result.isNotEmpty())
        assert(result.size == feedInfoList.size)
        assert(result.map { it.uploadAt } == result.map { it.uploadAt }.sorted())
    }

    @Test
    fun `피드를 삭제해야 한다`() {
        val userId = generateUserId()
        val feedInfoList = jpaDataGenerator.feedEntityDataList(userId)
        feedRepositoryImpl.removes(feedInfoList.map { it.feedId })
        val result = feedJpaRepository.findAllById(feedInfoList.map { it.feedId })
        assert(result.isEmpty())
    }

    @Test
    fun `소유자의 피드를 삭제해야 한다`() {
        val userId = generateUserId()
        val feedInfoList = jpaDataGenerator.feedEntityDataList(userId)
        feedRepositoryImpl.removesOwned(userId)
        val result = feedJpaRepository.findAllById(feedInfoList.map { it.feedId })
        assert(result.isEmpty())
    }

    @Test
    fun `피드 좋아요를 추가해야 한다`() {
        val userId = generateUserId()
        val feedInfo = jpaDataGenerator.feedEntityData(userId)
        feedRepositoryImpl.update(feedInfo.feedId, FeedTarget.LIKES)
        val result = feedRepositoryImpl.read(feedInfo.feedId)
        assert(result!!.likes == feedInfo.likes + 1)
    }

    @Test
    fun `피드 좋아요를 취소해야 한다`() {
        val userId = generateUserId()
        val feedInfo = jpaDataGenerator.feedEntityData(userId)
        feedRepositoryImpl.update(feedInfo.feedId, FeedTarget.LIKES)
        feedRepositoryImpl.update(feedInfo.feedId, FeedTarget.UNLIKES)
        val result = feedJpaRepository.findById(feedInfo.feedId).orElse(null).toFeedInfo()
        assert(result.likes == feedInfo.likes)
    }

    @Test
    fun `피드 댓글을 추가해야 한다`() {
        val userId = generateUserId()
        val feedInfo = jpaDataGenerator.feedEntityData(userId)
        feedRepositoryImpl.update(feedInfo.feedId, FeedTarget.COMMENTS)
        val result = feedJpaRepository.findById(feedInfo.feedId).orElse(null).toFeedInfo()
        assert(result.comments == feedInfo.comments + 1)
    }

    @Test
    fun `피드 댓글을 취소해야 한다`() {
        val userId = generateUserId()
        val feedInfo = jpaDataGenerator.feedEntityData(userId)
        feedRepositoryImpl.update(feedInfo.feedId, FeedTarget.COMMENTS)
        feedRepositoryImpl.update(feedInfo.feedId, FeedTarget.UNCOMMENTS)
        val result = feedJpaRepository.findById(feedInfo.feedId).orElse(null).toFeedInfo()
        assert(result.comments == feedInfo.comments)
    }

    @Test
    fun `피드 숨김을 해제해야 한다`() {
        val userId = generateUserId()
        val feedInfo = jpaDataGenerator.feedEntityData(userId)
        feedRepositoryImpl.update(feedInfo.feedId, FeedTarget.HIDE)
        feedRepositoryImpl.update(feedInfo.feedId, FeedTarget.UNHIDE)
        val result = feedJpaRepository.findAllByUserIdAndHideFalseOrderByCreatedAtAsc(userId)
        assert(result.isNotEmpty())
        assert(result.size == 1)
    }

    @Test
    fun `피드 숨김을 해야 한다`() {
        val userId = generateUserId()
        val feedInfo = jpaDataGenerator.feedEntityData(userId)
        feedRepositoryImpl.update(feedInfo.feedId, FeedTarget.HIDE)
        val result = feedJpaRepository.findAllByUserIdAndHideTrueOrderByCreatedAtAsc(userId)
        assert(result.isNotEmpty())
        assert(result.size == 1)
    }

    @Test
    fun `소유자의 모든 피드를 조회해야 한다`() {
        val userId = generateUserId()
        val feedInfoList = jpaDataGenerator.feedEntityDataList(userId)
        val result = feedRepositoryImpl.readsOwned(userId, FeedStatus.ALL)
        assert(result.isNotEmpty())
        assert(result.size == feedInfoList.size)
    }

    @Test
    fun `소유자의 숨김 피드를 조회해야 한다`() {
        val userId = generateUserId()
        val feedInfoList = jpaDataGenerator.feedEntityDataList(userId)
        feedRepositoryImpl.update(feedInfoList[0].feedId, FeedTarget.HIDE)
        val result = feedRepositoryImpl.readsOwned(userId, FeedStatus.HIDDEN)
        assert(result.isNotEmpty())
        assert(result.size == 1)
    }

    @Test
    fun `소유자의 숨김하지 않은 피드를 조회해야 한다`() {
        val userId = generateUserId()
        val feedInfoList = jpaDataGenerator.feedEntityDataList(userId)
        feedRepositoryImpl.update(feedInfoList[0].feedId, FeedTarget.HIDE)
        val result = feedRepositoryImpl.readsOwned(userId, FeedStatus.NOT_HIDDEN)
        assert(result.isNotEmpty())
        assert(result.size == feedInfoList.size - 1)
    }

    private fun generateUserId() = UUID.randomUUID().toString()
}
