package org.chewing.v1.jpaentity.user

import jakarta.persistence.*
import org.chewing.v1.model.schedule.Schedule
import org.chewing.v1.model.schedule.ScheduleContent
import org.chewing.v1.model.schedule.ScheduleTime
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
    name = "schedule",
    indexes = [
        Index(name = "schedule_idx_start_at", columnList = "startAt"),
        Index(name = "schedule_idx_end_at", columnList = "endAt"),
    ],
)
internal class ScheduleJpaEntity(
    @Id
    private val scheduleId: String = UUID.randomUUID().toString(),
    private val name: String,
    private val content: String,
    private val startAt: LocalDateTime,
    private val endAt: LocalDateTime,
    private val notificationAt: LocalDateTime,
    private val userId: String,
    private val private: Boolean,
    private val location: String,
) {
    companion object {
        fun generate(
            scheduleContent: ScheduleContent,
            scheduleTime: ScheduleTime,
            userId: String,
        ): ScheduleJpaEntity = ScheduleJpaEntity(
            name = scheduleContent.title,
            content = scheduleContent.memo,
            startAt = scheduleTime.startAt,
            endAt = scheduleTime.endAt,
            notificationAt = scheduleTime.notificationAt,
            userId = userId,
            location = scheduleContent.location,
            private = scheduleContent.private,
        )
    }

    fun toSchedule(): Schedule = Schedule.of(
        scheduleId,
        name,
        content,
        startAt,
        endAt,
        notificationAt,
        location,
        private,
    )
}
