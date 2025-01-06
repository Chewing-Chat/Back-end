package org.chewing.v1.model.friend

import java.time.LocalDateTime

class UserSearch private constructor(
    val keyword: String,
    val searchAt: LocalDateTime,
) {
    companion object {
        fun of(keyword: String, searchTime: LocalDateTime): UserSearch {
            return UserSearch(keyword, searchTime)
        }
    }
}
