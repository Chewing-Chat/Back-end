package org.chewing.v1.facade

import org.chewing.v1.implementation.search.ChatRoomSearchEngine
import org.chewing.v1.implementation.search.FriendSearchEngine
import org.chewing.v1.service.chat.ChatLogService
import org.chewing.v1.service.friend.FriendShipService
import org.springframework.stereotype.Service

@Service
class SearchFacade(
    private val friendShipService: FriendShipService,
    private val chatLogService: ChatLogService,
    private val friendSearchEngine: FriendSearchEngine,
    private val chatRoomSearchEngine: ChatRoomSearchEngine,
) {
//    fun search(userId: UserId, keyword: String): Search {
//        val friendShips = friendShipService.getFriendShips(userId, FriendSortCriteria.NAME)
//        val searchedFriendShips = friendSearchEngine.search(friendShips, keyword)
//        val roomInfos = roomService.getChatRooms(userId)
//        val searchedRooms = chatRoomSearchEngine.search(searchedFriendShips.map { it.friendId }, roomInfos)
//        val chatLogs = chatLogService.getLatestChat(searchedRooms.map { it.chatRoomId })
//        val chatRoomInfos = chatRoomAggregator.aggregateChatRoom(searchedRooms, chatLogs)
//        val chatRooms = ChatRoomSortEngine.sortChatRoom(chatRoomInfos, ChatRoomSortCriteria.DATE)
//        return Search.of(chatRooms, searchedFriendShips)
//    }
}
