package org.chewing.v1.implementation.feed.feed

import org.chewing.v1.model.comment.Comment
import org.chewing.v1.model.comment.CommentInfo
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.user.User
import org.springframework.stereotype.Component

@Component
class FeedAggregator() {
    fun aggregates(commentsInfo: List<CommentInfo>, friendsInfo: List<FriendShip>, users: List<User>): List<Comment> {
        // 친구 정보와 사용자 정보를 사전 형태로 변환
        val friendsMap = friendsInfo.associateBy { it.friendId }
        val usersMap = users.associateBy { it.userId }

        return commentsInfo.mapNotNull { comment ->
            // 댓글 작성자의 친구 정보를 찾고, 사용자 정보를 업데이트
            val user = friendsMap[comment.userId]?.let { friend ->
                usersMap[friend.friendId]?.updateName(friend.friendName)
            }

            // 유효한 사용자와 친구가 존재하는 경우에만 Comment 객체 반환
            user?.let {
                Comment.of(comment.commentId, comment.comment, comment.createAt, user) // 사용자 정보를 포함하여 새로운 Comment 객체 생성
            }
        }
    }
}
