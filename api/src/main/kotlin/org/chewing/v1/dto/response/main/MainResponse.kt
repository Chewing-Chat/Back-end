package org.chewing.v1.dto.response.main

import org.chewing.v1.dto.response.chat.ChatLogResponse
import org.chewing.v1.dto.response.feed.ThumbnailFeedResponse
import org.chewing.v1.dto.response.friend.FriendResponse
import org.chewing.v1.dto.response.user.UserResponse
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.room.DirectChatRoom
import org.chewing.v1.model.chat.room.GroupChatRoom
import org.chewing.v1.model.feed.Feed
import org.chewing.v1.model.friend.Friend
import org.chewing.v1.model.user.UserId
import org.chewing.v1.model.user.UserInfo

data class MainResponse(
    val friends: List<FriendResponse>,
    val user: UserResponse,
    val totalFriends: Int,
    val directChatRooms: List<DirectMainChatRoomResponse>,
    val groupChatRooms: List<GroupMainChatRoomResponse>,
    val oneDayFeeds: List<ThumbnailFeedResponse>,
) {
    data class DirectMainChatRoomResponse(
        val chatRoomId: String,
        val friendId: String,
        val chatLogs: List<ChatLogResponse>,
    ) {
        companion object {
            fun of(
                directChatRoom: DirectChatRoom,
                chatLogs: List<ChatLog>,
            ): DirectMainChatRoomResponse {
                return DirectMainChatRoomResponse(
                    chatRoomId = directChatRoom.roomInfo.chatRoomId.id,
                    friendId = directChatRoom.roomInfo.friendId.id,
                    chatLogs = chatLogs.map { ChatLogResponse.from(it) },
                )
            }
        }
    }

    data class GroupMainChatRoomResponse(
        val chatRoomId: String,
        val friendIds: List<String>,
        val chatLogs: List<ChatLogResponse>,
    ) {
        companion object {
            fun of(
                groupChatRoom: GroupChatRoom,
                chatLogs: List<ChatLog>,
                userId: UserId,
            ): GroupMainChatRoomResponse {
                val friendIds = groupChatRoom.memberInfos
                    .filter { it.memberId != userId }
                    .map { it.memberId.id }
                return GroupMainChatRoomResponse(
                    chatRoomId = groupChatRoom.roomInfo.chatRoomId.id,
                    chatLogs = chatLogs.map { ChatLogResponse.from(it) },
                    friendIds = friendIds,
                )
            }
        }
    }

    companion object {
        fun ofList(
            userInfo: UserInfo,
            friends: List<Friend>,
            directChats: List<Pair<DirectChatRoom, List<ChatLog>>>,
            groupChats: List<Pair<GroupChatRoom, List<ChatLog>>>,
            userId: UserId,
            oneDayFeeds: List<Feed>,
        ): MainResponse {
            return MainResponse(
                friends = friends.map { FriendResponse.of(it) },
                user = UserResponse.of(userInfo),
                totalFriends = friends.size,
                directChatRooms = directChats.map { (chatRoom, chatLogs) ->
                    DirectMainChatRoomResponse.of(chatRoom, chatLogs)
                },
                groupChatRooms = groupChats.map { (chatRoom, chatLogs) ->
                    GroupMainChatRoomResponse.of(chatRoom, chatLogs, userId)
                },
                oneDayFeeds = oneDayFeeds.map { ThumbnailFeedResponse.of(it) },
            )
        }
    }
}
