package org.chewing.v1.model.schedule

import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScheduleLog private constructor(
    val scheduleId: ScheduleId,
    val userId: UserId,
    val action: ScheduleAction,
    val createAt: String,
) {
    companion object {
        fun of(
            scheduleId: ScheduleId,
            userId: UserId,
            action: ScheduleAction,
            createAt: LocalDateTime,
        ): ScheduleLog {
            val formattedCreateTime =
                createAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

            return ScheduleLog(
                scheduleId,
                userId,
                action,
                formattedCreateTime,
            )
        }
    }
}
