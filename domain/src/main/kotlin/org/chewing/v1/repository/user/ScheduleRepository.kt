package org.chewing.v1.repository.user

import org.chewing.v1.model.schedule.*
import org.springframework.stereotype.Repository

@Repository
interface ScheduleRepository {
    fun append(scheduleTime: ScheduleTime, scheduleContent: ScheduleContent): ScheduleId
    fun remove(scheduleId: ScheduleId)
    fun reads(scheduleIds: List<ScheduleId>, type: ScheduleType, status: ScheduleStatus): List<ScheduleInfo>
    fun update(scheduleId: ScheduleId, scheduleTime: ScheduleTime, scheduleContent: ScheduleContent)
}
