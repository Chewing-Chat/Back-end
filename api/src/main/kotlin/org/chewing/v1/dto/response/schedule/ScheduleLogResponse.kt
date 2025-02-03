package org.chewing.v1.dto.response.schedule

import org.chewing.v1.model.schedule.ScheduleLog

data class ScheduleLogResponse(
    val scheduleId: String,
    val userId: String,
    val action: String,
    val createAt: String,
) {
    companion object {
        fun of(
            log: ScheduleLog,
        ): ScheduleLogResponse {
            return ScheduleLogResponse(
                log.scheduleId.id,
                log.userId.id,
                log.action.name.lowercase(),
                log.createAt,
            )
        }
    }
}
