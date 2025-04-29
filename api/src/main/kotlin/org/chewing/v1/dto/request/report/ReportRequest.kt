package org.chewing.v1.dto.request.report

import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.report.Report
import org.chewing.v1.model.report.ReportTargetType

class ReportRequest {
    data class Feed(
        val feedId: String,
        val reason: String,
    ) {
        fun toReport(): Report {
            return Report.of(ReportTargetType.FEED, reason)
        }
        fun toFeedId() = FeedId.of(feedId)
    }
}
