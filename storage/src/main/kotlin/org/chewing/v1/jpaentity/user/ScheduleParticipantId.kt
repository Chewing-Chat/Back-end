package org.chewing.v1.jpaentity.user

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.chewing.v1.model.user.UserId
import java.io.Serializable

@Embeddable
data class ScheduleParticipantId(
    @Column(name = "user_id")
    val userId: String,

    @Column(name = "schedule_id")
    val scheduleId: String,
) : Serializable {
    companion object {
        fun of(userId: UserId, scheduleId: String): ScheduleParticipantId {
            return ScheduleParticipantId(userId.id, scheduleId)
        }
    }
}
