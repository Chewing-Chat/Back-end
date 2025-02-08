package org.chewing.v1.facade

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.media.FileData
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.chat.ChatLogService
import org.chewing.v1.service.chat.GroupChatRoomService
import org.chewing.v1.service.notification.NotificationService
import org.springframework.stereotype.Service

@Service
class GroupChatFacade(
    private val chatLogService: ChatLogService,
    private val groupChatRoomService: GroupChatRoomService,
    private val notificationService: NotificationService,
) {

    fun processGroupChatLogs(userId: UserId, chatRoomId: ChatRoomId, sequenceNumber: Int): List<ChatLog> {
        val joinSequence =
            groupChatRoomService.getGroupChatRoom(userId, chatRoomId).chatRoomOwnSequence.joinSequenceNumber
        val chatLogs = chatLogService.getChatLog(chatRoomId, sequenceNumber, joinSequence)
        return chatLogs
    }
    fun processGroupChatFiles(fileDataList: List<FileData>, userId: UserId, chatRoomId: ChatRoomId) {
        val medias = chatLogService.uploadFiles(fileDataList, userId)
        val chatSequence = groupChatRoomService.increaseGroupChatRoomSequence(chatRoomId)
        val chatMessage = chatLogService.mediasMessage(chatRoomId, userId, chatSequence, medias, ChatRoomType.GROUP)

        val targetFriendIds = getFriendIds(userId, chatRoomId)

        notificationService.handleMessagesNotification(chatMessage, targetFriendIds, userId)
        notificationService.handleOwnedMessageNotification(chatMessage)
    }

    fun processGroupChatRead(chatRoomId: ChatRoomId, userId: UserId, sequenceNumber: Int) {
        try {
            groupChatRoomService.readGroupChatRoom(userId, chatRoomId, sequenceNumber)
            val chatMessage =
                chatLogService.readMessage(
                    chatRoomId,
                    userId,
                    ChatRoomSequence.of(chatRoomId, sequenceNumber),
                    ChatRoomType.GROUP,
                )
            notificationService.handleOwnedMessageNotification(chatMessage)
        } catch (e: NotFoundException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.GROUP)
            notificationService.handleOwnedMessageNotification(errorMessage)
        }
    }

    fun processGroupChatDelete(chatRoomId: ChatRoomId, userId: UserId, messageId: String) {
        try {
            groupChatRoomService.deleteGroupChatRoom(userId, chatRoomId)
            val chatMessage = chatLogService.deleteMessage(chatRoomId, userId, messageId, ChatRoomType.GROUP)
            val targetFriendIds = getFriendIds(userId, chatRoomId)

            notificationService.handleMessagesNotification(chatMessage, targetFriendIds, userId)
            notificationService.handleOwnedMessageNotification(chatMessage)
        } catch (e: NotFoundException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.GROUP)
            notificationService.handleOwnedMessageNotification(errorMessage)
        }
    }

    fun processGroupChatReply(chatRoomId: ChatRoomId, userId: UserId, parentMessageId: String, text: String) {
        try {
            val chatSequence = groupChatRoomService.increaseGroupChatRoomSequence(chatRoomId)
            val chatMessage =
                chatLogService.replyMessage(chatRoomId, userId, parentMessageId, text, chatSequence, ChatRoomType.GROUP)
            val targetFriendIds = getFriendIds(userId, chatRoomId)

            notificationService.handleMessagesNotification(chatMessage, targetFriendIds, userId)
            notificationService.handleOwnedMessageNotification(chatMessage)
        } catch (e: NotFoundException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.GROUP)
            notificationService.handleOwnedMessageNotification(errorMessage)
        }
    }

    fun processGroupChatCommon(chatRoomId: ChatRoomId, userId: UserId, text: String) {
        try {
            val chatSequence = groupChatRoomService.increaseGroupChatRoomSequence(chatRoomId)
            val chatMessage =
                chatLogService.chatNormalMessage(chatRoomId, userId, text, chatSequence, ChatRoomType.GROUP)

            val targetFriendIds = getFriendIds(userId, chatRoomId)

            notificationService.handleMessagesNotification(chatMessage, targetFriendIds, userId)
            notificationService.handleOwnedMessageNotification(chatMessage)
        } catch (e: NotFoundException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.GROUP)
            notificationService.handleOwnedMessageNotification(errorMessage)
        }
    }

    fun processGroupChatInvite(chatRoomId: ChatRoomId, userId: UserId, inviteUserId: UserId) {
        try {
            val chatSequence = groupChatRoomService.increaseGroupChatRoomSequence(chatRoomId)
            groupChatRoomService.inviteGroupChatRoom(userId, chatRoomId, inviteUserId)
            val chatMessage =
                chatLogService.inviteMessage(chatRoomId, userId, inviteUserId, chatSequence, ChatRoomType.GROUP)
            notificationService.handleOwnedMessageNotification(chatMessage)
            notificationService.handleMessageNotification(chatMessage, inviteUserId, userId)
        } catch (e: ConflictException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.GROUP)
            notificationService.handleOwnedMessageNotification(errorMessage)
        }
    }

    fun processGroupChatLeave(chatRoomId: ChatRoomId, userId: UserId) {
        try {
            val chatSequence = groupChatRoomService.increaseGroupChatRoomSequence(chatRoomId)
            val chatMessage = chatLogService.leaveMessage(chatRoomId, userId, chatSequence, ChatRoomType.GROUP)
            notificationService.handleOwnedMessageNotification(chatMessage)
        } catch (e: NotFoundException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.GROUP)
            notificationService.handleOwnedMessageNotification(errorMessage)
        }
    }

    fun processGroupChatCreate(chatRoomId: ChatRoomId, userId: UserId, friendIds: List<UserId>, groupName: String) {
        try {
            val chatRoom = groupChatRoomService.produceGroupChatRoom(userId,friendIds, groupName)
            val chatMessage =
                chatLogService.inviteMessages(friendIds, chatRoomId, userId, chatRoom.chatRoomSequence, ChatRoomType.GROUP)
            notificationService.handleOwnedMessageNotification(chatMessage)
        } catch (e: NotFoundException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.GROUP)
            notificationService.handleOwnedMessageNotification(errorMessage)
        }
    }

    private fun getFriendIds(userId: UserId, chatRoomId: ChatRoomId): List<UserId> {
        val groupChatRoom = groupChatRoomService.getGroupChatRoom(userId, chatRoomId)
        return groupChatRoom.chatRoomMembers
            .asSequence()
            .map { it.memberId }
            .filter { it != userId }
            .toList()
    }
}
