package org.chewing.v1.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
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
    JsonSubTypes.Type(value = ChatMessageDto.Bomb::class, name = "Bomb"),
    JsonSubTypes.Type(value = ChatMessageDto.Read::class, name = "Read"),
)
sealed class ChatMessageDto {
    data class Reply(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val parentMessageId: String,
        val parentMessagePage: Int,
        val parentSeqNumber: Int,
        val parentMessageText: String,
        val timestamp: String,
        val seqNumber: Int,
        val page: Int,
        val text: String,
    ) : ChatMessageDto()

    data class Delete(
        val targetMessageId: String,
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
        val page: Int,
    ) : ChatMessageDto()

    data class Leave(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
        val page: Int,
    ) : ChatMessageDto()

    data class Invite(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
        val page: Int,
    ) : ChatMessageDto()

    data class File(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
        val page: Int,
        val files: List<MediaDto>,
    ) : ChatMessageDto()

    data class Normal(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
        val page: Int,
        val text: String,
    ) : ChatMessageDto()

    data class Bomb(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
        val page: Int,
        val expiredAt: String,
        val text: String,
    ) : ChatMessageDto()

    data class Read(
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
        val page: Int,
    ) : ChatMessageDto()

    companion object {
        fun from(chatMessage: ChatMessage): ChatMessageDto {
            val dateTimeFormatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")
            val formattedTime = chatMessage.timestamp.format(dateTimeFormatter)
            return when (chatMessage) {
                is ChatReplyMessage -> Reply(
                    messageId = chatMessage.messageId,
                    type = chatMessage.type.toString().lowercase(),
                    chatRoomId = chatMessage.chatRoomId,
                    senderId = chatMessage.senderId,
                    parentMessageId = chatMessage.parentMessageId,
                    parentMessagePage = chatMessage.parentMessagePage,
                    parentSeqNumber = chatMessage.parentSeqNumber,
                    parentMessageText = chatMessage.parentMessageText,
                    timestamp = chatMessage.timestamp.format(dateTimeFormatter),
                    seqNumber = chatMessage.number.sequenceNumber,
                    page = chatMessage.number.page,
                    text = chatMessage.text,
                )

                is ChatLeaveMessage -> Leave(
                    messageId = chatMessage.messageId,
                    type = chatMessage.type.toString().lowercase(),
                    chatRoomId = chatMessage.chatRoomId,
                    senderId = chatMessage.senderId,
                    timestamp = formattedTime,
                    seqNumber = chatMessage.number.sequenceNumber,
                    page = chatMessage.number.page,
                )

                is ChatInviteMessage -> Invite(
                    messageId = chatMessage.messageId,
                    type = chatMessage.type.toString().lowercase(),
                    chatRoomId = chatMessage.chatRoomId,
                    senderId = chatMessage.senderId,
                    timestamp = formattedTime,
                    seqNumber = chatMessage.number.sequenceNumber,
                    page = chatMessage.number.page,
                )

                is ChatFileMessage -> File(
                    messageId = chatMessage.messageId,
                    type = chatMessage.type.toString().lowercase(),
                    chatRoomId = chatMessage.chatRoomId,
                    senderId = chatMessage.senderId,
                    timestamp = formattedTime,
                    seqNumber = chatMessage.number.sequenceNumber,
                    page = chatMessage.number.page,
                    files = chatMessage.medias.map { MediaDto.from(it) },
                )

                is ChatNormalMessage -> Normal(
                    messageId = chatMessage.messageId,
                    type = chatMessage.type.toString().lowercase(),
                    chatRoomId = chatMessage.chatRoomId,
                    senderId = chatMessage.senderId,
                    timestamp = formattedTime,
                    seqNumber = chatMessage.number.sequenceNumber,
                    page = chatMessage.number.page,
                    text = chatMessage.text,
                )

                is ChatBombMessage -> Bomb(
                    messageId = chatMessage.messageId,
                    type = chatMessage.type.toString().lowercase(),
                    chatRoomId = chatMessage.chatRoomId,
                    senderId = chatMessage.senderId,
                    timestamp = formattedTime,
                    seqNumber = chatMessage.number.sequenceNumber,
                    page = chatMessage.number.page,
                    expiredAt = chatMessage.expiredAt.format(dateTimeFormatter),
                    text = chatMessage.text,
                )

                is ChatReadMessage -> Read(
                    type = chatMessage.type.toString().lowercase(),
                    chatRoomId = chatMessage.chatRoomId,
                    senderId = chatMessage.senderId,
                    timestamp = formattedTime,
                    seqNumber = chatMessage.number.sequenceNumber,
                    page = chatMessage.number.page,
                )

                is ChatDeleteMessage -> Delete(
                    targetMessageId = chatMessage.targetMessageId,
                    type = chatMessage.type.toString().lowercase(),
                    chatRoomId = chatMessage.chatRoomId,
                    senderId = chatMessage.senderId,
                    timestamp = formattedTime,
                    seqNumber = chatMessage.number.sequenceNumber,
                    page = chatMessage.number.page,
                )
            }
        }
    }
}
