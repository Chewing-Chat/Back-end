package org.chewing.v1.implementation.search

import org.chewing.v1.model.friend.UserSearch
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.user.UserSearchRepository
import org.springframework.stereotype.Component

@Component
class SearchReader(
    private val userSearchRepository: UserSearchRepository,
) {
    fun readHistory(userId: UserId): List<UserSearch> {
        return userSearchRepository.readSearchHistory(userId)
    }
}
