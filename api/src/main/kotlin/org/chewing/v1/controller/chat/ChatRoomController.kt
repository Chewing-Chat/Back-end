package org.chewing.v1.controller.chat

import org.chewing.v1.dto.request.chat.ChatRoomRequest
import org.chewing.v1.dto.response.chat.ChatRoomListResponse
import org.chewing.v1.dto.response.chat.DirectChatRoomResponse
import org.chewing.v1.dto.response.chat.GroupChatRoomResponse
import org.chewing.v1.facade.DirectChatFacade
import org.chewing.v1.facade.GroupChatFacade
import org.chewing.v1.model.user.UserId
import org.chewing.v1.response.HttpResponse
import org.chewing.v1.response.SuccessOnlyResponse
import org.chewing.v1.service.chat.DirectChatRoomService
import org.chewing.v1.service.chat.GroupChatRoomService
import org.chewing.v1.util.helper.FileHelper
import org.chewing.v1.util.security.CurrentUser
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestAttribute
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
    fun getDirectChatLog(
        @CurrentUser userId: UserId,
    ): HttpResponse<ChatRoomListResponse> {
        val directChatRooms = directChatFacade.processGetDirectChatRooms(userId)
        val groupChatRooms = groupChatFacade.processGroupChatRooms(userId)
        return HttpResponse.success(ChatRoomListResponse.from(directChatRooms, groupChatRooms, userId))
    }

    @GetMapping("/direct/{friendId}")
    fun getDirectChatRoom(
        @CurrentUser userId: UserId,
        @PathVariable friendId: String,
    ): HttpResponse<DirectChatRoomResponse> {
        val directChatRoom = directChatFacade.processGetDirectChatRoom(userId, UserId.of(friendId))
        return HttpResponse.success(DirectChatRoomResponse.of(directChatRoom))
    }

    @PostMapping("/direct/create/common")
    fun produceDirectChatRoom(
        @CurrentUser userId: UserId,
        @RequestBody request: ChatRoomRequest.Create,
    ): HttpResponse<DirectChatRoomResponse> {
        val directChatRoom = directChatFacade.processCreateDirectChatRoomCommonChat(userId, request.toFriendId(), request.toMessage())
        return HttpResponse.success(DirectChatRoomResponse.of(directChatRoom))
    }

    @GetMapping("/direct/create/files")
    fun produceDirectChatRoom(
        @RequestPart("files") files: List<MultipartFile>,
        @RequestAttribute("userId") userId: String,
        @RequestParam("friendId") friendId: String,
    ): HttpResponse<DirectChatRoomResponse> {
        val convertFiles = FileHelper.convertMultipartFileToFileDataList(files)
        val directChatRoom = directChatFacade.processCreateDirectChatRoomFilesChat(UserId.of(userId), UserId.of(friendId), convertFiles)
        return HttpResponse.success(DirectChatRoomResponse.of(directChatRoom))
    }

    @PostMapping("/direct/delete")
    fun deleteDirectChatRoom(
        @CurrentUser userId: UserId,
        @RequestBody request: ChatRoomRequest.Delete,
    ): HttpResponse<SuccessOnlyResponse> {
        directChatRoomService.deleteDirectChatRoom(userId, request.toChatRoomId())
        return HttpResponse.successOnly()
    }

    @PutMapping("/direct/favorite")
    fun favoriteDirectChatRoom(
        @CurrentUser userId: UserId,
        @RequestBody request: ChatRoomRequest.Favorite,
    ): HttpResponse<SuccessOnlyResponse> {
        directChatRoomService.favoriteDirectChatRoomType(userId, request.toChatRoomId(), request.toFavorite())
        return HttpResponse.successOnly()
    }

    @PostMapping("/group/create")
    fun produceGroupChatRoom(
        @CurrentUser userId: UserId,
        @RequestBody request: ChatRoomRequest.CreateGroup,
    ): HttpResponse<GroupChatRoomResponse> {
        val groupChatRoom = groupChatFacade.processGroupChatCreate(userId, request.toFriendIds(), request.toName())
        return HttpResponse.success(GroupChatRoomResponse.of(groupChatRoom, userId))
    }

    @PutMapping("/group/favorite")
    fun favoriteGroupChatRoom(
        @CurrentUser userId: UserId,
        @RequestBody request: ChatRoomRequest.Favorite,
    ): HttpResponse<SuccessOnlyResponse> {
        groupChatRoomService.favoriteGroupChatRoomType(userId, request.toChatRoomId(), request.toFavorite())
        return HttpResponse.successOnly()
    }

    @PutMapping("/group/invite")
    fun inviteGroupChatRoom(
        @CurrentUser userId: UserId,
        @RequestBody request: ChatRoomRequest.Invite,
    ): HttpResponse<SuccessOnlyResponse> {
        groupChatFacade.processGroupChatInvite(request.toChatRoomId(), userId, request.toFriendId())
        return HttpResponse.successOnly()
    }

    @DeleteMapping("/group/leave")
    fun leaveGroupChatRoom(
        @CurrentUser userId: UserId,
        @RequestBody request: ChatRoomRequest.Leave,
    ): HttpResponse<SuccessOnlyResponse> {
        groupChatFacade.processGroupChatLeave(request.toChatRoomId(), userId)
        return HttpResponse.successOnly()
    }
}
