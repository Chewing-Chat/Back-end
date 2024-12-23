package org.chewing.v1.repository.feed

import org.chewing.v1.model.comment.CommentInfo
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository {
    fun reads(feedId: String): List<CommentInfo>
    fun readsIn(commentIds: List<String>): List<CommentInfo>
    fun append(userId: String, feedId: String, comment: String): String
    fun readsOwned(userId: String): List<CommentInfo>
    fun remove(commentId: String): String?
    fun removes(feedIds: List<String>)
}
