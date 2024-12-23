package org.chewing.v1.service

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.chewing.v1.TestDataFactory
import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.util.OptimisticLockHandler
import org.chewing.v1.implementation.feed.feed.*
import org.chewing.v1.implementation.media.FileHandler
import org.chewing.v1.model.feed.FeedStatus
import org.chewing.v1.model.feed.FeedTarget
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.repository.feed.FeedDetailRepository
import org.chewing.v1.repository.feed.FeedRepository
import org.chewing.v1.service.feed.FeedService
import org.chewing.v1.util.AsyncJobExecutor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.dao.OptimisticLockingFailureException

class FeedServiceTest {
    private val feedRepository: FeedRepository = mockk()
    private val feedDetailRepository: FeedDetailRepository = mockk()
    private val fileHandler: FileHandler = mockk()

    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private val asyncJobExecutor = AsyncJobExecutor(ioScope)
    private val feedReader: FeedReader = FeedReader(feedRepository, feedDetailRepository)
    private val feedAppender: FeedAppender = FeedAppender(feedRepository, feedDetailRepository)
    private val feedValidator: FeedValidator = FeedValidator(feedRepository)
    private val feedUpdater: FeedUpdater = FeedUpdater(feedRepository)
    private val feedEnricher: FeedEnricher = FeedEnricher()
    private val feedRemover: FeedRemover = FeedRemover(feedRepository, feedDetailRepository)
    private val optimisticLockHandler: OptimisticLockHandler = OptimisticLockHandler()
    private val feedHandler: FeedHandler = FeedHandler(feedUpdater, asyncJobExecutor, optimisticLockHandler)
    private val feedService: FeedService =
        FeedService(feedReader, feedHandler, feedAppender, feedValidator, fileHandler, feedEnricher, feedRemover)

    @Test
    fun `피드를 가져온다`() {
        val feedId = "feedId"
        val userId = "userId"
        val feedDetailId = "feedDetailId"
        val feed = TestDataFactory.createFeedInfo(feedId, userId)
        val feedDetail = TestDataFactory.createFeedDetail(feedId, feedDetailId, 0)

        every { feedRepository.read(feedId) } returns feed
        every { feedDetailRepository.read(feedId) } returns listOf(feedDetail)

        val result = feedService.getFeed(feedId)

        assert(result.feed.feedId == feedId)
        assert(result.feedDetails.size == 1)
        assert(result.feedDetails[0].feedDetailId == feedDetailId)
        assert(result.feedDetails[0].feedId == feedId)
        assert(result.feed.userId == userId)
    }

    @Test
    fun `피드를 가져온다 - 피드가 존재 하지 않음`() {
        val feedId = "feedId"

        every { feedRepository.read(feedId) } returns null

        val exception = assertThrows<NotFoundException> {
            feedService.getFeed(feedId)
        }

        assert(exception.errorCode == ErrorCode.FEED_NOT_FOUND)
    }

    @Test
    fun `피드들을 분류해서 가져온다`() {
        val feedId1 = "feedId1"
        val feedId2 = "feedId2"
        val userId = "userId"
        val feedDetailId1 = "feedDetailId1"
        val feedDetailId2 = "feedDetailId2"
        val feedDetailId3 = "feedDetailId3"
        val feedDetailId4 = "feedDetailId4"
        val feed1 = TestDataFactory.createFeedInfo(feedId1, userId)
        val feed2 = TestDataFactory.createFeedInfo(feedId2, userId)
        val feedDetail1 = TestDataFactory.createFeedDetail(feedId1, feedDetailId1, 0)
        val feedDetail2 = TestDataFactory.createFeedDetail(feedId1, feedDetailId2, 1)
        val feedDetail3 = TestDataFactory.createFeedDetail(feedId2, feedDetailId3, 0)
        val feedDetail4 = TestDataFactory.createFeedDetail(feedId2, feedDetailId4, 1)

        every { feedRepository.reads(listOf(feedId1, feedId2)) } returns listOf(feed1, feed2)
        every { feedDetailRepository.readsFirstIndex(listOf(feedId1, feedId2)) } returns listOf(
            feedDetail1,
            feedDetail2,
            feedDetail3,
            feedDetail4,
        )

        val result = feedService.getFeeds(listOf(feedId1, feedId2))

        assert(result.size == 2)
        assert(result[0].feed.feedId == feedId1)
        assert(result[0].feedDetails.size == 2)
        assert(result[0].feedDetails[0].feedDetailId == feedDetailId1)
        assert(result[0].feedDetails[1].feedDetailId == feedDetailId2)
        assert(result[1].feed.feedId == feedId2)
        assert(result[1].feedDetails.size == 2)
        assert(result[1].feedDetails[0].feedDetailId == feedDetailId3)
        assert(result[1].feedDetails[1].feedDetailId == feedDetailId4)
    }

    @Test
    fun `본인 피드들을 가져온다`() {
        val userId = "userId"
        val feedId1 = "feedId1"
        val feedId2 = "feedId2"
        val feedDetailId1 = "feedDetailId1"
        val feedDetailId2 = "feedDetailId2"
        val feed1 = TestDataFactory.createFeedInfo(feedId1, userId)
        val feed2 = TestDataFactory.createFeedInfo(feedId2, userId)
        val feedDetail1 = TestDataFactory.createFeedDetail(feedId1, feedDetailId1, 0)
        val feedDetail2 = TestDataFactory.createFeedDetail(feedId2, feedDetailId2, 1)

        every { feedRepository.readsOwned(userId, FeedStatus.NOT_HIDDEN) } returns listOf(feed1, feed2)
        every { feedDetailRepository.readsFirstIndex(listOf(feedId1, feedId2)) } returns listOf(
            feedDetail1,
            feedDetail2,
        )

        val result = feedService.getOwnedFeeds(userId, FeedStatus.NOT_HIDDEN)

        assert(result.size == 2)
        assert(result[0].feed.feedId == feedId1)
        assert(result[0].feedDetails.size == 1)
        assert(result[0].feedDetails[0].feedDetailId == feedDetailId1)
        assert(result[1].feed.feedId == feedId2)
        assert(result[1].feedDetails.size == 1)
        assert(result[1].feedDetails[0].feedDetailId == feedDetailId2)
    }

    @Test
    fun `피드들을 삭제에 성공한다`() {
        val userId = "userId"
        val feedIds = listOf("feedId1", "feedId2")
        val feed1 = TestDataFactory.createFeedInfo(feedIds[0], userId)
        val feed2 = TestDataFactory.createFeedInfo(feedIds[1], userId)

        every { feedRepository.reads(feedIds) } returns listOf(feed1, feed2)
        every { feedRepository.removes(feedIds) } just Runs
        every { feedDetailRepository.removes(feedIds) } returns listOf()
        every { fileHandler.handleOldFiles(any()) } just Runs

        assertDoesNotThrow { feedService.removes(userId, feedIds) }
    }

    @Test
    fun `피드들 삭제 에 실패 - 잘봇된 접근 본인 소유의 피드가 아님`() {
        val userId = "userId"
        val feedId1 = "feedId1"
        val feedId2 = "feedId2"
        val feed1 = TestDataFactory.createFeedInfo(feedId1, userId)
        val feed2 = TestDataFactory.createFeedInfo(feedId2, "anotherUserId")

        every { feedRepository.reads(listOf(feedId1, feedId2)) } returns listOf(feed1, feed2)

        assertThrows<ConflictException> {
            feedService.removes(userId, listOf(feedId1, feedId2))
        }
    }

    @Test
    fun `피드 숨기기 기능 성공`() {
        val userId = "userId"
        val feedId1 = "feedId1"
        val feedId2 = "feedId2"
        val feed1 = TestDataFactory.createFeedInfo(feedId1, userId)
        val feed2 = TestDataFactory.createFeedInfo(feedId2, userId)

        every { feedRepository.reads(listOf(feedId1)) } returns listOf(feed1)
        every { feedRepository.reads(listOf(feedId2)) } returns listOf(feed2)
        coEvery { feedRepository.update(feedId1, FeedTarget.UNHIDE) } returns feedId1
        coEvery { feedRepository.update(feedId2, FeedTarget.HIDE) } returns feedId2

        assertDoesNotThrow { feedService.changeHide(userId, listOf(feedId1), FeedTarget.UNHIDE) }
        assertDoesNotThrow { feedService.changeHide(userId, listOf(feedId2), FeedTarget.HIDE) }
    }

    @Test
    fun `피드를 숨기기 기능 실패 - 잘못된 접근 본인 소유의 피드가 아님`() {
        val userId = "userId"
        val feedId1 = "feedId1"
        val feedId2 = "feedId2"
        val feed1 = TestDataFactory.createFeedInfo(feedId1, userId)
        val feed2 = TestDataFactory.createFeedInfo(feedId2, userId)

        every { feedRepository.reads(listOf(feedId1, feedId2)) } returns listOf(feed1, feed2)

        val exception = assertThrows<ConflictException> {
            feedService.changeHide("anotherUserId", listOf(feedId1, feedId2), FeedTarget.HIDE)
        }

        assert(exception.errorCode == ErrorCode.FEED_IS_NOT_OWNED)
    }

    @Test
    fun `피드 숨기기 기능 실패 - 낙관적락 실패`() {
        val userId = "userId"
        val feedId1 = "feedId1"
        val feedId2 = "feedId2"
        val feed1 = TestDataFactory.createFeedInfo(feedId1, userId)
        val feed2 = TestDataFactory.createFeedInfo(feedId2, userId)

        every { feedRepository.reads(listOf(feedId1, feedId2)) } returns listOf(feed1, feed2)
        coEvery { feedRepository.update(feedId1, FeedTarget.HIDE) } throws OptimisticLockingFailureException("")
        coEvery { feedRepository.update(feedId2, FeedTarget.HIDE) } throws OptimisticLockingFailureException("")

        feedService.changeHide(userId, listOf(feedId1, feedId2), FeedTarget.HIDE)

        coVerify(exactly = 5) { feedRepository.update(feedId1, FeedTarget.HIDE) }
        coVerify(exactly = 5) { feedRepository.update(feedId2, FeedTarget.HIDE) }
    }

    @Test
    fun `피드를 추가한다`() {
        val userId = "userId"
        val topic = "topic"
        val feedId = "feedId"
        val fileData = TestDataFactory.createFileData()
        val media = TestDataFactory.createProfileMedia()

        every { feedRepository.append(userId, topic) } returns feedId
        every { feedDetailRepository.append(listOf(media), feedId) } just Runs
        every { fileHandler.handleNewFiles(userId, listOf(fileData), FileCategory.FEED) } returns listOf(media)

        val result =
            feedService.make(userId, listOf(fileData), topic, FileCategory.FEED)
        assert(result == feedId)
    }
}
