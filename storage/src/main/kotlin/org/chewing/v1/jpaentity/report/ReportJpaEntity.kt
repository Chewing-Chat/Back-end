package org.chewing.v1.jpaentity.report

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.chewing.v1.model.report.Report
import org.chewing.v1.model.report.ReportTargetType
import org.chewing.v1.model.user.UserId
import org.hibernate.annotations.DynamicInsert
import java.util.UUID

@DynamicInsert
@Entity
@Table(name = "report", schema = "chewing")
class ReportJpaEntity(
    @Id
    private val reportId: String = UUID.randomUUID().toString(),
    @Enumerated(EnumType.STRING)
    private val targetType: ReportTargetType,
    private val targetId: String,
    private val userId: String,
    private val reason: String,
) {
    companion object {
        fun generate(userId: UserId, targetId: String, report: Report): ReportJpaEntity {
            return ReportJpaEntity(
                targetType = report.targetType,
                targetId = targetId,
                userId = userId.id,
                reason = report.reason,
            )
        }
    }
}
