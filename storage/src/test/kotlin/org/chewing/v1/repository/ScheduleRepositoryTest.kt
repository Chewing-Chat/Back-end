package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jparepository.user.ScheduleJpaRepository
import org.chewing.v1.model.schedule.ScheduleType
import org.chewing.v1.repository.jpa.user.ScheduleRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
import org.chewing.v1.repository.support.ScheduleProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class ScheduleRepositoryTest : JpaContextTest() {
    @Autowired
    private lateinit var scheduleJpaRepository: ScheduleJpaRepository

    @Autowired
    private lateinit var jpaDataGenerator: JpaDataGenerator

    @Autowired
    private lateinit var scheduleRepositoryImpl: ScheduleRepositoryImpl

    @Test
    fun `스케줄 저장에 성공`() {
        val userId = generateUserId()
        val content = ScheduleProvider.buildContent()
        val time = ScheduleProvider.buildTime()
        val result = scheduleRepositoryImpl.append(time, content, userId)
        assert(result.isNotEmpty())
    }

    @Test
    fun `스케줄 삭제에 성공`() {
        val userId = generateUserId()
        val content = ScheduleProvider.buildContent()
        val time = ScheduleProvider.buildTime()
        val schedule = jpaDataGenerator.scheduleEntityData(content, time, userId)
        scheduleRepositoryImpl.remove(schedule.id)
        assert(scheduleJpaRepository.findById(schedule.id).isEmpty)
    }

    @Test
    fun `스케줄 전체 삭제에 성공`() {
        val userId = generateUserId()
        val content = ScheduleProvider.buildContent()
        val time = ScheduleProvider.buildTime()
        val schedule = jpaDataGenerator.scheduleEntityData(content, time, userId)
        scheduleRepositoryImpl.removeUsers(userId)
        assert(scheduleJpaRepository.findById(schedule.id).isEmpty)
    }

    @Test
    fun `시간 기준으로 본인 스케줄 조회에 성공`() {
        val userId = generateUserId()
        val content = ScheduleProvider.buildContent()
        val time = ScheduleProvider.build1000YearTime()
        val schedule = jpaDataGenerator.scheduleEntityData(content, time, userId)
        val schedules = scheduleRepositoryImpl.reads(userId, ScheduleType.of(time.dateTime.year, time.dateTime.monthValue))
        assert(schedules.size == 1)
        assert(schedule.time.dateTime == schedules[0].time.dateTime)
    }

    @Test
    fun `시간 기준으로 본인 스케줄 조회에 실패`() {
        val userId = generateUserId()
        val content = ScheduleProvider.buildContent()
        val time = ScheduleProvider.build1000YearTime()
        jpaDataGenerator.scheduleEntityData(content, time, userId)
        jpaDataGenerator.scheduleEntityData(content, time, userId)
        val schedules = scheduleRepositoryImpl.reads(userId, ScheduleType.of(time.dateTime.year.minus(1), time.dateTime.monthValue))
        assert(schedules.isEmpty())
    }
    fun generateUserId(): String = UUID.randomUUID().toString()
}
