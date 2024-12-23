package org.chewing.v1.jparepository.user

import org.chewing.v1.jpaentity.user.ScheduleJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

internal interface ScheduleJpaRepository : JpaRepository<ScheduleJpaEntity, String> {
    @Query("SELECT s FROM ScheduleJpaEntity s WHERE s.userId = :userId AND s.startAt BETWEEN :start AND :end AND (:isOwned = TRUE OR s.private = FALSE)")
    fun findSchedules(
        @Param("userId") userId: String,
        @Param("start") start: LocalDateTime,
        @Param("end") end: LocalDateTime,
        @Param("isOwned") isOwned: Boolean,
    ): List<ScheduleJpaEntity>

    fun deleteAllByUserId(userId: String)
}
