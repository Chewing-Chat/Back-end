package org.chewing.v1.implementation.user.schedule

import org.chewing.v1.model.schedule.ScheduleContent
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleTime
import org.chewing.v1.repository.user.ScheduleRepository
import org.springframework.stereotype.Component

@Component
class ScheduleUpdater(
    private val scheduleRepository: ScheduleRepository,
) {
    fun updateInfo(scheduleId: ScheduleId, scheduleTime: ScheduleTime, scheduleContent: ScheduleContent) {
        scheduleRepository.update(scheduleId, scheduleTime, scheduleContent)
    }
}
