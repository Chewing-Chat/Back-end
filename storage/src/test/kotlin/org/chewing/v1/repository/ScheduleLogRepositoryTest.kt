package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jparepository.schedule.ScheduleLogJpaRepository
import org.chewing.v1.model.schedule.ScheduleAction
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.jpa.schedule.ScheduleLogRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
import org.chewing.v1.util.SortType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class ScheduleLogRepositoryTest : JpaContextTest() {
    @Autowired
    private lateinit var scheduleLogRepositoryImpl: ScheduleLogRepositoryImpl

    @Autowired
    private lateinit var jpaDataGenerator: JpaDataGenerator

    @Autowired
    private lateinit var scheduleLogJpaRepository: ScheduleLogJpaRepository

    @Test
    fun `스케줄 로그 저장에 성공`() {
        val userId = generateUserId()
        val scheduleId = generateScheduleId()
        scheduleLogRepositoryImpl.appendLog(scheduleId, userId, ScheduleAction.CREATED)
        assert(scheduleLogJpaRepository.findAllByScheduleIdIn(listOf(scheduleId.id), SortType.LATEST.toSort()).isNotEmpty())
    }

    @Test
    fun `스케줄 로그 목록 읽기에 성공`() {
        val userId = generateUserId()
        val scheduleId = generateScheduleId()
        jpaDataGenerator.scheduleLogEntityData(scheduleId, userId, ScheduleAction.CREATED)
        jpaDataGenerator.scheduleLogEntityData(scheduleId, userId, ScheduleAction.UPDATED)
        val result = scheduleLogRepositoryImpl.readsLogs(listOf(scheduleId))
        assert(result.size == 2)
    }

    private fun generateUserId() = UserId.of(UUID.randomUUID().toString())

    private fun generateScheduleId() = ScheduleId.of(UUID.randomUUID().toString())
}
