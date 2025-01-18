package org.chewing.v1.dto.response.schedule

import org.chewing.v1.model.schedule.Schedule
import org.chewing.v1.model.schedule.ScheduleParticipant
import java.time.format.DateTimeFormatter

data class ScheduleListResponse(
    val schedules: List<ScheduleResponse>,
) {
    companion object {
        fun of(schedules: List<Schedule>): ScheduleListResponse {
            return ScheduleListResponse(
                schedules.map {
                    ScheduleResponse.of(it)
                },
            )
        }
    }

    data class ScheduleResponse(
        val scheduleId: String,
        val title: String,
        val dateTime: String,
        val memo: String,
        val location: String,
        val timeDecided: Boolean,
        val participants: List<ParticipantResponse>,
    ) {
        companion object {
            fun of(schedule: Schedule): ScheduleResponse {
                val formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")
                return ScheduleResponse(
                    schedule.info.id,
                    schedule.info.content.title,
                    schedule.info.time.dateTime.format(formatter),
                    schedule.info.content.memo,
                    schedule.info.content.location,
                    schedule.info.time.timeDecided,
                    schedule.participants.map {
                        ParticipantResponse.of(it)
                    },
                )
            }
        }
    }

    data class ParticipantResponse(
        val friendId: String,
    ) {
        companion object {
            fun of(friendId: ScheduleParticipant): ParticipantResponse {
                return ParticipantResponse(
                    friendId.userId,
                )
            }
        }
    }
}
