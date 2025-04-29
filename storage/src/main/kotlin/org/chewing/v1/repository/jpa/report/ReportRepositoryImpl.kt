package org.chewing.v1.repository.jpa.report

import org.chewing.v1.jpaentity.report.ReportJpaEntity
import org.chewing.v1.jparepository.report.ReportJpaRepository
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.report.Report
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.report.ReportRepository
import org.springframework.stereotype.Repository

@Repository
internal class ReportRepositoryImpl(
    private val reportJpaRepository: ReportJpaRepository,
) : ReportRepository {
    override fun reportFeed(
        userId: UserId,
        feedId: FeedId,
        report: Report,
    ) {
        val entity = ReportJpaEntity.generate(
            userId,
            feedId.id,
            report,
        )
        reportJpaRepository.save(entity)
    }
}
