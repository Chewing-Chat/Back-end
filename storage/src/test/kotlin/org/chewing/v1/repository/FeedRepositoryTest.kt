package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jparepository.feed.FeedJpaRepository
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
        val result = feedRepositoryImpl.reads(userId)
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

    private fun generateUserId() = UUID.randomUUID().toString()
}
