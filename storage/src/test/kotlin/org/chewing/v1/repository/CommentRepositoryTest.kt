package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jparepository.feed.FeedCommentJpaRepository
import org.chewing.v1.repository.jpa.user.CommentRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

internal class CommentRepositoryTest : JpaContextTest() {
    @Autowired
    private lateinit var commentJpaRepository: FeedCommentJpaRepository

    @Autowired
    private lateinit var jpaDataGenerator: JpaDataGenerator

    @Autowired
    private lateinit var commentRepositoryImpl: CommentRepositoryImpl

    @Test
    fun `댓글을 추가해야 한다`() {
        val userId = generateUserId()
        val feedId = generateFeedId()
        val comment = "comment"
        val result = commentRepositoryImpl.append(userId, feedId, comment)

        assert(result.isNotEmpty())
    }

    @Test
    fun `댓글을 조회해야 한다`() {
        val userId = generateUserId()
        val feedId = generateFeedId()
        val comments = jpaDataGenerator.feedCommentEntityDataList(userId, feedId)
        val result = commentRepositoryImpl.reads(feedId)
        assert(result.size == comments.size)
    }

    @Test
    fun `댓글을 삭제해야 한다`() {
        val userId = generateUserId()
        val feedId = generateFeedId()
        val comment = jpaDataGenerator.feedCommentEntityData(userId, feedId)
        val result = commentRepositoryImpl.remove(comment.commentId)
        assert(result != null)
    }

    @Test
    fun `댓글 삭제 실패 댓글이 없어야 한다`() {
        val commentId = generateCommentId()
        val result = commentRepositoryImpl.remove(commentId)
        assert(result == null)
    }

    @Test
    fun `댓글아이디를 가지고 모든 댓글을 조회해야 한다`() {
        val userId = generateUserId()
        val feedId = generateFeedId()
        val comments = jpaDataGenerator.feedCommentEntityDataList(userId, feedId)
        val result = commentRepositoryImpl.readsIn(comments.map { it.commentId })
        assert(result.size == comments.size)
    }

    @Test
    fun `모든 댓글을 삭제해야 한다`() {
        val userId = generateUserId()
        val feedId = generateFeedId()
        jpaDataGenerator.feedCommentEntityDataList(userId, feedId)
        commentRepositoryImpl.removes(listOf(feedId))
        val result = commentJpaRepository.findAllByFeedId(feedId)
        assert(result.isEmpty())
    }

    @Test
    fun `댓글을 작성한 사용자의 댓글을 조회해야 한다`() {
        val userId = generateUserId()
        val userId2 = generateUserId()
        val feedId = generateFeedId()
        val comments = jpaDataGenerator.feedCommentEntityDataList(userId, feedId)
        val comments2 = jpaDataGenerator.feedCommentEntityDataList(userId2, feedId)
        val result = commentRepositoryImpl.readsOwned(userId)
        val result2 = commentRepositoryImpl.readsOwned(userId2)
        assert(result.size == comments.size)
        assert(result2.size == comments2.size)
    }

    private fun generateUserId() = UUID.randomUUID().toString()

    private fun generateFeedId() = UUID.randomUUID().toString()

    private fun generateCommentId() = UUID.randomUUID().toString()
}
