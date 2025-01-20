package org.chewing.v1.implementation.user.emoticon

import org.chewing.v1.model.user.UserEmoticonPackInfo
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.user.UserEmoticonRepository
import org.springframework.stereotype.Component

@Component
class UserEmoticonReader(
    private val userEmoticonRepository: UserEmoticonRepository,
) {
    fun readUserEmoticonPacks(userId: UserId): List<UserEmoticonPackInfo> {
        return userEmoticonRepository.readUserEmoticons(userId)
    }
}
