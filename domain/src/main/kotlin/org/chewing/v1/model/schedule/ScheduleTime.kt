package org.chewing.v1.model.schedule

import java.time.LocalDateTime

class ScheduleTime private constructor(
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val notificationAt: LocalDateTime,
) {
    companion object {
        fun of(
            startAt: LocalDateTime,
            endAt: LocalDateTime,
            notificationAt: LocalDateTime,
        ): ScheduleTime {
            return ScheduleTime(
                startAt,
                endAt,
                notificationAt,
            )
        }
    }
}
