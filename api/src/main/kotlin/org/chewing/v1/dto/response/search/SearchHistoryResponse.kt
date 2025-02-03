package org.chewing.v1.dto.response.search

data class SearchHistoryResponse(
    val keyword: String,
) {
    companion object {
        fun of(keyword: String): SearchHistoryResponse {
            return SearchHistoryResponse(
                keyword = keyword,
            )
        }
    }
}
