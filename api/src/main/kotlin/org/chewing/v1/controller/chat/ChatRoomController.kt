package org.chewing.v1.controller.chat

import org.chewing.v1.dto.request.chat.ChatRoomRequest
import org.chewing.v1.dto.response.chat.ChatRoomListResponse
import org.chewing.v1.dto.response.chat.DirectChatRoomResponse
import org.chewing.v1.dto.response.chat.FullDirectChatRoomResponse
import org.chewing.v1.dto.response.chat.FullGroupChatRoomResponse
import org.chewing.v1.facade.DirectChatFacade
import org.chewing.v1.facade.GroupChatFacade
import org.chewing.v1.model.user.UserId
import org.chewing.v1.response.SuccessOnlyResponse
import org.chewing.v1.service.chat.DirectChatRoomService
import org.chewing.v1.service.chat.GroupChatRoomService
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.chewing.v1.util.helper.FileHelper
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.security.CurrentUser
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/chatRoom")
class ChatRoomController(
    private val groupChatFacade: GroupChatFacade,
    private val directChatFacade: DirectChatFacade,
    private val groupChatRoomService: GroupChatRoomService,
    private val directChatRoomService: DirectChatRoomService,
) {

    @GetMapping("/list")
    fun getChatRoomList(
        @CurrentUser userId: UserId,
    ): SuccessResponseEntity<ChatRoomListResponse> {
        val directChatRooms = directChatFacade.processGetDirectChatRooms(userId)
        val groupChatRooms = groupChatFacade.processGroupChatRooms(userId)
        return ResponseHelper.success(ChatRoomListResponse.from(directChatRooms, groupChatRooms, userId))
    }

    @GetMapping("/search")
    fun searchChatRoom(
        @CurrentUser userId: UserId,
        @RequestParam("friendIds") friendIds: List<String>,
    ): SuccessResponseEntity<ChatRoomListResponse> {
        val directChatRooms = directChatFacade.searchDirectChatRooms(userId, friendIds.map { UserId.of(it) })
        val groupChatRooms = groupChatFacade.searchGroupChatRooms(userId, friendIds.map { UserId.of(it) })
        return ResponseHelper.success(ChatRoomListResponse.from(directChatRooms, groupChatRooms, userId))
    }

    @GetMapping("/direct/{friendId}")
    fun getDirectChatRoom(
        @CurrentUser userId: UserId,
        @PathVariable friendId: String,
    ): SuccessResponseEntity<DirectChatRoomResponse> {
        val directChatRoom = directChatFacade.processGetDirectChatRoom(userId, UserId.of(friendId))
        return ResponseHelper.success(DirectChatRoomResponse.of(directChatRoom))
    }

    @PostMapping("/direct/create/common")
    fun produceDirectChatRoom(
        @CurrentUser userId: UserId,
        @RequestBody request: ChatRoomRequest.Create,
    ): SuccessResponseEntity<FullDirectChatRoomResponse> {
        val (chatRoom, chatLog) = directChatFacade.processCreateDirectChatRoomCommonChat(userId, request.toFriendId(), request.toMessage())
        return ResponseHelper.successCreate(FullDirectChatRoomResponse.of(chatRoom, chatLog))
    }

    @PostMapping("/direct/create/files")
    fun produceDirectChatRoom(
        @RequestPart("files") files: List<MultipartFile>,
        @CurrentUser userId: UserId,
        @RequestParam("friendId") friendId: String,
    ): SuccessResponseEntity<FullDirectChatRoomResponse> {
        val convertFiles = FileHelper.convertMultipartFileToFileDataList(files)
        val (chatRoom, chatLog) = directChatFacade.processCreateDirectChatRoomFilesChat(userId, UserId.of(friendId), convertFiles)
        return ResponseHelper.successCreate(FullDirectChatRoomResponse.of(chatRoom, chatLog))
    }

    @DeleteMapping("/direct/delete")
    fun deleteDirectChatRoom(
        @CurrentUser userId: UserId,
        @RequestBody request: ChatRoomRequest.Delete,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        directChatRoomService.deleteDirectChatRoom(userId, request.toChatRoomId())
        return ResponseHelper.successOnly()
    }

    @PutMapping("/direct/favorite")
    fun favoriteDirectChatRoom(
        @CurrentUser userId: UserId,
        @RequestBody request: ChatRoomRequest.Favorite,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        directChatRoomService.favoriteDirectChatRoomType(userId, request.toChatRoomId(), request.toFavorite())
        return ResponseHelper.successOnly()
    }

    @PostMapping("/group/create")
    fun produceGroupChatRoom(
        @CurrentUser userId: UserId,
        @RequestBody request: ChatRoomRequest.CreateGroup,
    ): SuccessResponseEntity<FullGroupChatRoomResponse> {
        val (chatRoom, chatLog) = groupChatFacade.processGroupChatCreate(userId, request.toFriendIds(), request.toName())
        return ResponseHelper.successCreate(FullGroupChatRoomResponse.of(chatRoom, chatLog, userId))
    }

    @PutMapping("/group/favorite")
    fun favoriteGroupChatRoom(
        @CurrentUser userId: UserId,
        @RequestBody request: ChatRoomRequest.Favorite,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        groupChatRoomService.favoriteGroupChatRoomType(userId, request.toChatRoomId(), request.toFavorite())
        return ResponseHelper.successOnly()
    }

    @PostMapping("/group/invite")
    fun inviteGroupChatRoom(
        @CurrentUser userId: UserId,
        @RequestBody request: ChatRoomRequest.Invite,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        groupChatFacade.processGroupChatInvite(request.toChatRoomId(), userId, request.toFriendId())
        return ResponseHelper.successOnly()
    }

    @DeleteMapping("/group/leave")
    fun leaveGroupChatRoom(
        @CurrentUser userId: UserId,
        @RequestBody request: ChatRoomRequest.Leave,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        groupChatFacade.processGroupChatLeave(request.toChatRoomId(), userId)
        return ResponseHelper.successOnly()
    }
}
