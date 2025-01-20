package org.chewing.v1.implementation.user.schedule

import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.user.ScheduleParticipantRepository
import org.chewing.v1.repository.user.ScheduleRepository
import org.springframework.stereotype.Component

@Component
class ScheduleRemover(
    private val scheduleRepository: ScheduleRepository,
    private val scheduleParticipantRepository: ScheduleParticipantRepository,
) {
    fun removeInfo(scheduleId: String) {
        scheduleRepository.remove(scheduleId)
    }
    fun removeParticipated(userId: UserId) {
        scheduleParticipantRepository.removeParticipated(userId)
    }
    fun removeAllParticipants(scheduleId: String) {
        scheduleParticipantRepository.removeAllParticipants(scheduleId)
    }

    fun removeParticipants(scheduleId: String, userIds: List<UserId>) {
        scheduleParticipantRepository.removeParticipants(scheduleId, userIds)
    }
}
