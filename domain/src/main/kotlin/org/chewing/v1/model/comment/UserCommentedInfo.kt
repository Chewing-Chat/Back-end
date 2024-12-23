package org.chewing.v1.model.comment

import org.chewing.v1.model.feed.Feed
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.user.User

class UserCommentedInfo private constructor(
    val comment: CommentInfo,
    val friendShip: FriendShip,
    val user: User,
    val feed: Feed,
) {
    companion object {
        fun of(comment: CommentInfo, friendShip: FriendShip, user: User, feed: Feed): UserCommentedInfo {
            return UserCommentedInfo(
                comment = comment,
                friendShip = friendShip,
                user = user,
                feed = feed,
            )
        }
    }
}
