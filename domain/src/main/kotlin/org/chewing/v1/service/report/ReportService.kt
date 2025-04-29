package org.chewing.v1.service.report

import org.chewing.v1.implementation.report.ReportAppender
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.report.Report
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Service

@Service
class ReportService(
    private val reportAppender: ReportAppender,
) {
    fun reportFeed(userId: UserId, feedId: FeedId, report: Report) {
        reportAppender.appendReport(userId, feedId, report)
    }
}
