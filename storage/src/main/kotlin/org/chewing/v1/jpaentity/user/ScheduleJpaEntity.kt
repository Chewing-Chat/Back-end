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
        Index(name = "schedule_idx_dateTime", columnList = "dateTime"),
    ],
)
internal class ScheduleJpaEntity(
    @Id
    private val scheduleId: String = UUID.randomUUID().toString(),
    private val name: String,
    private val content: String,
    private val dateTime: LocalDateTime,
    private val userId: String,
    private val timeDecided: Boolean,
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
            dateTime = scheduleTime.dateTime,
            userId = userId,
            location = scheduleContent.location,
            timeDecided = scheduleTime.timeDecided,
        )
    }

    fun toSchedule(): Schedule = Schedule.of(
        scheduleId,
        name,
        content,
        dateTime,
        location,
        timeDecided,
    )
}
