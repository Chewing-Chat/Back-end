package org.chewing.v1.implementation.user.schedule

import org.chewing.v1.model.schedule.ScheduleContent
import org.chewing.v1.model.schedule.ScheduleTime
import org.chewing.v1.repository.user.ScheduleRepository
import org.springframework.stereotype.Component

@Component
class ScheduleAppender(
    val scheduleRepository: ScheduleRepository,
) {
    fun append(scheduleTime: ScheduleTime, scheduleContent: ScheduleContent, userId: String): String {
        return scheduleRepository.append(scheduleTime, scheduleContent, userId)
    }
}
