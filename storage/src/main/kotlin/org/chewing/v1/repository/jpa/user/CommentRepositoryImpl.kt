package org.chewing.v1.repository.jpa.user

import org.chewing.v1.jpaentity.feed.FeedCommentJpaEntity
import org.chewing.v1.jparepository.feed.FeedCommentJpaRepository
import org.chewing.v1.model.comment.CommentInfo
import org.chewing.v1.repository.feed.CommentRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
internal class CommentRepositoryImpl(
    private val commentJpaRepository: FeedCommentJpaRepository,
) : CommentRepository {

    override fun reads(feedId: String): List<CommentInfo> = commentJpaRepository.findAllByFeedId(feedId).map {
        it.toCommentInfo()
    }

    override fun readsIn(commentIds: List<String>): List<CommentInfo> =
        commentJpaRepository.findAllByFeedCommentIdIn(commentIds).map {
            it.toCommentInfo()
        }

    @Transactional
    override fun remove(commentId: String): String? = commentJpaRepository.findById(commentId).map {
        commentJpaRepository.deleteById(commentId)
        it.toCommentInfo().feedId
    }.orElse(null)

    @Transactional
    override fun removes(feedIds: List<String>) {
        commentJpaRepository
            .deleteAllByFeedIdIn(feedIds)
    }

    override fun append(userId: String, feedId: String, comment: String): String {
        return commentJpaRepository.save(FeedCommentJpaEntity.generate(userId, feedId, comment)).toCommentInfo().commentId
    }

    override fun readsOwned(userId: String): List<CommentInfo> = commentJpaRepository.findAllByUserId(userId).map {
        it.toCommentInfo()
    }
}
