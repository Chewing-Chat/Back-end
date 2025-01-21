package org.chewing.v1.model.schedule

class Schedule private constructor(
    val info: ScheduleInfo,
    val participants: List<ScheduleParticipant>,
    val isOwned: Boolean,
    val isParticipant: Boolean,
) {
    companion object {
        fun of(
            scheduleInfo: ScheduleInfo,
            participants: List<ScheduleParticipant>,
            isOwned: Boolean,
            isParticipant: Boolean,
        ): Schedule {
            return Schedule(scheduleInfo, participants, isOwned, isParticipant)
        }
    }
}
