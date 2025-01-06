package org.chewing.v1.controller.search

import org.chewing.v1.dto.request.friend.FriendSearchRequest
import org.chewing.v1.dto.response.search.SearchHistoriesResponse
import org.chewing.v1.dto.response.search.SearchResultResponse
import org.chewing.v1.facade.SearchFacade
import org.chewing.v1.response.SuccessCreateResponse
import org.chewing.v1.service.search.SearchService
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/search")
class SearchController(
    private val searchFacade: SearchFacade,
    private val searchService: SearchService,
) {
    @GetMapping("")
    fun search(
        @RequestAttribute("userId") userId: String,
        @RequestParam("keyword") keyword: String,
    ): SuccessResponseEntity<SearchResultResponse> {
        val search = searchFacade.search(userId, keyword)
        // 성공 응답 200 반환
        return ResponseHelper.success(SearchResultResponse.ofList(search))
    }

    @PostMapping("")
    fun addSearchKeyword(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: FriendSearchRequest,
    ): SuccessResponseEntity<SuccessCreateResponse> {
        searchService.createSearchKeyword(userId, request.keyword)
        // 성공 응답 200 반환
        return ResponseHelper.successCreateOnly()
    }

    @GetMapping("/recent")
    fun getSearchHistory(
        @RequestAttribute("userId") userId: String,
    ): SuccessResponseEntity<SearchHistoriesResponse> {
        val searchKeywords = searchService.getSearchKeywords(userId)
        // 성공 응답 200 반환
        return ResponseHelper.success(SearchHistoriesResponse.ofList(searchKeywords))
    }
}
