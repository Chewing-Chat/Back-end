package org.chewing.v1.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.chewing.v1.TestDataFactory
import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.util.OptimisticLockHandler
import org.chewing.v1.implementation.feed.comment.*
import org.chewing.v1.implementation.feed.feed.FeedUpdater
import org.chewing.v1.model.feed.FeedTarget
import org.chewing.v1.repository.feed.CommentRepository
import org.chewing.v1.service.feed.FeedCommentService
import org.chewing.v1.util.AsyncJobExecutor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.dao.OptimisticLockingFailureException

class FeedCommentServiceTest {

    private val commentRepository: CommentRepository = mockk()
    private val feedUpdater: FeedUpdater = mockk()
    private val commentReader = CommentReader(commentRepository)
    private val commentRemover = CommentRemover(commentRepository)
    private val commentValidator = CommentValidator(commentRepository)
    private val commentAppender = CommentAppender(commentRepository)
    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private val commentProcessor = CommentProcessor(commentRemover, commentAppender, feedUpdater)
    private val asyncJobExecutor = AsyncJobExecutor(ioScope)
    private val optimisticLockHandler: OptimisticLockHandler = OptimisticLockHandler()
    private val commentHandler = CommentHandler(commentProcessor, asyncJobExecutor, optimisticLockHandler)
    private val feedCommentService = FeedCommentService(commentReader, commentHandler, commentRemover, commentValidator)

    @Test
    fun `댓글 삭제 한번에 성공`() {
        // given
        val userId = "userId"
        val commentId1 = "commentId1"
        val commentId2 = "commentId2"
        val feedId = "feedId"
        val commentIds = listOf(commentId1, commentId2)
        val target = FeedTarget.UNCOMMENTS
        val comments = listOf(
            TestDataFactory.createCommentInfo(userId, commentId1, feedId),
            TestDataFactory.createCommentInfo(userId, commentId2, feedId),
        )

        every { commentRepository.readsIn(commentIds) } returns comments
        every { commentRepository.remove(commentId1) } returns feedId
        every { commentRepository.remove(commentId2) } returns feedId

        justRun { feedUpdater.update(feedId, FeedTarget.UNCOMMENTS) }

        assertDoesNotThrow {
            feedCommentService.remove(userId, commentIds, target)
        }

        verify(exactly = 1) { commentRepository.remove(commentId1) }
        verify(exactly = 1) { commentRepository.remove(commentId2) }
        verify(exactly = 2) { feedUpdater.update(feedId, FeedTarget.UNCOMMENTS) }
    }

    @Test
    fun `댓글 삭제 실패 - 삭제하려는 것과 삭제 대상이 크기가 다름`() {
        val userId = "userId"
        val commentId1 = "commentId1"
        val commentId2 = "commentId2"
        val feedId = "feedId"
        val commentIds = listOf(commentId1, commentId2)
        val target = FeedTarget.UNCOMMENTS
        val comments = listOf(
            TestDataFactory.createCommentInfo(userId, commentId1, feedId),
        )

        every { commentRepository.readsIn(commentIds) } returns comments

        val result = assertThrows<ConflictException> {
            feedCommentService.remove(userId, commentIds, target)
        }

        assert(result.errorCode == ErrorCode.FEED_COMMENT_NOT_FOUND)
    }

    @Test
    fun `댓글 삭제 실패 - 삭제하려는 댓글 소유자가 아님`() {
        val userId = "userId"
        val commentId1 = "commentId1"
        val commentId2 = "commentId2"
        val feedId = "feedId"
        val commentIds = listOf(commentId1, commentId2)
        val target = FeedTarget.UNCOMMENTS
        val comments = listOf(
            TestDataFactory.createCommentInfo("otherUserId", commentId1, feedId),
            TestDataFactory.createCommentInfo("otherUserId", commentId2, feedId),
        )

        every { commentRepository.readsIn(commentIds) } returns comments

        val result = assertThrows<ConflictException> {
            feedCommentService.remove(userId, commentIds, target)
        }

        assert(result.errorCode == ErrorCode.FEED_COMMENT_IS_NOT_OWNED)
    }

    @Test
    fun `댓글 삭제 실패 - 낙관적 락 retry만큼 실패`() {
        val userId = "userId"
        val commentId1 = "commentId1"
        val commentId2 = "commentId2"
        val feedId = "feedId"
        val commentIds = listOf(commentId1, commentId2)
        val target = FeedTarget.UNCOMMENTS
        val comments = listOf(
            TestDataFactory.createCommentInfo(userId, commentId1, feedId),
            TestDataFactory.createCommentInfo(userId, commentId2, feedId),
        )

        every { commentRepository.readsIn(commentIds) } returns comments
        every { commentRepository.remove(commentId1) } returns feedId
        every { commentRepository.remove(commentId2) } returns feedId
        every { feedUpdater.update(feedId, FeedTarget.UNCOMMENTS) } throws OptimisticLockingFailureException("")

        feedCommentService.remove(userId, commentIds, target)

        verify(exactly = 5) { commentRepository.remove(commentId1) }
        verify(exactly = 5) { commentRepository.remove(commentId2) }
        verify(exactly = 10) { feedUpdater.update(feedId, FeedTarget.UNCOMMENTS) }
    }

    @Test
    fun `댓글 삭제 여러번에 성공`() {
        // given
        val feedIds = listOf("feedId1", "feedId2")

        every { commentRepository.removes(feedIds) } just Runs

        assertDoesNotThrow {
            feedCommentService.removes(feedIds)
        }
    }

    @Test
    fun `댓글 한번에 추가 성공`() {
        // given
        val userId = "userId"
        val feedId = "feedId"
        val comment = "comment"
        val target = FeedTarget.COMMENTS
        val commentId = "commentId"

        justRun { feedUpdater.update(feedId, FeedTarget.COMMENTS) }
        every { commentRepository.append(userId, feedId, comment) } returns commentId

        val result =
            feedCommentService.comment(userId, feedId, comment, target)

        assert(result == commentId)
        verify(exactly = 1) { feedUpdater.update(feedId, FeedTarget.COMMENTS) }
    }

    @Test
    fun `댓글 추가 실패 - 낙관적락`() {
        val userId = "userId"
        val feedId = "feedId"
        val comment = "comment"
        val target = FeedTarget.COMMENTS

        every { feedUpdater.update(feedId, FeedTarget.COMMENTS) }.throws(OptimisticLockingFailureException(""))

        val result = assertThrows<ConflictException> {
            feedCommentService.comment(userId, feedId, comment, target)
        }

        assert(result.errorCode == ErrorCode.FEED_COMMENT_FAILED)

        verify(exactly = 5) { feedUpdater.update(feedId, FeedTarget.COMMENTS) }
    }

    @Test
    fun `내가 작성한 댓글 가져오기`() {
        val userId = "userId"
        val commentId = "commentId"
        val feedId = "feedId"
        val comment = TestDataFactory.createCommentInfo(userId, commentId, feedId)

        every { commentRepository.readsOwned(userId) } returns listOf(comment)

        assert(feedCommentService.getOwnedComment(userId).size == 1)
    }

    @Test
    fun `피드의 댓글 가져오기`() {
        val feedId = "feedId"
        val commentId = "commentId"
        val userId = "userId"
        val comment = TestDataFactory.createCommentInfo(userId, commentId, feedId)

        every { commentRepository.reads(feedId) } returns listOf(comment)

        assert(feedCommentService.getComment(feedId).size == 1)
    }
}
