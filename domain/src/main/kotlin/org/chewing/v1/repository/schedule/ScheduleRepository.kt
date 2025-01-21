package org.chewing.v1.repository.schedule

import org.chewing.v1.model.schedule.ScheduleContent
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleInfo
import org.chewing.v1.model.schedule.ScheduleStatus
import org.chewing.v1.model.schedule.ScheduleTime
import org.chewing.v1.model.schedule.ScheduleType
import org.springframework.stereotype.Repository

@Repository
interface ScheduleRepository {
    fun append(scheduleTime: ScheduleTime, scheduleContent: ScheduleContent): ScheduleId
    fun remove(scheduleId: ScheduleId)
    fun reads(scheduleIds: List<ScheduleId>, type: ScheduleType, status: ScheduleStatus): List<ScheduleInfo>
    fun read(scheduleId: ScheduleId, status: ScheduleStatus): ScheduleInfo?
    fun update(scheduleId: ScheduleId, scheduleTime: ScheduleTime, scheduleContent: ScheduleContent)
}
