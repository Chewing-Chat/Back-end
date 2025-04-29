package org.chewing.v1.implementation.ai

import org.chewing.v1.model.user.UserId
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AiUserGenerator(
    @Value("\${ai.user-id}")
    private val aiUserId: String,
) {
    fun getAiUserId(): UserId {
        return UserId.of(aiUserId)
    }
}
