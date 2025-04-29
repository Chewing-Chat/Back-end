package org.chewing.v1.jparepository.report

import org.chewing.v1.jpaentity.report.ReportJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface ReportJpaRepository : JpaRepository<ReportJpaEntity, String>
