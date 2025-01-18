package org.chewing.v1.repository.user

import org.chewing.v1.model.schedule.*
import org.springframework.stereotype.Repository

@Repository
interface ScheduleRepository {
    fun append(scheduleTime: ScheduleTime, scheduleContent: ScheduleContent): String
    fun remove(scheduleId: String)
    fun reads(scheduleIds: List<String>, type: ScheduleType, status: ScheduleStatus): List<ScheduleInfo>
    fun update(scheduleId: String, scheduleTime: ScheduleTime, scheduleContent: ScheduleContent)
}
