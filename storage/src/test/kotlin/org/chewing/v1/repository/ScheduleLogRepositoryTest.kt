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
        assert(scheduleLogJpaRepository.findAllByUserId(userId.id, SortType.LATEST.toSort()).isNotEmpty())
    }

    @Test
    fun `스케줄 로그 목록 읽기에 성공`() {
        val userId = generateUserId()
        val scheduleId = generateScheduleId()
        val scheduleLog = jpaDataGenerator.scheduleLogEntityData(scheduleId, userId, ScheduleAction.CREATED)
        val result = scheduleLogRepositoryImpl.readLogs(userId)
        assert(result.size == 1)
        assert(result[0].userId == scheduleLog.userId)
        assert(result[0].scheduleId == scheduleLog.scheduleId)
        assert(result[0].action == scheduleLog.action)
    }

    private fun generateUserId() = UserId.of(UUID.randomUUID().toString())

    private fun generateScheduleId() = ScheduleId.of(UUID.randomUUID().toString())
}
