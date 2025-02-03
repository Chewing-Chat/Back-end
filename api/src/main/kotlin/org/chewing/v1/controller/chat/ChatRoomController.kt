package org.chewing.v1.controller.chat

import org.chewing.v1.dto.request.chat.ChatRoomRequest
import org.chewing.v1.dto.response.chat.ChatRoomIdResponse
import org.chewing.v1.dto.response.chat.ChatRoomListResponse
import org.chewing.v1.facade.ChatRoomFacade
import org.chewing.v1.model.chat.room.ChatRoomSortCriteria
import org.chewing.v1.model.user.UserId
import org.chewing.v1.response.HttpResponse
import org.chewing.v1.response.SuccessOnlyResponse
import org.chewing.v1.service.chat.RoomService
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.chewing.v1.util.security.CurrentUser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/chatRoom")
class ChatRoomController(
    private val chatRoomFacade: ChatRoomFacade,
    private val roomService: RoomService,
) {
    @PostMapping("/list")
    fun getChatRooms(
        @CurrentUser userId: UserId,
        @RequestParam("sort") sort: ChatRoomSortCriteria,
    ): SuccessResponseEntity<ChatRoomListResponse> {
        val chatRooms = chatRoomFacade.getChatRooms(userId, sort)
        return ResponseHelper.success(ChatRoomListResponse.ofList(chatRooms))
    }

    @PostMapping("/delete")
    fun deleteChatRooms(
        @RequestBody request: ChatRoomRequest.Delete,
        @CurrentUser userId: UserId,
    ): ResponseEntity<HttpResponse<SuccessOnlyResponse>> {
        roomService.deleteChatRoom(request.chatRoomIds, userId)
        return ResponseHelper.successOnly()
    }

    @PostMapping("/delete/group")
    fun deleteGroupChatRooms(
        @RequestBody request: ChatRoomRequest.Delete,
        @CurrentUser userId: UserId,
    ): ResponseEntity<HttpResponse<SuccessOnlyResponse>> {
        chatRoomFacade.leavesChatRoom(request.chatRoomIds, userId)
        return ResponseHelper.successOnly()
    }

    @PostMapping("/create")
    fun createChatRoom(
        @CurrentUser userId: UserId,
        @RequestBody request: ChatRoomRequest.Create,
    ): SuccessResponseEntity<ChatRoomIdResponse> {
        val roomId = roomService.createChatRoom(userId, request.toFriendId())
        return ResponseHelper.successCreate(ChatRoomIdResponse.from(roomId))
    }

    @PostMapping("/create/group")
    fun createGroupChatRoom(
        @CurrentUser userId: UserId,
        @RequestBody request: ChatRoomRequest.CreateGroup,
    ): SuccessResponseEntity<ChatRoomIdResponse> {
        val roomId = chatRoomFacade.createGroupChatRoom(userId, request.toFriendIds())
        return ResponseHelper.successCreate(ChatRoomIdResponse.from(roomId))
    }

    @PostMapping("/invite")
    fun inviteChatRoom(
        @CurrentUser userId: UserId,
        @RequestBody request: ChatRoomRequest.Invite,
    ): ResponseEntity<HttpResponse<SuccessOnlyResponse>> {
        chatRoomFacade.inviteChatRoom(userId, request.chatRoomId, request.toFriendId())
        return ResponseHelper.successOnly()
    }

    @PostMapping("/favorite")
    fun updateFavorite(
        @CurrentUser userId: UserId,
        @RequestBody request: ChatRoomRequest.Favorite,
    ): ResponseEntity<HttpResponse<SuccessOnlyResponse>> {
        roomService.favoriteChatRoom(request.chatRoomId, userId, request.favorite)
        return ResponseHelper.successOnly()
    }
}
