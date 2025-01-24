package org.chewing.v1.repository.user

import org.chewing.v1.model.user.UserEmoticonPackInfo
import org.chewing.v1.model.user.UserId

interface UserEmoticonRepository {
    fun readUserEmoticons(userId: UserId): List<UserEmoticonPackInfo>
}
