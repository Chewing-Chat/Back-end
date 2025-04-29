package org.chewing.v1.repository.report

import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.report.Report
import org.chewing.v1.model.user.UserId

interface ReportRepository {
    fun reportFeed(
        userId: UserId,
        feedId: FeedId,
        report: Report,
    )
}
