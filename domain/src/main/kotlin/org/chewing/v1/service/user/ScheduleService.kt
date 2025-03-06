package org.chewing.v1.service.user

import org.chewing.v1.implementation.schedule.ScheduleAppender
import org.chewing.v1.implementation.schedule.ScheduleEnricher
import org.chewing.v1.implementation.schedule.ScheduleFilter
import org.chewing.v1.implementation.schedule.ScheduleReader
import org.chewing.v1.implementation.schedule.ScheduleRemover
import org.chewing.v1.implementation.schedule.ScheduleUpdater
import org.chewing.v1.implementation.schedule.ScheduleValidator
import org.chewing.v1.model.schedule.*
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Service

@Service
class ScheduleService(
    private val scheduleAppender: ScheduleAppender,
    private val scheduleRemover: ScheduleRemover,
    private val scheduleReader: ScheduleReader,
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
    ): ScheduleId {
        val scheduleId = scheduleAppender.appendInfo(scheduleTime, scheduleContent)
        scheduleAppender.appendOwner(scheduleId, userId)
        scheduleAppender.appendParticipants(scheduleId, friendIds)
        scheduleAppender.appendLog(scheduleId, userId, ScheduleAction.CREATED)
        return scheduleId
    }

    fun delete(userId: UserId, scheduleId: ScheduleId) {
        scheduleValidator.isOwner(userId, scheduleId)
        scheduleRemover.removeInfo(scheduleId)
        scheduleRemover.removeAllParticipants(scheduleId)
        scheduleAppender.appendLog(scheduleId, userId, ScheduleAction.DELETED)
    }

    fun cancel(userId: UserId, scheduleId: ScheduleId) {
        scheduleValidator.isParticipate(userId, scheduleId)
        scheduleRemover.removeParticipant(scheduleId, userId)
        scheduleAppender.appendLog(scheduleId, userId, ScheduleAction.CANCELED)
    }

    fun deleteAllParticipant(userId: UserId) {
        val scheduleIds = scheduleRemover.removeAllParticipated(userId)
        scheduleIds.forEach { scheduleAppender.appendLog(it, userId, ScheduleAction.CANCELED) }
    }

    fun fetches(userId: UserId, type: ScheduleType): Pair<List<Schedule>, Int> {
        val participatedSchedules = scheduleReader.readParticipated(userId, ScheduleParticipantStatus.ACTIVE)
        val scheduleInfos =
            scheduleReader.readInfos(participatedSchedules.map { it.scheduleId }, type, ScheduleStatus.ACTIVE)
        val participants =
            scheduleReader.readsParticipants(scheduleInfos.map { it.scheduleId }, ScheduleParticipantStatus.ACTIVE)
        return scheduleEnricher.enriches(userId, scheduleInfos, participants, participatedSchedules)
    }

    fun fetch(userId: UserId, scheduleId: ScheduleId): Schedule {
        val scheduleInfo = scheduleReader.readInfo(scheduleId, ScheduleStatus.ACTIVE)
        val participants = scheduleReader.readParticipants(scheduleId, ScheduleParticipantStatus.ACTIVE)
        scheduleUpdater.updateParticipantReadStatus(userId, scheduleId, ScheduleParticipantReadStatus.READ)
        return scheduleEnricher.enrich(userId, scheduleInfo, participants)
    }

    fun update(
        userId: UserId,
        scheduleId: ScheduleId,
        scheduleTime: ScheduleTime,
        scheduleContent: ScheduleContent,
        friendIds: List<UserId>,
        participated: Boolean,
    ) {
        scheduleValidator.isParticipate(userId, scheduleId)
        scheduleUpdater.updateInfo(scheduleId, scheduleTime, scheduleContent)
        val existingParticipants = scheduleReader.readAllParticipants(scheduleId)
        val targetParticipant = scheduleEnricher.enrichParticipant(userId, friendIds, participated)
        val (appendTargetIds, updateTargetIds, removeTargetIds) = scheduleFilter.filterUpdateTarget(
            existingParticipants,
            targetParticipant,
        )
        scheduleAppender.appendParticipants(scheduleId, appendTargetIds)
        scheduleUpdater.updateParticipants(scheduleId, updateTargetIds, ScheduleParticipantStatus.ACTIVE)
        scheduleRemover.removeParticipants(scheduleId, removeTargetIds)
        scheduleAppender.appendLog(scheduleId, userId, ScheduleAction.UPDATED)
    }

    fun fetchLogs(userId: UserId): List<ScheduleLog> {
        val participatedSchedules = scheduleReader.readParticipated(userId, ScheduleParticipantStatus.ACTIVE)
        return scheduleReader.readsLogs(participatedSchedules.map { it.scheduleId })
    }
}
