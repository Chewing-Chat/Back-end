package org.chewing.v1.service.user

import org.chewing.v1.implementation.user.schedule.ScheduleAppender
import org.chewing.v1.implementation.user.schedule.ScheduleEnricher
import org.chewing.v1.implementation.user.schedule.ScheduleFilter
import org.chewing.v1.implementation.user.schedule.ScheduleGenerator
import org.chewing.v1.implementation.user.schedule.ScheduleReader
import org.chewing.v1.implementation.user.schedule.ScheduleRemover
import org.chewing.v1.implementation.user.schedule.ScheduleUpdater
import org.chewing.v1.implementation.user.schedule.ScheduleValidator
import org.chewing.v1.model.schedule.*
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Service

@Service
class ScheduleService(
    private val scheduleAppender: ScheduleAppender,
    private val scheduleRemover: ScheduleRemover,
    private val scheduleReader: ScheduleReader,
    private val scheduleGenerator: ScheduleGenerator,
    private val scheduleEnricher: ScheduleEnricher,
    private val scheduleValidator: ScheduleValidator,
    private val scheduleUpdater: ScheduleUpdater,
    private val scheduleFilter: ScheduleFilter,
) {
    fun create(
        userId: UserId,
        scheduleTime: ScheduleTime,
        scheduleContent: ScheduleContent,
        friendIds: List<UserId>,
    ): String {
        val scheduleId = scheduleAppender.appendInfo(scheduleTime, scheduleContent)
        scheduleAppender.appendParticipants(scheduleId, friendIds.plus(userId))
        return scheduleId
    }

    fun delete(userId: UserId, scheduleId: String) {
        scheduleValidator.isParticipate(userId, scheduleId)
        scheduleRemover.removeInfo(scheduleId)
        scheduleRemover.removeAllParticipants(scheduleId)
    }

    fun deleteParticipant(userId: UserId) {
        scheduleRemover.removeParticipated(userId)
    }

    fun fetches(userId: UserId, type: ScheduleType): List<Schedule> {
        val scheduleIds = scheduleReader.readParticipantScheduleIds(userId, ScheduleParticipantStatus.ACTIVE)
        val scheduleInfos = scheduleReader.readInfos(scheduleIds, type, ScheduleStatus.ACTIVE)
        val participants =
            scheduleReader.readsParticipants(scheduleInfos.map { it.id }, ScheduleParticipantStatus.ACTIVE)
        return scheduleEnricher.enrich(userId, scheduleInfos, participants)
    }

    fun update(
        userId: UserId,
        scheduleId: String,
        scheduleTime: ScheduleTime,
        scheduleContent: ScheduleContent,
        friendIds: List<UserId>,
    ) {
        scheduleValidator.isParticipate(userId, scheduleId)
        scheduleUpdater.updateInfo(scheduleId, scheduleTime, scheduleContent)
        val existingParticipantIds = scheduleReader.readParticipants(scheduleId).map { it.userId }
        val targetParticipantIds = friendIds.plus(userId)
        val needToAddFriendIds = scheduleFilter.filterAppendsTarget(existingParticipantIds, targetParticipantIds)
        val needToRemoveFriendIds = scheduleFilter.filterRemovesTarget(existingParticipantIds, targetParticipantIds)
        scheduleAppender.appendParticipants(scheduleId, needToAddFriendIds)
        scheduleRemover.removeParticipants(scheduleId, needToRemoveFriendIds)
    }

    fun createAiSchedule(userId: String, scheduleStringInfo: String): String {
        val (scheduleContent, scheduleTime) = scheduleGenerator.generateScheduleFromString(scheduleStringInfo)
        return scheduleAppender.appendInfo(scheduleTime, scheduleContent)
    }
}
