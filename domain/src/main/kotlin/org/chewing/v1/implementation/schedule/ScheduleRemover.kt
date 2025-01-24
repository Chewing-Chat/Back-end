package org.chewing.v1.implementation.schedule

import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.schedule.ScheduleParticipantRepository
import org.chewing.v1.repository.schedule.ScheduleRepository
import org.springframework.stereotype.Component

@Component
class ScheduleRemover(
    private val scheduleRepository: ScheduleRepository,
    private val scheduleParticipantRepository: ScheduleParticipantRepository,
) {
    fun removeInfo(scheduleId: ScheduleId) {
        scheduleRepository.remove(scheduleId)
    }
    fun removeAllParticipated(userId: UserId): List<ScheduleId> {
        return scheduleParticipantRepository.removeParticipated(userId)
    }
    fun removeAllParticipants(scheduleId: ScheduleId) {
        scheduleParticipantRepository.removeAllParticipants(scheduleId)
    }

    fun removeParticipants(scheduleId: ScheduleId, userIds: List<UserId>) {
        scheduleParticipantRepository.removeParticipants(scheduleId, userIds)
    }

    fun removeParticipant(scheduleId: ScheduleId, userId: UserId) {
        scheduleParticipantRepository.removeParticipant(scheduleId, userId)
    }
}
