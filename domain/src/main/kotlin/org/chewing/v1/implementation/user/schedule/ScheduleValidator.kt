package org.chewing.v1.implementation.user.schedule

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.user.ScheduleParticipantRepository
import org.springframework.stereotype.Component

@Component
class ScheduleValidator(
    private val scheduleParticipantRepository: ScheduleParticipantRepository,
) {
    fun isParticipate(userId: UserId, scheduleId: String) {
        val scheduleParticipant = scheduleParticipantRepository.readParticipant(userId, scheduleId)

        if (scheduleParticipant == null) {
            throw NotFoundException(ErrorCode.SCHEDULE_NOT_PARTICIPANT)
        }

        if (scheduleParticipant.status == ScheduleParticipantStatus.DELETED) {
            throw NotFoundException(ErrorCode.SCHEDULE_NOT_PARTICIPANT)
        }
    }
}
