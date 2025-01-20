package org.chewing.v1.implementation.user.schedule

import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Component

@Component
class ScheduleFilter {
    fun filterRemovesTarget(participantIds: List<UserId>, targetIds: List<UserId>): List<UserId> {
        return participantIds.filter { it !in targetIds }
    }
    fun filterAppendsTarget(participantIds: List<UserId>, targetIds: List<UserId>): List<UserId> {
        return targetIds.filter { it !in participantIds }
    }
}
