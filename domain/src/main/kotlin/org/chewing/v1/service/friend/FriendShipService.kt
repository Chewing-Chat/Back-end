package org.chewing.v1.service.friend

import org.chewing.v1.implementation.friend.friendship.*
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.friend.FriendShipProfile
import org.chewing.v1.model.friend.FriendShipStatus
import org.chewing.v1.model.user.User
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Service

@Service
class FriendShipService(
    private val friendShipReader: FriendShipReader,
    private val friendShipRemover: FriendShipRemover,
    private val friendShipAppender: FriendShipAppender,
    private val friendShipValidator: FriendShipValidator,
    private val friendShipUpdater: FriendShipUpdater,
    private val friendShipFilter: FriendShipFilter,
) {

    fun getFriendShips(userId: UserId): List<FriendShip> =
        friendShipReader.reads(userId)

    fun getFavoriteFriendShips(userId: UserId): List<FriendShip> = friendShipReader.readsFavorite(userId)
    fun createFriendShips(
        userId: UserId,
        user: User,
        targetUsers: List<User>,
        friendShipProfiles: List<FriendShipProfile>,
    ): List<FriendShip> {
        val matchedFriends = friendShipFilter.filterTargetFriend(targetUsers, friendShipProfiles)
        return matchedFriends.map { (matchingUser, profile) ->
            friendShipAppender.appendIfNotExist(
                matchingUser.info.userId,
                userId,
                user.info.name,
                FriendShipStatus.NORMAL,
            )

            friendShipAppender.appendIfNotExist(
                userId,
                matchingUser.info.userId,
                profile.friendName,
                FriendShipStatus.FRIEND,
            )
        }
    }

    fun ensureAllMembersAreFriends(members: List<User>) {
        members.asSequence()
            .flatMapIndexed { i, userA ->
                members.asSequence().drop(i + 1).map { userB ->
                    userA to userB
                }
            }
            .forEach { (userA, userB) ->
                friendShipAppender.appendIfNotExist(
                    userA.info.userId,
                    userB.info.userId,
                    userB.info.name,
                    FriendShipStatus.NORMAL,
                )
                friendShipAppender.appendIfNotExist(
                    userB.info.userId,
                    userA.info.userId,
                    userA.info.name,
                    FriendShipStatus.NORMAL,
                )
            }
    }

    fun changeFriendShipStatus(userId: UserId, friendId: UserId, friendName: String) {
        friendShipUpdater.allowedFriend(userId, friendId, friendName)
    }

    fun removeFriendShip(userId: UserId, friendId: UserId) {
        friendShipRemover.remove(userId, friendId)
    }

    fun blockFriendShip(userId: UserId, friendId: UserId) {
        friendShipRemover.block(userId, friendId)
    }

    fun changeFriendFavorite(userId: UserId, friendId: UserId, favorite: Boolean) {
        val friendShip = friendShipReader.read(userId, friendId)
        friendShipValidator.validateInteractionAllowed(friendShip)
        friendShipUpdater.updateFavorite(userId, friendId, favorite)
    }

    fun changeFriendName(userId: UserId, friendId: UserId, friendName: String) {
        val friendShip = friendShipReader.read(userId, friendId)
        friendShipValidator.validateInteractionAllowed(friendShip)
        friendShipUpdater.updateName(userId, friendId, friendName)
    }

    fun checkAccessibleFriendShip(userId: UserId, friendId: UserId) {
        val friendShip = friendShipReader.read(userId, friendId)
        friendShipValidator.validateInteractionAllowed(friendShip)
    }

    fun checkAllowedFriendShip(userId: UserId, friendId: UserId) {
        val friendShip = friendShipReader.read(userId, friendId)
        friendShipValidator.validateAllowedFriend(friendShip)
    }
}
