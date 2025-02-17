package org.chewing.v1.implementation.chat.message

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.chat.log.ChatLog
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ChatValidator {
    fun isPossibleDeleteMessage(chatLog: ChatLog) {
        val deleteThreshold = LocalDateTime.now().minusMinutes(5)
        if (chatLog.timestamp.isBefore(deleteThreshold)) {
            throw ConflictException(ErrorCode.CHATLOG_DELETE_MESSAGE_TIME_LIMIT)
        }
    }
}
