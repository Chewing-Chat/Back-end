package org.chewing.v1.implementation.user.schedule

import org.chewing.v1.model.schedule.ScheduleContent
import org.chewing.v1.model.schedule.ScheduleTime
import org.chewing.v1.repository.user.ScheduleRepository
import org.springframework.stereotype.Component

@Component
class ScheduleUpdater(
    private val scheduleRepository: ScheduleRepository,
) {
    fun updateInfo(scheduleId: String, scheduleTime: ScheduleTime, scheduleContent: ScheduleContent) {
        scheduleRepository.update(scheduleId, scheduleTime, scheduleContent)
    }
}
