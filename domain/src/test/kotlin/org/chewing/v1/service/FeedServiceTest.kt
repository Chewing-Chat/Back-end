package org.chewing.v1.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.chewing.v1.TestDataFactory
import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.implementation.feed.FeedAppender
import org.chewing.v1.implementation.feed.FeedEnricher
import org.chewing.v1.implementation.feed.FeedReader
import org.chewing.v1.implementation.feed.FeedRemover
import org.chewing.v1.implementation.feed.FeedValidator
import org.chewing.v1.implementation.media.FileHandler
import org.chewing.v1.model.feed.FeedType
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.repository.feed.FeedDetailRepository
import org.chewing.v1.repository.feed.FeedRepository
import org.chewing.v1.repository.feed.FeedVisibilityRepository
import org.chewing.v1.service.feed.FeedService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class FeedServiceTest {
    private val feedRepository: FeedRepository = mockk()
    private val feedDetailRepository: FeedDetailRepository = mockk()
    private val fileHandler: FileHandler = mockk()
    private val feedVisibilityRepository: FeedVisibilityRepository = mockk()

    private val feedReader: FeedReader = FeedReader(feedRepository, feedDetailRepository, feedVisibilityRepository)
    private val feedAppender: FeedAppender =
        FeedAppender(feedRepository, feedDetailRepository, feedVisibilityRepository)
    private val feedValidator: FeedValidator = FeedValidator(feedRepository, feedVisibilityRepository)
    private val feedEnricher: FeedEnricher = FeedEnricher()
    private val feedRemover: FeedRemover = FeedRemover(feedRepository, feedDetailRepository, feedVisibilityRepository)
    private val feedService: FeedService =
        FeedService(feedReader, feedAppender, feedValidator, fileHandler, feedEnricher, feedRemover)

    @Test
    fun `피드를 가져온다`() {
        val feedId = TestDataFactory.createFeedId()
        val userId = TestDataFactory.createUserId()
        val feedDetailId = TestDataFactory.createFeedDetailId()
        val feed = TestDataFactory.createFeedInfo(feedId, userId)
        val feedDetail = TestDataFactory.createFeedDetail(feedId, feedDetailId, 0)

        every { feedRepository.read(feedId) } returns feed
        every { feedDetailRepository.read(feedId) } returns listOf(feedDetail)
        every { feedVisibilityRepository.isVisible(feedId, userId) } returns true

        val result = feedService.getFeed(feedId, userId)

        assert(result.feed.feedId == feedId)
        assert(result.feedDetails.size == 1)
        assert(result.feedDetails[0].feedDetailId == feedDetailId)
        assert(result.feedDetails[0].feedId == feedId)
        assert(result.feed.userId == userId)
    }

    @Test
    fun `피드를 가져온다 - 피드가 존재 하지 않음`() {
        val feedId = TestDataFactory.createFeedId()
        val userId = TestDataFactory.createUserId()

        every { feedRepository.read(feedId) } returns null
        every { feedVisibilityRepository.isVisible(feedId, userId) } returns true

        val exception = assertThrows<NotFoundException> {
            feedService.getFeed(feedId, userId)
        }

        assert(exception.errorCode == ErrorCode.FEED_NOT_FOUND)
    }

    @Test
    fun `피드를 가져온다 - 공개 되지 않은 피드`() {
        val feedId = TestDataFactory.createFeedId()
        val userId = TestDataFactory.createUserId()

        every { feedRepository.read(feedId) } returns null
        every { feedVisibilityRepository.isVisible(feedId, userId) } returns false

        val exception = assertThrows<ConflictException> {
            feedService.getFeed(feedId, userId)
        }

        assert(exception.errorCode == ErrorCode.FEED_IS_NOT_VISIBLE)
    }

    @Test
    fun `자신의 피드들을 가져온다`() {
        // given
        val userId = TestDataFactory.createUserId()
        val feedId1 = TestDataFactory.createFeedId()
        val feedId2 = TestDataFactory.createSecondFeedId()
        val feedIds = listOf(feedId1, feedId2)
        val feedDetailId = TestDataFactory.createFeedDetailId()
        val feedDetailId2 = TestDataFactory.createSecondFeedDetailId()
        val feedDetailId3 = TestDataFactory.createThirdFeedDetailId()
        val feedDetailId4 = TestDataFactory.createFourthFeedDetailId()
        val feedDetailsByFeedId = mapOf(
            feedId1 to listOf(
                TestDataFactory.createFeedDetail(feedId1, feedDetailId, 0),
                TestDataFactory.createFeedDetail(feedId1, feedDetailId2, 1),
            ),
            feedId2 to listOf(
                TestDataFactory.createFeedDetail(feedId2, feedDetailId3, 0),
                TestDataFactory.createFeedDetail(feedId2, feedDetailId4, 1),
            ),
        )
        val feeds = feedIds.map { TestDataFactory.createFeedInfo(it, userId) }
        val allFeedDetails = feedDetailsByFeedId.values.flatten()

        every { feedRepository.reads(userId) } returns feeds
        every { feedDetailRepository.readsFirstIndex(feedIds) } returns allFeedDetails
        every { feedVisibilityRepository.readVisibleFeedIds(userId, feedIds) } returns feedIds

        // when
        val result = feedService.getFeeds(userId, userId)

        // then
        assert(result.size == feedIds.size)
        feedIds.forEachIndexed { index, feedId ->
            val feedResult = result[index]
            val expectedFeedDetails = feedDetailsByFeedId[feedId]!!

            assert(feedResult.feed.feedId == feedId)
            assert(feedResult.feedDetails.size == expectedFeedDetails.size)
            expectedFeedDetails.forEachIndexed { detailIndex, expectedDetail ->
                assert(feedResult.feedDetails[detailIndex].feedDetailId == expectedDetail.feedDetailId)
            }
        }
    }

    @Test
    fun `친구의 피드들을 가져온다`() {
        // given
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val feedId = TestDataFactory.createFeedId()
        val feedIds = listOf(feedId)
        val feedDetailId = TestDataFactory.createFeedDetailId()
        val feedDetailId2 = TestDataFactory.createSecondFeedDetailId()
        val visibleFeedIds = listOf(feedId)
        val feedDetailsByFeedId = mapOf(
            feedId to listOf(
                TestDataFactory.createFeedDetail(feedId, feedDetailId, 0),
                TestDataFactory.createFeedDetail(feedId, feedDetailId2, 1),
            ),
        )
        val feeds = feedIds.map { TestDataFactory.createFeedInfo(it, userId) }
        val visibleFeedDetails = feedDetailsByFeedId.values.flatten()

        every { feedRepository.reads(friendId) } returns feeds
        every { feedVisibilityRepository.readVisibleFeedIds(userId, feedIds) } returns visibleFeedIds
        every { feedDetailRepository.readsFirstIndex(visibleFeedIds) } returns visibleFeedDetails

        // when
        val result = feedService.getFeeds(userId, friendId)

        // then
        assert(result.size == visibleFeedIds.size)
        visibleFeedIds.forEachIndexed { index, feedId ->
            val feedResult = result[index]
            val expectedFeedDetails = feedDetailsByFeedId[feedId]!!

            assert(feedResult.feed.feedId == feedId)
            assert(feedResult.feedDetails.size == expectedFeedDetails.size)
            expectedFeedDetails.forEachIndexed { detailIndex, expectedDetail ->
                assert(feedResult.feedDetails[detailIndex].feedDetailId == expectedDetail.feedDetailId)
            }
        }
    }

    @Test
    fun `피드들을 삭제에 성공한다`() {
        val userId = TestDataFactory.createUserId()
        val feedIds = listOf(TestDataFactory.createFeedId())

        every { feedRepository.isOwners(feedIds, userId) } returns true
        every { feedRepository.removes(feedIds) } just Runs
        every { feedDetailRepository.removes(feedIds) } returns listOf()
        every { fileHandler.handleOldFiles(any()) } just Runs
        every { feedVisibilityRepository.removes(feedIds) } just Runs

        assertDoesNotThrow { feedService.removes(userId, feedIds) }
    }

    @Test
    fun `피드들 삭제 에 실패 - 잘봇된 접근 본인 소유의 피드가 아님`() {
        val userId = TestDataFactory.createUserId()
        val feedIds = listOf(TestDataFactory.createFeedId())

        every { feedRepository.isOwners(feedIds, userId) } returns false
        every { feedVisibilityRepository.removes(feedIds) } just Runs

        assertThrows<ConflictException> {
            feedService.removes(userId, feedIds)
        }
    }

    @Test
    fun `피드를 추가한다`() {
        val userId = TestDataFactory.createUserId()
        val topic = "topic"
        val feedId = TestDataFactory.createFeedId()
        val fileData = TestDataFactory.createFileData()
        val media = TestDataFactory.createProfileMedia()
        val visibleFriendIds = listOf(TestDataFactory.createFriendId())

        every { feedRepository.append(userId, topic, FeedType.FILE) } returns feedId
        every { feedDetailRepository.append(listOf(media), feedId) } just Runs
        every { feedVisibilityRepository.append(feedId, visibleFriendIds.plus(userId)) } just Runs
        every { fileHandler.handleNewFiles(userId, listOf(fileData), FileCategory.FEED) } returns listOf(media)

        val result =
            feedService.makeFile(userId, visibleFriendIds, listOf(fileData), topic, FileCategory.FEED, FeedType.FILE)
        assert(result == feedId)
    }
}
