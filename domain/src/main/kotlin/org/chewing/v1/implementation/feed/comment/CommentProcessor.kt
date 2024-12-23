package org.chewing.v1.implementation.feed.comment

import org.chewing.v1.implementation.feed.feed.FeedUpdater
import org.chewing.v1.model.feed.FeedTarget
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CommentProcessor(
    private val commentRemover: CommentRemover,
    private val commentAppender: CommentAppender,
    private val feedUpdater: FeedUpdater,
) {

    @Transactional
    fun processComment(userId: String, feedId: String, comment: String, updateType: FeedTarget): String {
        feedUpdater.update(feedId, updateType)
        return commentAppender.appendComment(userId, feedId, comment)
    }

    // 존재 하는 댓글에 대해서만 업데이트 진행
    @Transactional
    fun processUnComment(commentId: String, updateType: FeedTarget) {
        commentRemover.remove(commentId)?.let {
            feedUpdater.update(it, updateType)
        }
    }
}
