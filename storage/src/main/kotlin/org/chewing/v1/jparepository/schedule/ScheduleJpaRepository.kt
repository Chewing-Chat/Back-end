package org.chewing.v1.jparepository.schedule

import org.chewing.v1.jpaentity.schedule.ScheduleJpaEntity
import org.chewing.v1.model.schedule.ScheduleStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

internal interface ScheduleJpaRepository : JpaRepository<ScheduleJpaEntity, String> {
    @Query(
        """
    SELECT s
    FROM ScheduleJpaEntity s
    WHERE s.scheduleId IN :scheduleIds
      AND s.dateTime BETWEEN :start AND :end
      AND s.status = :status
    ORDER BY s.dateTime DESC
    """,
    )
    fun findSchedules(
        @Param("scheduleIds") scheduleIds: List<String>,
        @Param("start") start: LocalDateTime,
        @Param("end") end: LocalDateTime,
        @Param("status") status: ScheduleStatus,
    ): List<ScheduleJpaEntity>

    fun findByScheduleIdAndStatus(scheduleId: String, status: ScheduleStatus): ScheduleJpaEntity?
}
