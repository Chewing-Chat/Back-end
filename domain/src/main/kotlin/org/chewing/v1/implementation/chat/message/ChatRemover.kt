package org.chewing.v1.implementation.chat.message

import org.chewing.v1.repository.chat.ChatLogRepository
import org.springframework.stereotype.Component

@Component
class ChatRemover(
    private val chatLogRepository: ChatLogRepository,
) {
    fun removeChatLog(messageId: String) {
        chatLogRepository.removeLog(messageId)
    }
}
