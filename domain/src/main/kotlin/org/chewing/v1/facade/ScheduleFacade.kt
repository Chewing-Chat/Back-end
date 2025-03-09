package org.chewing.v1.facade

import org.chewing.v1.model.schedule.ScheduleAction
import org.chewing.v1.model.schedule.ScheduleContent
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleTime
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.notification.NotificationService
import org.chewing.v1.service.user.ScheduleService
import org.springframework.stereotype.Service

@Service
class ScheduleFacade(
    private val scheduleService: ScheduleService,
    private val notificationService: NotificationService,
) {
    fun createSchedule(
        userId: UserId,
        scheduleTime: ScheduleTime,
        scheduleContent: ScheduleContent,
        friendIds: List<UserId>,
    ): ScheduleId {
        val scheduleId = scheduleService.create(userId, scheduleTime, scheduleContent, friendIds)
        notificationService.handleSchedulesNotification(friendIds, userId, scheduleId, ScheduleAction.CREATED)
        return scheduleId
    }

    fun cancelSchedule(
        userId: UserId,
        scheduleId: ScheduleId,
    ) {
        val participantFriendIds = scheduleService.fetchParticipant(scheduleId).map { it.userId }.filter { it != userId }
        scheduleService.cancel(userId, scheduleId)
        notificationService.handleSchedulesNotification(participantFriendIds, userId, scheduleId, ScheduleAction.CANCELED)
    }

    fun deleteSchedule(
        userId: UserId,
        scheduleId: ScheduleId,
    ) {
        val participantFriendIds = scheduleService.fetchParticipant(scheduleId).map { it.userId }.filter { it != userId }
        scheduleService.delete(userId, scheduleId)
        notificationService.handleSchedulesNotification(participantFriendIds, userId, scheduleId, ScheduleAction.DELETED)
    }

    fun updateSchedule(
        userId: UserId,
        scheduleId: ScheduleId,
        scheduleTime: ScheduleTime,
        scheduleContent: ScheduleContent,
        friendIds: List<UserId>,
        participated: Boolean,
    ) {
        val oldParticipantFriendIds = scheduleService.fetchParticipant(scheduleId).map { it.userId }.filter { it != userId }
        scheduleService.update(userId, scheduleId, scheduleTime, scheduleContent, friendIds, participated)
        val newParticipantFriendIds = scheduleService.fetchParticipant(scheduleId).map { it.userId }.filter { it != userId }
        val mergedParticipants = (oldParticipantFriendIds + newParticipantFriendIds).distinct()
        notificationService.handleSchedulesNotification(mergedParticipants, userId, scheduleId, ScheduleAction.UPDATED)
    }
}
