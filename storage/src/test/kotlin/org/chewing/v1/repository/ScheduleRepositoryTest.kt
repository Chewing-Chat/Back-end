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
        val content = ScheduleProvider.buildContent(true)
        val time = ScheduleProvider.buildTime()
        val result = scheduleRepositoryImpl.append(time, content, userId)
        assert(result.isNotEmpty())
    }

    @Test
    fun `스케줄 삭제에 성공`() {
        val userId = generateUserId()
        val content = ScheduleProvider.buildContent(true)
        val time = ScheduleProvider.buildTime()
        val schedule = jpaDataGenerator.scheduleEntityData(content, time, userId)
        scheduleRepositoryImpl.remove(schedule.id)
        assert(scheduleJpaRepository.findById(schedule.id).isEmpty)
    }

    @Test
    fun `스케줄 전체 삭제에 성공`() {
        val userId = generateUserId()
        val content = ScheduleProvider.buildContent(true)
        val time = ScheduleProvider.buildTime()
        val schedule = jpaDataGenerator.scheduleEntityData(content, time, userId)
        scheduleRepositoryImpl.removeUsers(userId)
        assert(scheduleJpaRepository.findById(schedule.id).isEmpty)
    }

    @Test
    fun `시작시간 기준으로 본인 스케줄 조회에 성공`() {
        val userId = generateUserId()
        val content = ScheduleProvider.buildContent(true)
        val firstTime = ScheduleProvider.build1000YearFirstTime()
        val secondTime = ScheduleProvider.build1000YearSecondTime()
        val schedule2 = jpaDataGenerator.scheduleEntityData(content, secondTime, userId)
        val schedule = jpaDataGenerator.scheduleEntityData(content, firstTime, userId)
        val schedules = scheduleRepositoryImpl.reads(userId, ScheduleType.of(1000, 1), true)
        assert(schedules.size == 2)
        assert(schedule.time.startAt == schedules[0].time.startAt)
        assert(schedule2.time.startAt == schedules[1].time.startAt)
    }

    @Test
    fun `시작시간 기준으로 본인 스케줄 조회에 실패`() {
        val userId = generateUserId()
        val content = ScheduleProvider.buildContent(true)
        val firstTime = ScheduleProvider.build1000YearFirstTime()
        val secondTime = ScheduleProvider.build1000YearSecondTime()
        jpaDataGenerator.scheduleEntityData(content, secondTime, userId)
        jpaDataGenerator.scheduleEntityData(content, firstTime, userId)
        val schedules = scheduleRepositoryImpl.reads(userId, ScheduleType.of(1001, 1), true)
        assert(schedules.isEmpty())
    }

    @Test
    fun `시작시간 기준으로 친구 스케줄 조회에 성공`() {
        val friendId = generateUserId()
        val content = ScheduleProvider.buildContent(false)
        val firstTime = ScheduleProvider.build1000YearFirstTime()
        val secondTime = ScheduleProvider.build1000YearSecondTime()
        val schedule2 = jpaDataGenerator.scheduleEntityData(content, secondTime, friendId)
        val schedule = jpaDataGenerator.scheduleEntityData(content, firstTime, friendId)
        val schedules = scheduleRepositoryImpl.reads(friendId, ScheduleType.of(1000, 1), false)
        assert(schedules.size == 2)
        assert(schedule.time.startAt == schedules[0].time.startAt)
        assert(schedule2.time.startAt == schedules[1].time.startAt)
    }

    @Test
    fun `시작시간 기준으로 친구 스케줄 조회 실패 - private 스케줄`() {
        val friendId = generateUserId()
        val content = ScheduleProvider.buildContent(true)
        val firstTime = ScheduleProvider.build1000YearFirstTime()
        val secondTime = ScheduleProvider.build1000YearSecondTime()
        jpaDataGenerator.scheduleEntityData(content, secondTime, friendId)
        jpaDataGenerator.scheduleEntityData(content, firstTime, friendId)
        val schedules = scheduleRepositoryImpl.reads(friendId, ScheduleType.of(1000, 1), false)
        assert(schedules.isEmpty())
    }

    fun generateUserId(): String = UUID.randomUUID().toString()
}
