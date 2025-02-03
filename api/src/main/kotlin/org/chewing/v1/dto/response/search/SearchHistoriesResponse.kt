package org.chewing.v1.dto.response.search

import org.chewing.v1.model.friend.UserSearch

data class SearchHistoriesResponse(
    val keywords: List<SearchHistoryResponse>,
) {
    companion object {
        fun ofList(keywords: List<UserSearch>): SearchHistoriesResponse {
            return SearchHistoriesResponse(
                keywords = keywords.map { SearchHistoryResponse.of(it.keyword) },
            )
        }
    }
}
