package org.chewing.v1.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.chat.message.*
import java.time.format.DateTimeFormatter

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ChatMessageDto.Reply::class, name = "Reply"),
    JsonSubTypes.Type(value = ChatMessageDto.Delete::class, name = "Delete"),
    JsonSubTypes.Type(value = ChatMessageDto.Leave::class, name = "Leave"),
    JsonSubTypes.Type(value = ChatMessageDto.Invite::class, name = "Invite"),
    JsonSubTypes.Type(value = ChatMessageDto.File::class, name = "File"),
    JsonSubTypes.Type(value = ChatMessageDto.Normal::class, name = "Message"),
    JsonSubTypes.Type(value = ChatMessageDto.Read::class, name = "Read"),
    JsonSubTypes.Type(value = ChatMessageDto.Error::class, name = "Error"),
)
sealed class ChatMessageDto {
    data class Reply(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val parentMessageId: String,
        val parentSeqNumber: Int,
        val parentMessageText: String,
        val timestamp: String,
        val seqNumber: Int,
        val text: String,
        val chatRoomType: String,
    ) : ChatMessageDto()

    data class Delete(
        val targetMessageId: String,
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
        val chatRoomType: String,
    ) : ChatMessageDto()

    data class Leave(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
        val chatRoomType: String,
    ) : ChatMessageDto()

    data class Invite(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
        val chatRoomType: String,
    ) : ChatMessageDto()

    data class File(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
        val files: List<MediaDto>,
        val chatRoomType: String,
    ) : ChatMessageDto()

    data class Normal(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
        val text: String,
        val chatRoomType: String,
    ) : ChatMessageDto()

    data class Read(
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
        val chatRoomType: String,
    ) : ChatMessageDto()

    data class Error(
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val timestamp: String,
        val errorCode: String,
        val errorMessage: String,
        val chatRoomType: String,
    ) : ChatMessageDto()

    data class Comment(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
        val chatRoomType: String,
        val files: List<MediaDto>,
        val feedId: String,
        val feedType: String,
        val comment: String,
        val content: String,
    ) : ChatMessageDto()

    companion object {
        fun from(chatMessage: ChatMessage): ChatMessageDto {
            val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formattedTime = chatMessage.timestamp.format(dateTimeFormatter)
            return when (chatMessage) {
                is ChatReplyMessage -> Reply(
                    messageId = chatMessage.messageId,
                    type = chatMessage.type.name.lowercase(),
                    chatRoomId = chatMessage.chatRoomId.id,
                    senderId = chatMessage.senderId.id,
                    parentMessageId = chatMessage.parentMessageId,
                    parentSeqNumber = chatMessage.parentSeqNumber,
                    parentMessageText = chatMessage.parentMessageText,
                    timestamp = chatMessage.timestamp.format(dateTimeFormatter),
                    seqNumber = chatMessage.roomSequence.sequence,
                    text = chatMessage.text,
                    chatRoomType = chatMessage.chatRoomType.name.lowercase(),
                )

                is ChatLeaveMessage -> Leave(
                    messageId = chatMessage.messageId,
                    type = chatMessage.type.name.lowercase(),
                    chatRoomId = chatMessage.chatRoomId.id,
                    senderId = chatMessage.senderId.id,
                    timestamp = formattedTime,
                    seqNumber = chatMessage.roomSequence.sequence,
                    chatRoomType = chatMessage.chatRoomType.name.lowercase(),
                )

                is ChatInviteMessage -> Invite(
                    messageId = chatMessage.messageId,
                    type = chatMessage.type.name.lowercase(),
                    chatRoomId = chatMessage.chatRoomId.id,
                    senderId = chatMessage.senderId.id,
                    timestamp = formattedTime,
                    seqNumber = chatMessage.roomSequence.sequence,
                    chatRoomType = chatMessage.chatRoomType.name.lowercase(),
                )

                is ChatFileMessage -> File(
                    messageId = chatMessage.messageId,
                    type = chatMessage.type.name.lowercase(),
                    chatRoomId = chatMessage.chatRoomId.id,
                    senderId = chatMessage.senderId.id,
                    timestamp = formattedTime,
                    seqNumber = chatMessage.roomSequence.sequence,
                    files = chatMessage.medias.map { MediaDto.from(it) },
                    chatRoomType = chatMessage.chatRoomType.name.lowercase(),
                )

                is ChatNormalMessage -> Normal(
                    messageId = chatMessage.messageId,
                    type = chatMessage.type.name.lowercase(),
                    chatRoomId = chatMessage.chatRoomId.id,
                    senderId = chatMessage.senderId.id,
                    timestamp = formattedTime,
                    seqNumber = chatMessage.roomSequence.sequence,
                    text = chatMessage.text,
                    chatRoomType = chatMessage.chatRoomType.name.lowercase(),
                )

                is ChatReadMessage -> Read(
                    type = chatMessage.type.name.lowercase(),
                    chatRoomId = chatMessage.chatRoomId.id,
                    senderId = chatMessage.senderId.id,
                    timestamp = formattedTime,
                    seqNumber = chatMessage.roomSequence.sequence,
                    chatRoomType = chatMessage.chatRoomType.name.lowercase(),
                )

                is ChatDeleteMessage -> Delete(
                    targetMessageId = chatMessage.targetMessageId,
                    type = chatMessage.type.name.lowercase(),
                    chatRoomId = chatMessage.chatRoomId.id,
                    senderId = chatMessage.senderId.id,
                    timestamp = formattedTime,
                    seqNumber = chatMessage.roomSequence.sequence,
                    chatRoomType = chatMessage.chatRoomType.name.lowercase(),
                )

                is ChatErrorMessage -> Error(
                    type = chatMessage.type.name.lowercase(),
                    chatRoomId = chatMessage.chatRoomId.id,
                    senderId = chatMessage.senderId.id,
                    timestamp = formattedTime,
                    errorCode = chatMessage.errorCode.code,
                    errorMessage = chatMessage.errorCode.message,
                    chatRoomType = chatMessage.chatRoomType.name.lowercase(),
                )

                is ChatCommentMessage -> Comment(
                    messageId = chatMessage.messageId,
                    type = chatMessage.type.name.lowercase(),
                    chatRoomId = chatMessage.chatRoomId.id,
                    senderId = chatMessage.senderId.id,
                    timestamp = formattedTime,
                    seqNumber = chatMessage.roomSequence.sequence,
                    chatRoomType = chatMessage.chatRoomType.name.lowercase(),
                    files = chatMessage.medias.map { MediaDto.from(it) },
                    feedId = chatMessage.feedId.id,
                    feedType = chatMessage.feedType.name.lowercase(),
                    comment = chatMessage.comment,
                    content = chatMessage.content,
                )

                is ChatAiMessage -> throw ConflictException(ErrorCode.AI_WEBSOCKET_NOT_SUPPORTED)
            }
        }
    }
}
