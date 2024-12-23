package org.chewing.v1.implementation.user.emoticon

import org.chewing.v1.model.user.UserEmoticonPackInfo
import org.chewing.v1.repository.user.UserEmoticonRepository
import org.springframework.stereotype.Component

@Component
class UserEmoticonReader(
    private val userEmoticonRepository: UserEmoticonRepository,
) {
    fun readUserEmoticonPacks(userId: String): List<UserEmoticonPackInfo> {
        return userEmoticonRepository.readUserEmoticons(userId)
    }
}
