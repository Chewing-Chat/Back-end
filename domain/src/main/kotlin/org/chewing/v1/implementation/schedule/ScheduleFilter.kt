package org.chewing.v1.implementation.schedule

import org.chewing.v1.model.schedule.ScheduleParticipant
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Component

@Component
class ScheduleFilter {
    fun filterUpdateTarget(
        participants: List<ScheduleParticipant>,
        targetIds: List<UserId>,
    ): Triple<List<UserId>, List<UserId>, List<UserId>> {
        val deletedParticipantIds = participants.filter { it.status == ScheduleParticipantStatus.DELETED }.mapTo(mutableSetOf()) { it.userId }
        val activatedParticipantIds = participants.filter { it.status == ScheduleParticipantStatus.ACTIVE }.mapTo(mutableSetOf()) { it.userId }
        val existingParticipants = participants.mapTo(mutableSetOf()) { it.userId }

        val appendTargetIds = targetIds.filter { it !in existingParticipants }
        val updateTargetIds = targetIds.filter { it in deletedParticipantIds }
        val removeTargetIds = activatedParticipantIds.filter { it !in targetIds }

        return Triple(appendTargetIds, updateTargetIds, removeTargetIds)
    }
}
