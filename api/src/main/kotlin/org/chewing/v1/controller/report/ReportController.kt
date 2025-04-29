package org.chewing.v1.controller.report

import org.chewing.v1.dto.request.report.ReportRequest
import org.chewing.v1.model.user.UserId
import org.chewing.v1.response.SuccessOnlyResponse
import org.chewing.v1.service.report.ReportService
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.security.CurrentUser
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/report")
class ReportController(
    private val reportService: ReportService,
) {
    @PostMapping("/feed")
    fun reportFeed(
        @RequestBody request: ReportRequest.Feed,
        @CurrentUser userId: UserId,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        reportService.reportFeed(userId, request.toFeedId(), request.toReport())
        return ResponseHelper.successOnly()
    }
}
