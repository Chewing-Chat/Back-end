package org.chewing.v1.implementation.schedule

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.util.OptimisticLockHandler
import org.chewing.v1.model.schedule.ScheduleContent
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleParticipantReadStatus
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.schedule.ScheduleTime
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.schedule.ScheduleParticipantRepository
import org.chewing.v1.repository.schedule.ScheduleRepository
import org.springframework.stereotype.Component

@Component
class ScheduleUpdater(
    private val optimisticLockHandler: OptimisticLockHandler,
    private val scheduleRepository: ScheduleRepository,
    private val scheduleParticipantRepository: ScheduleParticipantRepository,
) {
    fun updateInfo(scheduleId: ScheduleId, scheduleTime: ScheduleTime, scheduleContent: ScheduleContent) {
        optimisticLockHandler.retryOnOptimisticLock {
            scheduleRepository.update(scheduleId, scheduleTime, scheduleContent)
        } ?: throw ConflictException(ErrorCode.SCHEDULE_UPDATE_FAILED)
    }
    fun updateParticipants(scheduleId: ScheduleId, userIds: List<UserId>, status: ScheduleParticipantStatus) {
        scheduleParticipantRepository.updateParticipantsStatus(scheduleId, userIds, status)
    }
    fun updateParticipantReadStatus(userId: UserId, scheduleId: ScheduleId, readStatus: ScheduleParticipantReadStatus) {
        scheduleParticipantRepository.updateParticipantReadStatus(userId, scheduleId, readStatus)
    }
}
