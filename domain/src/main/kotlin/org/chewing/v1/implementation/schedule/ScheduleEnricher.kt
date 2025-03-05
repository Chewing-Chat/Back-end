package org.chewing.v1.implementation.schedule

import org.chewing.v1.model.schedule.Schedule
import org.chewing.v1.model.schedule.ScheduleInfo
import org.chewing.v1.model.schedule.ScheduleParticipant
import org.chewing.v1.model.schedule.ScheduleParticipantRole
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Component

@Component
class ScheduleEnricher {
    fun enriches(
        userId: UserId,
        scheduleInfos: List<ScheduleInfo>,
        scheduleParticipants: List<ScheduleParticipant>,
    ): List<Schedule> {
        return scheduleInfos.map { scheduleInfo ->
            val participants = scheduleParticipants
                .filter { it.scheduleId == scheduleInfo.scheduleId }
                .filter { it.userId != userId }
            val isOwned = participants.any { it.userId == userId && it.role == ScheduleParticipantRole.OWNER }
            val isParticipant = participants.any { it.userId == userId && it.status == ScheduleParticipantStatus.ACTIVE }
            Schedule.of(scheduleInfo, participants, isOwned, isParticipant)
        }
    }
    fun enrichParticipant(
        userId: UserId,
        friendIds: List<UserId>,
        participated: Boolean,
    ): List<UserId> {
        if (participated) {
            return friendIds.plus(userId)
        } else {
            return friendIds
        }
    }
    fun enrich(
        userId: UserId,
        scheduleInfo: ScheduleInfo,
        scheduleParticipants: List<ScheduleParticipant>,
    ): Schedule {
        val participants = scheduleParticipants
            .filter { it.scheduleId == scheduleInfo.scheduleId }
            .filter { it.userId != userId }
        val isOwned = participants.any { it.userId == userId && it.role == ScheduleParticipantRole.OWNER }
        val isParticipant = participants.any { it.userId == userId && it.status == ScheduleParticipantStatus.ACTIVE }
        return Schedule.of(scheduleInfo, participants, isOwned, isParticipant)
    }
}
