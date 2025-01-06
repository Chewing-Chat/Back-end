package org.chewing.v1.implementation.search

import org.chewing.v1.repository.user.UserSearchRepository
import org.springframework.stereotype.Component

@Component
class SearchAppender(
    private val userSearchRepository: UserSearchRepository,
) {
    fun append(userId: String, keyword: String) {
        userSearchRepository.appendHistory(userId, keyword)
    }
}
