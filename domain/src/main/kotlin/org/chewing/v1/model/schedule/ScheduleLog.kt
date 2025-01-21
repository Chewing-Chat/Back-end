package org.chewing.v1.model.schedule

import org.chewing.v1.model.user.UserId

class ScheduleLog private constructor(
    val scheduleId: ScheduleId,
    val userId: UserId,
    val changeStatus: ScheduleChangeStatus,
) {
    companion object {
        fun of(
            scheduleId: ScheduleId,
            userId: UserId,
            changeStatus: ScheduleChangeStatus,
        ): ScheduleLog {
            return ScheduleLog(
                scheduleId,
                userId,
                changeStatus,
            )
        }
    }
}
