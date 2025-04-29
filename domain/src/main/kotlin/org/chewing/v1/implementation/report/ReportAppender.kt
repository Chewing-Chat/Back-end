package org.chewing.v1.implementation.report

import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.report.Report
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.report.ReportRepository
import org.springframework.stereotype.Component

@Component
class ReportAppender(
    private val reportRepository: ReportRepository,
) {
    fun appendReport(userId: UserId, feedId: FeedId, report: Report) {
        reportRepository.reportFeed(userId, feedId, report)
    }
}
