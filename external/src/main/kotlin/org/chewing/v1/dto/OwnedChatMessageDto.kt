package org.chewing.v1.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.chewing.v1.domain.ChatMessageOwner
import org.chewing.v1.model.chat.message.ChatDeleteMessage
import org.chewing.v1.model.chat.message.ChatErrorMessage
import org.chewing.v1.model.chat.message.ChatFileMessage
import org.chewing.v1.model.chat.message.ChatInviteMessage
import org.chewing.v1.model.chat.message.ChatLeaveMessage
import org.chewing.v1.model.chat.message.ChatMessage
import org.chewing.v1.model.chat.message.ChatNormalMessage
import org.chewing.v1.model.chat.message.ChatReadMessage
import org.chewing.v1.model.chat.message.ChatReplyMessage
import org.chewing.v1.model.chat.room.ChatRoomType
import java.time.format.DateTimeFormatter


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
)
@JsonSubTypes(
    JsonSubTypes.Type(value = OwnedChatMessageDto.Reply::class, name = "Reply"),
    JsonSubTypes.Type(value = OwnedChatMessageDto.Delete::class, name = "Delete"),
    JsonSubTypes.Type(value = OwnedChatMessageDto.Leave::class, name = "Leave"),
    JsonSubTypes.Type(value = OwnedChatMessageDto.Invite::class, name = "Invite"),
    JsonSubTypes.Type(value = OwnedChatMessageDto.File::class, name = "File"),
    JsonSubTypes.Type(value = OwnedChatMessageDto.Normal::class, name = "Message"),
    JsonSubTypes.Type(value = OwnedChatMessageDto.Read::class, name = "Read"),
    JsonSubTypes.Type(value = OwnedChatMessageDto.Error::class, name = "Error"),
)
sealed class OwnedChatMessageDto {
    data class Reply(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val parentMessageId: String,
        val parentSeqNumber: Int,
        val parentMessageText: String,
        val timestamp: String,
        val seqNumber: Int,
        val text: String,
        val owner: String = ChatMessageOwner.ME.name.lowercase(),
        val chatRoomType: String
    ) : ChatMessageDto()

    data class Delete(
        val targetMessageId: String,
        val type: String,
        val chatRoomId: String,
        val timestamp: String,
        val seqNumber: Int,
        val owner: String = ChatMessageOwner.ME.name.lowercase(),
        val chatRoomType: String
    ) : ChatMessageDto()

    data class Leave(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val timestamp: String,
        val seqNumber: Int,
        val owner: String = ChatMessageOwner.ME.name.lowercase(),
        val chatRoomType: String
    ) : ChatMessageDto()

    data class Invite(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val timestamp: String,
        val seqNumber: Int,
        val owner: String = ChatMessageOwner.ME.name.lowercase(),
        val chatRoomType: String
    ) : ChatMessageDto()

    data class File(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val timestamp: String,
        val seqNumber: Int,
        val files: List<MediaDto>,
        val owner: String = ChatMessageOwner.ME.name.lowercase(),
        val chatRoomType: String
    ) : ChatMessageDto()

    data class Normal(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val timestamp: String,
        val seqNumber: Int,
        val text: String,
        val owner: String = ChatMessageOwner.ME.name.lowercase(),
        val chatRoomType: String
    ) : ChatMessageDto()

    data class Read(
        val type: String,
        val chatRoomId: String,
        val timestamp: String,
        val seqNumber: Int,
        val owner: String = ChatMessageOwner.ME.name.lowercase(),
        val chatRoomType: String
    ) : ChatMessageDto()

    data class Error(
        val type: String,
        val chatRoomId: String,
        val timestamp: String,
        val errorCode: String,
        val errorMessage: String,
        val owner: String = ChatMessageOwner.ME.name.lowercase(),
        val chatRoomType: String
    ) : ChatMessageDto()

    companion object {
        fun from(chatMessage: ChatMessage): ChatMessageDto {
            val dateTimeFormatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")
            val formattedTime = chatMessage.timestamp.format(dateTimeFormatter)
            return when (chatMessage) {
                is ChatReplyMessage -> Reply(
                    messageId = chatMessage.messageId,
                    type = chatMessage.type.name.lowercase(),
                    chatRoomId = chatMessage.chatRoomId.id,
                    parentMessageId = chatMessage.parentMessageId,
                    parentSeqNumber = chatMessage.parentSeqNumber,
                    parentMessageText = chatMessage.parentMessageText,
                    timestamp = chatMessage.timestamp.format(dateTimeFormatter),
                    seqNumber = chatMessage.number.sequenceNumber,
                    text = chatMessage.text,
                    chatRoomType = chatMessage.chatRoomType.name.lowercase(),
                )

                is ChatLeaveMessage -> Leave(
                    messageId = chatMessage.messageId,
                    type = chatMessage.type.name.lowercase(),
                    chatRoomId = chatMessage.chatRoomId.id,
                    timestamp = formattedTime,
                    seqNumber = chatMessage.number.sequenceNumber,
                    chatRoomType = chatMessage.chatRoomType.name.lowercase(),
                )

                is ChatInviteMessage -> Invite(
                    messageId = chatMessage.messageId,
                    type = chatMessage.type.name.lowercase(),
                    chatRoomId = chatMessage.chatRoomId.id,
                    timestamp = formattedTime,
                    seqNumber = chatMessage.number.sequenceNumber,
                    chatRoomType = chatMessage.chatRoomType.name.lowercase(),
                )

                is ChatFileMessage -> File(
                    messageId = chatMessage.messageId,
                    type = chatMessage.type.name.lowercase(),
                    chatRoomId = chatMessage.chatRoomId.id,
                    timestamp = formattedTime,
                    seqNumber = chatMessage.number.sequenceNumber,
                    files = chatMessage.medias.map { MediaDto.from(it) },
                    chatRoomType = chatMessage.chatRoomType.name.lowercase(),
                )

                is ChatNormalMessage -> Normal(
                    messageId = chatMessage.messageId,
                    type = chatMessage.type.name.lowercase(),
                    chatRoomId = chatMessage.chatRoomId.id,
                    timestamp = formattedTime,
                    seqNumber = chatMessage.number.sequenceNumber,
                    text = chatMessage.text,
                    chatRoomType = chatMessage.chatRoomType.name.lowercase(),
                )

                is ChatReadMessage -> Read(
                    type = chatMessage.type.name.lowercase(),
                    chatRoomId = chatMessage.chatRoomId.id,
                    timestamp = formattedTime,
                    seqNumber = chatMessage.number.sequenceNumber,
                    chatRoomType = chatMessage.chatRoomType.name.lowercase(),
                )

                is ChatDeleteMessage -> Delete(
                    targetMessageId = chatMessage.targetMessageId,
                    type = chatMessage.type.name.lowercase(),
                    chatRoomId = chatMessage.chatRoomId.id,
                    timestamp = formattedTime,
                    seqNumber = chatMessage.number.sequenceNumber,
                    chatRoomType = chatMessage.chatRoomType.name.lowercase(),
                )

                is ChatErrorMessage -> Error(
                    type = chatMessage.type.name.lowercase(),
                    chatRoomId = chatMessage.chatRoomId.id,
                    timestamp = formattedTime,
                    errorCode = chatMessage.errorCode.code,
                    errorMessage = chatMessage.errorCode.message,
                    chatRoomType = chatMessage.chatRoomType.name.lowercase(),
                )
            }
        }
    }
}
