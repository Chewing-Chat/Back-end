package org.chewing.v1.implementation.schedule

import org.chewing.v1.model.schedule.Schedule
import org.chewing.v1.model.schedule.ScheduleInfo
import org.chewing.v1.model.schedule.ScheduleParticipant
import org.chewing.v1.model.schedule.ScheduleParticipantReadStatus
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
        participatedSchedules: List<ScheduleParticipant>,
    ): Pair<List<Schedule>, Int> {
        val unReadCount = participatedSchedules.count { it.readStatus == ScheduleParticipantReadStatus.UNREAD }
        val schedules = scheduleInfos.map { scheduleInfo ->
            val participants = scheduleParticipants
                .filter { it.scheduleId == scheduleInfo.scheduleId }
            val isOwned = participants.any { it.userId == userId && it.role == ScheduleParticipantRole.OWNER }
            val isParticipant = participants.any { it.userId == userId && it.status == ScheduleParticipantStatus.ACTIVE }
            val friendParticipants = participants.filter { it.userId != userId }
            Schedule.of(scheduleInfo, friendParticipants, isOwned, isParticipant)
        }
        return Pair(schedules, unReadCount)
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
        participants: List<ScheduleParticipant>,
    ): Schedule {
        val isOwned = participants.any { it.userId == userId && it.role == ScheduleParticipantRole.OWNER }
        val isParticipant = participants.any { it.userId == userId && it.status == ScheduleParticipantStatus.ACTIVE }
        val friendParticipants = participants.filter { it.userId != userId }
        return Schedule.of(scheduleInfo, friendParticipants, isOwned, isParticipant)
    }
}
