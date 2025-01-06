package org.chewing.v1.service.search

import org.chewing.v1.implementation.search.SearchAppender
import org.chewing.v1.implementation.search.SearchReader
import org.chewing.v1.model.friend.UserSearch
import org.springframework.stereotype.Service

@Service
class SearchService(
    private val searchReader: SearchReader,
    private val searchAppender: SearchAppender,
) {
    fun createSearchKeyword(userId: String, keyword: String) {
        searchAppender.append(userId, keyword)
    }

    fun getSearchKeywords(userId: String): List<UserSearch> {
        return searchReader.readHistory(userId)
    }
}
