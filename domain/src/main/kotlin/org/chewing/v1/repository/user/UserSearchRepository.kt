package org.chewing.v1.repository.user

import org.chewing.v1.model.friend.UserSearch

interface UserSearchRepository {
    fun appendHistory(userId: String, keyword: String)
    fun readSearchHistory(userId: String): List<UserSearch>
}
