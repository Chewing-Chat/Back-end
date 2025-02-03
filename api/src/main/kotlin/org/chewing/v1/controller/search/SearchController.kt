package org.chewing.v1.controller.search

import org.chewing.v1.facade.SearchFacade
import org.chewing.v1.service.search.SearchService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/search")
class SearchController(
    private val searchFacade: SearchFacade,
    private val searchService: SearchService,
) {
//    @GetMapping("")
//    fun search(
//        @CurrentUser userId: UserId,
//        @RequestParam("keyword") keyword: String,
//    ): SuccessResponseEntity<SearchResultResponse> {
//        val search = searchFacade.search(userId, keyword)
//        // 성공 응답 200 반환
//        return ResponseHelper.success(SearchResultResponse.ofList(search))
//    }
//
//    @PostMapping("")
//    fun addSearchKeyword(
//        @CurrentUser userId: UserId,
//        @RequestBody request: FriendSearchRequest,
//    ): SuccessResponseEntity<SuccessCreateResponse> {
//        searchService.createSearchKeyword(userId, request.keyword)
//        // 성공 응답 200 반환
//        return ResponseHelper.successCreateOnly()
//    }
//
//    @GetMapping("/recent")
//    fun getSearchHistory(
//        @CurrentUser userId: UserId,
//    ): SuccessResponseEntity<SearchHistoriesResponse> {
//        val searchKeywords = searchService.getSearchKeywords(userId)
//        // 성공 응답 200 반환
//        return ResponseHelper.success(SearchHistoriesResponse.ofList(searchKeywords))
//    }
}
