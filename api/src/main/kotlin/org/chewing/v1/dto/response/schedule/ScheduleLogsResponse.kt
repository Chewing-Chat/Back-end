package org.chewing.v1.dto.response.schedule

import org.chewing.v1.model.schedule.ScheduleLog

data class ScheduleLogsResponse(
    val logs: List<ScheduleLogResponse>,
) {
    companion object {
        fun of(
            logs: List<ScheduleLog>,
        ): ScheduleLogsResponse {
            return ScheduleLogsResponse(
                logs.map { ScheduleLogResponse.of(it) },
            )
        }
    }
}
