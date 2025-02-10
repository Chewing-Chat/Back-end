package org.chewing.v1.facade

import org.chewing.v1.implementation.main.MainAggregator
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.UnReadTarget
import org.chewing.v1.model.chat.room.DirectChatRoom
import org.chewing.v1.model.chat.room.GroupChatRoom
import org.chewing.v1.model.feed.Feed
import org.chewing.v1.model.friend.Friend
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.User
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.chat.ChatLogService
import org.chewing.v1.service.chat.DirectChatRoomService
import org.chewing.v1.service.chat.GroupChatRoomService
import org.chewing.v1.service.feed.FeedService
import org.chewing.v1.service.friend.FriendShipService
import org.chewing.v1.service.user.UserService
import org.springframework.stereotype.Service

@Service
class MainFacade(
    private val userService: UserService,
    private val friendShipService: FriendShipService,
    private val feedService: FeedService,
    private val chatLogsService: ChatLogService,
    private val directChatRoomService: DirectChatRoomService,
    private val groupChatRoomService: GroupChatRoomService,
    private val mainAggregator: MainAggregator,
) {
    fun getFriendFeeds(userId: UserId): List<Pair<Friend, List<Feed>>> {
        val friendShips = friendShipService.getFavoriteFriendShips(userId)
        val friendIds = friendShips.map { it.friendId }

        val feedsByFriendId = feedService.getOneDayFeeds(userId, friendIds)
            .groupBy { it.feed.userId }  // 친구 ID 기준으로 피드 그룹화

        val friendIdsWithFeeds = feedsByFriendId.keys

        val usersWithFeeds = userService.getUsers(friendIdsWithFeeds.toList())

        return friendShips
            .filter { it.friendId in friendIdsWithFeeds }
            .mapNotNull { friendShip ->
                val user = usersWithFeeds.find { it.info.userId == friendShip.friendId }
                val friendFeeds = feedsByFriendId[friendShip.friendId] ?: emptyList()
                user?.let { user ->
                    val friend = Friend.of(user, friendShip.isFavorite, friendShip.friendName, friendShip.status)
                    friend to friendFeeds
                }
            }
    }


    fun getUserInfo(userId: UserId): User {
        return userService.getUser(userId, AccessStatus.ACCESS)
    }

    fun getDirectUnreadChatLog(userId: UserId): List<Pair<DirectChatRoom, List<ChatLog>>> {
        val directChatRooms = directChatRoomService.getUnreadDirectChatRooms(userId)

        val unReadDirectTargets = directChatRooms.map { chatRoom ->
            UnReadTarget.of(
                chatRoom.roomInfo.chatRoomId,
                chatRoom.roomSequence.sequenceNumber,
                chatRoom.ownSequence.readSequenceNumber,
            )
        }

        val chatLogsByRoomId = chatLogsService.getUnreadChatLogs(unReadDirectTargets)
            .groupBy { it.chatRoomId }

        return directChatRooms.mapNotNull { chatRoom ->
            chatLogsByRoomId[chatRoom.roomInfo.chatRoomId]?.let { chatLogs ->
                chatRoom to chatLogs
            }
        }
    }


    fun getGroupUnreadChatLog(userId: UserId): List<Pair<GroupChatRoom, List<ChatLog>>> {
        val groupChatRooms = groupChatRoomService.getUnreadGroupChatRooms(userId)
        val unReadGroupTargets = groupChatRooms.map {
            UnReadTarget.of(it.roomInfo.chatRoomId, it.roomSequence.sequenceNumber, it.ownSequence.readSequenceNumber)
        }
        val chatLogsByRoomId = chatLogsService.getUnreadChatLogs(unReadGroupTargets)
            .groupBy { it.chatRoomId }

        return groupChatRooms.mapNotNull { chatRoom ->
            chatLogsByRoomId[chatRoom.roomInfo.chatRoomId]?.let { chatLogs ->
                chatRoom to chatLogs
            }
        }
    }
}
