package org.chewing.v1.repository.user

import org.chewing.v1.model.user.UserEmoticonPackInfo

interface UserEmoticonRepository {
    fun readUserEmoticons(userId: String): List<UserEmoticonPackInfo>
}
