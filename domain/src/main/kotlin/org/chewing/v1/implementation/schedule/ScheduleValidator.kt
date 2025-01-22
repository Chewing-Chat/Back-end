package org.chewing.v1.implementation.schedule

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleParticipantRole
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.schedule.ScheduleParticipantRepository
import org.springframework.stereotype.Component

@Component
class ScheduleValidator(
    private val scheduleParticipantRepository: ScheduleParticipantRepository,
) {
    fun isParticipate(userId: UserId, scheduleId: ScheduleId) {
        val scheduleParticipant = scheduleParticipantRepository.readParticipant(userId, scheduleId)

        if (scheduleParticipant == null) {
            throw NotFoundException(ErrorCode.SCHEDULE_NOT_PARTICIPANT)
        }
    }

    fun isOwner(userId: UserId, scheduleId: ScheduleId) {
        val scheduleParticipant = scheduleParticipantRepository.readParticipant(userId, scheduleId)

        if (scheduleParticipant == null) {
            throw NotFoundException(ErrorCode.SCHEDULE_NOT_PARTICIPANT)
        }

        if (scheduleParticipant.role != ScheduleParticipantRole.OWNER) {
            throw ConflictException(ErrorCode.SCHEDULE_NOT_OWNER)
        }
    }
}
