package org.chewing.v1.controller.chat

import org.chewing.v1.dto.request.chat.ChatRoomRequest
import org.chewing.v1.dto.response.chat.ChatRoomIdResponse
import org.chewing.v1.dto.response.chat.ChatRoomListResponse
import org.chewing.v1.facade.ChatRoomFacade
import org.chewing.v1.model.chat.room.ChatRoomSortCriteria
import org.chewing.v1.response.HttpResponse
import org.chewing.v1.response.SuccessOnlyResponse
import org.chewing.v1.service.chat.RoomService
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
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
        @RequestAttribute("userId") userId: String,
        @RequestParam("sort") sort: ChatRoomSortCriteria,
    ): SuccessResponseEntity<ChatRoomListResponse> {
        val chatRooms = chatRoomFacade.getChatRooms(userId, sort)
        return ResponseHelper.success(ChatRoomListResponse.ofList(chatRooms))
    }

    @PostMapping("/delete")
    fun deleteChatRooms(
        @RequestBody request: ChatRoomRequest.Delete,
        @RequestAttribute("userId") userId: String,
    ): ResponseEntity<HttpResponse<SuccessOnlyResponse>> {
        roomService.deleteChatRoom(request.chatRoomIds, userId)
        return ResponseHelper.successOnly()
    }

    @PostMapping("/delete/group")
    fun deleteGroupChatRooms(
        @RequestBody request: ChatRoomRequest.Delete,
        @RequestAttribute("userId") userId: String,
    ): ResponseEntity<HttpResponse<SuccessOnlyResponse>> {
        chatRoomFacade.leavesChatRoom(request.chatRoomIds, userId)
        return ResponseHelper.successOnly()
    }

    @PostMapping("/create")
    fun createChatRoom(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: ChatRoomRequest.Create,
    ): SuccessResponseEntity<ChatRoomIdResponse> {
        val roomId = roomService.createChatRoom(userId, request.friendId)
        return ResponseHelper.successCreate(ChatRoomIdResponse.from(roomId))
    }

    @PostMapping("/create/group")
    fun createGroupChatRoom(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: ChatRoomRequest.CreateGroup,
    ): SuccessResponseEntity<ChatRoomIdResponse> {
        val roomId = chatRoomFacade.createGroupChatRoom(userId, request.friendIds)
        return ResponseHelper.successCreate(ChatRoomIdResponse.from(roomId))
    }

    @PostMapping("/invite")
    fun inviteChatRoom(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: ChatRoomRequest.Invite,
    ): ResponseEntity<HttpResponse<SuccessOnlyResponse>> {
        chatRoomFacade.inviteChatRoom(userId, request.chatRoomId, request.friendId)
        return ResponseHelper.successOnly()
    }

    @PostMapping("/favorite")
    fun updateFavorite(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: ChatRoomRequest.Favorite,
    ): ResponseEntity<HttpResponse<SuccessOnlyResponse>> {
        roomService.favoriteChatRoom(userId, request.chatRoomId, request.favorite)
        return ResponseHelper.successOnly()
    }
}
