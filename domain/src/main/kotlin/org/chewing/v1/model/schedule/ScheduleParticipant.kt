package org.chewing.v1.model.schedule

class ScheduleParticipant private constructor(
    val userId: String,
    val scheduleId: String,
    val status: ScheduleParticipantStatus,
) {
    companion object {
        fun of(
            userId: String,
            scheduleId: String,
            status: ScheduleParticipantStatus,
        ): ScheduleParticipant {
            return ScheduleParticipant(userId, scheduleId, status)
        }
    }
}
