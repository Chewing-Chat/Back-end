package org.chewing.v1.implementation.user.schedule

import org.chewing.v1.model.schedule.Schedule
import org.chewing.v1.model.schedule.ScheduleType
import org.chewing.v1.repository.user.ScheduleRepository
import org.springframework.stereotype.Component

@Component
class ScheduleReader(
    private val scheduleRepository: ScheduleRepository,
) {
    fun reads(userId: String, type: ScheduleType, isOwned: Boolean): List<Schedule> {
        return scheduleRepository.reads(userId, type, isOwned)
    }
}
