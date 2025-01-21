package org.chewing.v1.repository.schedule

import org.chewing.v1.model.schedule.ScheduleChangeStatus
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleLog
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Repository

@Repository
interface ScheduleLogRepository {
    fun appendLog(scheduleId: ScheduleId, userId: UserId, changeStatus: ScheduleChangeStatus)
    fun readLogs(userId: UserId): List<ScheduleLog>
}
