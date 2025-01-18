package org.chewing.v1.model.schedule

class Schedule private constructor(
    val info: ScheduleInfo,
    val participants: List<ScheduleParticipant>,
) {
    companion object {
        fun of(scheduleInfo: ScheduleInfo, participants: List<ScheduleParticipant>): Schedule {
            return Schedule(scheduleInfo, participants)
        }
    }
}
