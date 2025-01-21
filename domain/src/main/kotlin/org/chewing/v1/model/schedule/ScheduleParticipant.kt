package org.chewing.v1.model.schedule

import org.chewing.v1.model.user.UserId

class ScheduleParticipant private constructor(
    val userId: UserId,
    val scheduleId: ScheduleId,
    val status: ScheduleParticipantStatus,
    val role: ScheduleParticipantRole,
) {
    companion object {
        fun of(
            userId: UserId,
            scheduleId: ScheduleId,
            status: ScheduleParticipantStatus,
            role: ScheduleParticipantRole,
        ): ScheduleParticipant {
            return ScheduleParticipant(userId, scheduleId, status, role)
        }
    }
}
