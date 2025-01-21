package org.chewing.v1.dto.response.schedule

import org.chewing.v1.model.schedule.ScheduleChangeStatus
import org.chewing.v1.model.schedule.ScheduleLog

data class ScheduleLogResponse(
    val scheduleId: String,
    val userId: String,
    val status: ScheduleChangeStatus,
) {
    companion object {
        fun of(
            log: ScheduleLog,
        ): ScheduleLogResponse {
            return ScheduleLogResponse(
                log.scheduleId.id,
                log.userId.id,
                log.changeStatus,
            )
        }
    }
}
