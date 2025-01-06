package org.chewing.v1.implementation.user.schedule

import org.chewing.v1.repository.user.ScheduleRepository
import org.springframework.stereotype.Component

@Component
class ScheduleRemover(
    private val scheduleRepository: ScheduleRepository,
) {
    fun remove(scheduleId: String) {
        scheduleRepository.remove(scheduleId)
    }
    fun removeUsers(userId: String) {
        scheduleRepository.removeUsers(userId)
    }
}
