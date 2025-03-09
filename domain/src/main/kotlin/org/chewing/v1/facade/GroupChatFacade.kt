package org.chewing.v1.facade

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.UnReadTarget
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.chat.room.GroupChatRoom
import org.chewing.v1.model.media.FileData
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.chat.ChatLogService
import org.chewing.v1.service.chat.GroupChatRoomService
import org.chewing.v1.service.friend.FriendShipService
import org.chewing.v1.service.notification.NotificationService
import org.chewing.v1.service.user.UserService
import org.springframework.stereotype.Service

@Service
class GroupChatFacade(
    private val chatLogService: ChatLogService,
    private val groupChatRoomService: GroupChatRoomService,
    private val notificationService: NotificationService,
    private val friendShipService: FriendShipService,
    private val userService: UserService,
) {

    fun processGroupChatLogs(userId: UserId, chatRoomId: ChatRoomId, sequenceNumber: Int): List<ChatLog> {
        val joinSequence =
            groupChatRoomService.getGroupChatRoom(userId, chatRoomId).ownSequence.joinSequenceNumber
        val chatLogs = chatLogService.getChatLogs(chatRoomId, sequenceNumber, joinSequence)
        return chatLogs
    }

    fun processGroupLatestChatLogs(userId: UserId, chatRoomId: ChatRoomId): List<ChatLog> {
        val groupChatRoom =
            groupChatRoomService.getGroupChatRoom(userId, chatRoomId)
        val chatLogs = chatLogService.getLatestChatLogs(chatRoomId, groupChatRoom.ownSequence.joinSequenceNumber)
        groupChatRoomService.readGroupChatRoom(userId, chatRoomId, groupChatRoom.roomSequence.sequence)
        return chatLogs
    }

    fun processGroupChatRooms(userId: UserId): List<Pair<GroupChatRoom, ChatLog>> {
        val chatRooms = groupChatRoomService.getGroupChatRooms(userId)
        val chatRoomIds = getChatRoomIds(chatRooms)
        val chatMessages = chatLogService.getsLatestChatLog(chatRoomIds)
            .associateBy { it.chatRoomId }

        return chatRooms.mapNotNull { chatRoom ->
            chatMessages[chatRoom.roomInfo.chatRoomId]?.let { latestMessage ->
                chatRoom to latestMessage
            }
        }.sortedByDescending { it.second.timestamp }
    }

    fun searchGroupChatRooms(userId: UserId, friendIds: List<UserId>): List<Pair<GroupChatRoom, ChatLog>> {
        val chatRooms = groupChatRoomService.searchGroupChatRooms(userId, friendIds)
        val chatRoomIds = getChatRoomIds(chatRooms)
        val chatMessages = chatLogService.getsLatestChatLog(chatRoomIds)
            .associateBy { it.chatRoomId }

        return chatRooms.mapNotNull { chatRoom ->
            chatMessages[chatRoom.roomInfo.chatRoomId]?.let { latestMessage ->
                chatRoom to latestMessage
            }
        }.sortedByDescending { it.second.timestamp }
    }

    fun processGroupChatFiles(fileDataList: List<FileData>, userId: UserId, chatRoomId: ChatRoomId) {
        val medias = chatLogService.uploadFiles(fileDataList, userId)
        val chatSequence = groupChatRoomService.increaseGroupChatRoomSequence(chatRoomId)
        val chatMessage = chatLogService.mediasMessage(chatRoomId, userId, chatSequence, medias, ChatRoomType.GROUP)

        val targetMemberIds = getMemberIds(userId, chatRoomId)

        groupChatRoomService.readGroupChatRoom(userId, chatRoomId, chatMessage.roomSequence.sequence)

        notificationService.handleMessagesNotification(chatMessage, targetMemberIds, userId)
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
            notificationService.handleMessageNotification(chatMessage, userId, userId)
        } catch (e: NotFoundException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.GROUP)
            notificationService.handleMessageNotification(errorMessage, userId, userId)
        }
    }

    fun processGroupChatDelete(chatRoomId: ChatRoomId, userId: UserId, messageId: String) {
        try {
            val targetMemberIds = getMemberIds(userId, chatRoomId)

            val chatMessage = chatLogService.deleteMessage(chatRoomId, userId, messageId, ChatRoomType.GROUP)
            notificationService.handleMessagesNotification(chatMessage, targetMemberIds, userId)
        } catch (e: NotFoundException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.GROUP)
            notificationService.handleMessageNotification(errorMessage, userId, userId)
        } catch (e: ConflictException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.GROUP)
            notificationService.handleMessageNotification(errorMessage, userId, userId)
        }
    }

    fun processGroupChatReply(chatRoomId: ChatRoomId, userId: UserId, parentMessageId: String, text: String) {
        try {
            val targetMemberIds = getMemberIds(userId, chatRoomId)

            val chatSequence = groupChatRoomService.increaseGroupChatRoomSequence(chatRoomId)
            val chatMessage =
                chatLogService.replyMessage(chatRoomId, userId, parentMessageId, text, chatSequence, ChatRoomType.GROUP)

            groupChatRoomService.readGroupChatRoom(userId, chatRoomId, chatMessage.roomSequence.sequence)

            notificationService.handleMessagesNotification(chatMessage, targetMemberIds, userId)
        } catch (e: NotFoundException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.GROUP)
            notificationService.handleMessageNotification(errorMessage, userId, userId)
        }
    }

    fun processGroupChatCommon(chatRoomId: ChatRoomId, userId: UserId, text: String) {
        try {
            val targetMemberIds = getMemberIds(userId, chatRoomId)

            val chatSequence = groupChatRoomService.increaseGroupChatRoomSequence(chatRoomId)
            val chatMessage =
                chatLogService.chatNormalMessage(chatRoomId, userId, text, chatSequence, ChatRoomType.GROUP)

            groupChatRoomService.readGroupChatRoom(userId, chatRoomId, chatMessage.roomSequence.sequence)

            notificationService.handleMessagesNotification(chatMessage, targetMemberIds, userId)
        } catch (e: NotFoundException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.GROUP)
            notificationService.handleMessageNotification(errorMessage, userId, userId)
        }
    }

    fun processGroupChatInvite(chatRoomId: ChatRoomId, userId: UserId, inviteUserId: UserId) {
        try {
            val memberIds = getMemberIds(userId, chatRoomId)
            val chatSequence = groupChatRoomService.increaseGroupChatRoomSequence(chatRoomId)
            groupChatRoomService.inviteGroupChatRoom(userId, chatRoomId, inviteUserId)
            val chatMessage =
                chatLogService.inviteMessage(chatRoomId, userId, inviteUserId, chatSequence, ChatRoomType.GROUP)
            groupChatRoomService.readGroupChatRoom(userId, chatRoomId, chatMessage.roomSequence.sequence)
            notificationService.handleMessagesNotification(chatMessage, memberIds, userId)
        } catch (e: ConflictException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.GROUP)
            notificationService.handleMessageNotification(errorMessage, userId, userId)
        }
    }

    fun processGroupChatLeave(chatRoomId: ChatRoomId, userId: UserId) {
        try {
            groupChatRoomService.deleteGroupChatRoom(userId, chatRoomId)
            val chatSequence = groupChatRoomService.increaseGroupChatRoomSequence(chatRoomId)
            val chatMessage = chatLogService.leaveMessage(chatRoomId, userId, chatSequence, ChatRoomType.GROUP)
            groupChatRoomService.readGroupChatRoom(userId, chatRoomId, chatMessage.roomSequence.sequence)
            notificationService.handleMessageNotification(chatMessage, userId, userId)
        } catch (e: NotFoundException) {
            val errorMessage = chatLogService.chatErrorMessages(chatRoomId, userId, e.errorCode, ChatRoomType.GROUP)
            notificationService.handleMessageNotification(errorMessage, userId, userId)
        }
    }

    fun processGetGroupChatRoom(userId: UserId, chatRoomId: ChatRoomId): Pair<GroupChatRoom, ChatLog> {
        val groupChatRoom = groupChatRoomService.getGroupChatRoom(userId, chatRoomId)
        val chatLogs = chatLogService.getLatestChatLog(chatRoomId)
        return groupChatRoom to chatLogs
    }

    fun searchChatLog(userId: UserId, chatRoomId: ChatRoomId, keyword: String): List<ChatLog> {
        groupChatRoomService.validateIsParticipant(chatRoomId, userId)
        return chatLogService.getChatKeyWordMessages(chatRoomId, keyword).sortedByDescending { it.timestamp }
    }

    fun processGroupChatCreate(userId: UserId, friendIds: List<UserId>, groupName: String): Pair<GroupChatRoom, ChatLog> {
        val memberIds = friendIds + userId
        val members = userService.getUsers(memberIds, AccessStatus.ACCESS)
        friendShipService.ensureAllMembersAreFriends(members)
        val chatRoomId = groupChatRoomService.produceGroupChatRoom(userId, friendIds, groupName)
        val chatSequence = groupChatRoomService.increaseGroupChatRoomSequence(chatRoomId)
        val chatMessage =
            chatLogService.inviteMessages(
                friendIds,
                chatRoomId,
                userId,
                chatSequence,
                ChatRoomType.GROUP,
            )
        groupChatRoomService.readGroupChatRoom(userId, chatRoomId, chatMessage.roomSequence.sequence)

        val chatRoom = groupChatRoomService.getGroupChatRoom(userId, chatRoomId)
        val chatLog = chatLogService.getChatLog(chatMessage.messageId)
        notificationService.handleMessagesNotification(chatMessage, friendIds, userId)
        return Pair(chatRoom, chatLog)
    }

    fun processUnreadGroupChatLog(userId: UserId): List<Pair<GroupChatRoom, List<ChatLog>>> {
        val groupChatRooms = groupChatRoomService.getUnreadGroupChatRooms(userId)
        val unReadGroupTargets = groupChatRooms.map {
            UnReadTarget.of(it.roomInfo.chatRoomId, it.roomSequence.sequence, it.ownSequence.readSequenceNumber)
        }
        val chatLogsByRoomId = chatLogService.getUnreadChatLogs(unReadGroupTargets)
            .groupBy { it.chatRoomId }

        return groupChatRooms.mapNotNull { chatRoom ->
            chatLogsByRoomId[chatRoom.roomInfo.chatRoomId]?.let { chatLogs ->
                chatRoom to chatLogs.sortedByDescending { it.timestamp }
            }
        }.sortedByDescending { it.second.first().timestamp }
    }

    private fun getMemberIds(userId: UserId, chatRoomId: ChatRoomId): List<UserId> {
        val groupChatRoom = groupChatRoomService.getGroupChatRoom(userId, chatRoomId)
        return groupChatRoom.memberInfos.map { it.memberId }
    }

    private fun getChatRoomIds(chatRooms: List<GroupChatRoom>): List<ChatRoomId> {
        return chatRooms.map { it.roomInfo.chatRoomId }
    }
}
