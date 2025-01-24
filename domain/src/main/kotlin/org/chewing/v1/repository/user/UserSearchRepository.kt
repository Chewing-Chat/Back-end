package org.chewing.v1.repository.user

import org.chewing.v1.model.friend.UserSearch
import org.chewing.v1.model.user.UserId

interface UserSearchRepository {
    fun appendHistory(userId: UserId, keyword: String)
    fun readSearchHistory(userId: UserId): List<UserSearch>
}
