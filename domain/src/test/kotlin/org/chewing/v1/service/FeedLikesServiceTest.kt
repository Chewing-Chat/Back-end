package org.chewing.v1.service

import io.mockk.Runs
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.justRun
import io.mockk.mockk
import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.util.OptimisticLockHandler
import org.chewing.v1.implementation.feed.feed.FeedUpdater
import org.chewing.v1.implementation.feed.like.*
import org.chewing.v1.model.feed.FeedTarget
import org.chewing.v1.repository.feed.FeedLikesRepository
import org.chewing.v1.service.feed.FeedLikesService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.dao.OptimisticLockingFailureException

class FeedLikesServiceTest {
    private val feedLikesRepository: FeedLikesRepository = mockk()
    private val feedUpdater: FeedUpdater = mockk()

    private val feedLikeAppender = FeedLikeAppender(feedLikesRepository)
    private val feedLikeChecker = FeedLikeChecker(feedLikesRepository)
    private val feedLikeRemover = FeedLikeRemover(feedLikesRepository)
    private val feedLikeProcessor = FeedLikeProcessor(feedLikeAppender, feedLikeRemover, feedUpdater)
    private val optimisticLockHandler = OptimisticLockHandler()
    private val feedLikeHandler = FeedLikeHandler(feedLikeProcessor, optimisticLockHandler)
    private val feedLikeValidator = FeedLikeValidator(feedLikesRepository)
    private val feedLikeService = FeedLikesService(feedLikeValidator, feedLikeHandler, feedLikeRemover, feedLikeChecker)

    @Test
    fun `피드 좋아요 성공`() {
        val userId = "userId"
        val feedId = "feedId"
        val feedTarget = FeedTarget.LIKES

        every { feedLikesRepository.checkLike(feedId, userId) } returns false
        justRun { feedUpdater.update(feedId, feedTarget) }
        justRun { feedLikesRepository.likes(any(), any()) }

        feedLikeService.like(feedId, userId, feedTarget)
    }

    @Test
    fun `피드 좋아요 실패 - 이미 좋아요 함`() {
        val userId = "userId"
        val feedId = "feedId"
        val feedTarget = FeedTarget.LIKES

        every { feedLikesRepository.checkLike(feedId, userId) } returns true

        val exception = assertThrows<ConflictException> {
            feedLikeService.like(feedId, userId, feedTarget)
        }

        assert(exception.errorCode == ErrorCode.FEED_ALREADY_LIKED)
    }

    @Test
    fun `피드 좋아요 실패 - 낙관적 락`() {
        val userId = "userId"
        val feedId = "feedId"
        val feedTarget = FeedTarget.LIKES

        every { feedLikesRepository.checkLike(feedId, userId) } returns false
        justRun { feedLikesRepository.likes(feedId, userId) }
        every { feedUpdater.update(feedId, feedTarget) } throws OptimisticLockingFailureException("")

        val result = assertThrows<ConflictException> {
            feedLikeService.like(feedId, userId, feedTarget)
        }

        coVerify(exactly = 5) { feedUpdater.update(feedId, feedTarget) }

        assert(result.errorCode == ErrorCode.FEED_LIKED_FAILED)
    }

    @Test
    fun `피드 좋아요 취소 성공`() {
        val userId = "userId"
        val feedId = "feedId"
        val feedTarget = FeedTarget.UNLIKES

        every { feedLikesRepository.checkLike(feedId, userId) } returns true
        justRun { feedUpdater.update(feedId, feedTarget) }
        justRun { feedLikesRepository.unlikes(any(), any()) }

        feedLikeService.unlike(feedId, userId, feedTarget)
    }

    @Test
    fun `피드 좋아요 취소 실패 - 이미 좋아요 안함`() {
        val userId = "userId"
        val feedId = "feedId"
        val feedTarget = FeedTarget.UNLIKES

        every { feedLikesRepository.checkLike(feedId, userId) } returns false

        val exception = assertThrows<ConflictException> {
            feedLikeService.unlike(feedId, userId, feedTarget)
        }

        assert(exception.errorCode == ErrorCode.FEED_ALREADY_UNLIKED)
    }

    @Test
    fun `피드 좋아요 취소 실패 - 낙관적 락`() {
        val userId = "userId"
        val feedId = "feedId"
        val feedTarget = FeedTarget.UNLIKES

        every { feedLikesRepository.checkLike(feedId, userId) } returns true
        justRun { feedLikesRepository.unlikes(feedId, userId) }
        every { feedUpdater.update(feedId, feedTarget) } throws OptimisticLockingFailureException("")

        val result = assertThrows<ConflictException> {
            feedLikeService.unlike(feedId, userId, feedTarget)
        }

        coVerify(exactly = 5) { feedUpdater.update(feedId, feedTarget) }

        assert(result.errorCode == ErrorCode.FEED_UNLIKED_FAILED)
    }

    @Test
    fun `피드 좋아요 여부 - 좋아요 함`() {
        val userId = "userId"
        val feedId = "feedId"

        every { feedLikesRepository.checkLike(feedId, userId) } returns true

        val result = feedLikeService.checkLike(feedId, userId)

        assert(result)
    }

    @Test
    fun `피드 좋아요 여부 - 좋아요 안함`() {
        val userId = "userId"
        val feedId = "feedId"

        every { feedLikesRepository.checkLike(feedId, userId) } returns false

        val result = feedLikeService.checkLike(feedId, userId)

        assert(!result)
    }

    @Test
    fun `모든 좋아요 삭제`() {
        val feedId = "feedId"
        every { feedLikesRepository.unlikeAll(listOf(feedId)) } just Runs
        assertDoesNotThrow {
            feedLikeService.unlikes(listOf(feedId))
        }
    }
}
