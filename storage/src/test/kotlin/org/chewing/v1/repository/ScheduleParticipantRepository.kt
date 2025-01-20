package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jpaentity.user.ScheduleParticipantId
import org.chewing.v1.jparepository.user.ScheduleParticipantJpaRepository
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.jpa.user.ScheduleParticipantRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class ScheduleParticipantRepository : JpaContextTest() {
    @Autowired
    private lateinit var scheduleParticipantJpaRepository: ScheduleParticipantJpaRepository

    @Autowired
    private lateinit var jpaDataGenerator: JpaDataGenerator

    @Autowired
    private lateinit var scheduleParticipantRepositoryImpl: ScheduleParticipantRepositoryImpl

    @Test
    fun `참여자 추가에 성공`() {
        val userId = generateUserId()
        val scheduleId = generateScheduleId()
        scheduleParticipantRepositoryImpl.appendParticipants(scheduleId, listOf(userId))
        val result = scheduleParticipantJpaRepository.findById(
            ScheduleParticipantId.of(
                scheduleId = scheduleId,
                userId = userId,
            ),
        )
        assert(result.isPresent)
    }

    @Test
    fun `스케줄들의 참여자들 가져오기에 성공`() {
        val userId1 = generateUserId()
        val scheduleId1 = generateScheduleId()
        val scheduleId2 = generateScheduleId()
        jpaDataGenerator.scheduleParticipantEntityData(
            scheduleId = scheduleId1,
            userId = userId1,
            status = ScheduleParticipantStatus.ACTIVE,
        )
        jpaDataGenerator.scheduleParticipantEntityData(
            scheduleId = scheduleId2,
            userId = userId1,
            status = ScheduleParticipantStatus.ACTIVE,
        )

        val result = scheduleParticipantRepositoryImpl.readsParticipants(
            scheduleIds = listOf(scheduleId1, scheduleId2),
            status = ScheduleParticipantStatus.ACTIVE,
        )
        assert(result.size == 2)
    }

    @Test
    fun `스케줄들의 참여자들 가져오기에 실패 삭제된 유저`() {
        val userId1 = generateUserId()
        val scheduleId1 = generateScheduleId()
        val scheduleId2 = generateScheduleId()
        jpaDataGenerator.scheduleParticipantEntityData(
            scheduleId = scheduleId1,
            userId = userId1,
            status = ScheduleParticipantStatus.DELETED,
        )
        jpaDataGenerator.scheduleParticipantEntityData(
            scheduleId = scheduleId2,
            userId = userId1,
            status = ScheduleParticipantStatus.DELETED,
        )

        val result = scheduleParticipantRepositoryImpl.readsParticipants(
            scheduleIds = listOf(scheduleId1, scheduleId2),
            status = ScheduleParticipantStatus.ACTIVE,
        )
        assert(result.isEmpty())
    }

    @Test
    fun `참여자 가져오기에 성공`() {
        val userId = generateUserId()
        val scheduleId = generateScheduleId()
        jpaDataGenerator.scheduleParticipantEntityData(
            scheduleId = scheduleId,
            userId = userId,
            status = ScheduleParticipantStatus.ACTIVE,
        )

        val result = scheduleParticipantRepositoryImpl.readParticipant(userId, scheduleId)
        assert(result != null)
    }

    @Test
    fun `참여자 가져오기에 실패 - 참여자가 없음`() {
        val userId = generateUserId()
        val scheduleId = generateScheduleId()

        val result = scheduleParticipantRepositoryImpl.readParticipant(userId, scheduleId)
        assert(result == null)
    }

    @Test
    fun `스케줄의 모든 참여자 가져오기 성공`() {
        val userId1 = generateUserId()
        val userId2 = generateUserId()
        val scheduleId = generateScheduleId()
        jpaDataGenerator.scheduleParticipantEntityData(
            scheduleId = scheduleId,
            userId = userId1,
            status = ScheduleParticipantStatus.ACTIVE,
        )
        jpaDataGenerator.scheduleParticipantEntityData(
            scheduleId = scheduleId,
            userId = userId2,
            status = ScheduleParticipantStatus.ACTIVE,
        )

        val result = scheduleParticipantRepositoryImpl.readParticipants(scheduleId)
        assert(result.size == 2)
    }

    @Test
    fun `참여자의 스케줄 아이디 가져오기 성공`() {
        val userId = generateUserId()
        val scheduleId1 = generateScheduleId()
        val scheduleId2 = generateScheduleId()
        jpaDataGenerator.scheduleParticipantEntityData(
            scheduleId = scheduleId1,
            userId = userId,
            status = ScheduleParticipantStatus.ACTIVE,
        )
        jpaDataGenerator.scheduleParticipantEntityData(
            scheduleId = scheduleId2,
            userId = userId,
            status = ScheduleParticipantStatus.ACTIVE,
        )

        val result =
            scheduleParticipantRepositoryImpl.readParticipantScheduleIds(userId, ScheduleParticipantStatus.ACTIVE)
        assert(result.size == 2)
    }

    @Test
    fun `참여자의 스케줄 아이디 가져오기 실패 - 삭제된 참여자`() {
        val userId = generateUserId()
        val scheduleId1 = generateScheduleId()
        val scheduleId2 = generateScheduleId()
        jpaDataGenerator.scheduleParticipantEntityData(
            scheduleId = scheduleId1,
            userId = userId,
            status = ScheduleParticipantStatus.DELETED,
        )
        jpaDataGenerator.scheduleParticipantEntityData(
            scheduleId = scheduleId2,
            userId = userId,
            status = ScheduleParticipantStatus.DELETED,
        )

        val result =
            scheduleParticipantRepositoryImpl.readParticipantScheduleIds(userId, ScheduleParticipantStatus.ACTIVE)
        assert(result.isEmpty())
    }

    @Test
    fun `참여자 삭제에 성공`() {
        val userId = generateUserId()
        val scheduleId = generateScheduleId()
        jpaDataGenerator.scheduleParticipantEntityData(
            scheduleId = scheduleId,
            userId = userId,
            status = ScheduleParticipantStatus.ACTIVE,
        )

        scheduleParticipantRepositoryImpl.removeParticipants(scheduleId, listOf(userId))
        val result = scheduleParticipantJpaRepository.findById(
            ScheduleParticipantId.of(
                scheduleId = scheduleId,
                userId = userId,
            ),
        )
        assert(result.isPresent)
        assert(result.get().toParticipant().status == ScheduleParticipantStatus.DELETED)
    }

    @Test
    fun `스케줄 삭제에 성공 - 모든 참여자가 삭제처리됨`() {
        val userId1 = generateUserId()
        val userId2 = generateUserId()
        val scheduleId = generateScheduleId()

        jpaDataGenerator.scheduleParticipantEntityData(
            scheduleId = scheduleId,
            userId = userId1,
            status = ScheduleParticipantStatus.ACTIVE,
        )
        jpaDataGenerator.scheduleParticipantEntityData(
            scheduleId = scheduleId,
            userId = userId2,
            status = ScheduleParticipantStatus.ACTIVE,
        )
        scheduleParticipantRepositoryImpl.removeAllParticipants(scheduleId)

        val result1 = scheduleParticipantJpaRepository.findById(
            ScheduleParticipantId.of(
                scheduleId = scheduleId,
                userId = userId1,
            ),
        )
        val result2 = scheduleParticipantJpaRepository.findById(
            ScheduleParticipantId.of(
                scheduleId = scheduleId,
                userId = userId2,
            ),
        )
        assert(result1.isPresent)
        assert(result1.get().toParticipant().status == ScheduleParticipantStatus.DELETED)
        assert(result2.isPresent)
        assert(result2.get().toParticipant().status == ScheduleParticipantStatus.DELETED)
    }

    @Test
    fun `유저 삭제시 포함된 모든 참여 삭제`() {
        val userId = generateUserId()
        val scheduleId1 = generateScheduleId()
        val scheduleId2 = generateScheduleId()
        jpaDataGenerator.scheduleParticipantEntityData(
            scheduleId = scheduleId1,
            userId = userId,
            status = ScheduleParticipantStatus.ACTIVE,
        )
        jpaDataGenerator.scheduleParticipantEntityData(
            scheduleId = scheduleId2,
            userId = userId,
            status = ScheduleParticipantStatus.ACTIVE,
        )

        scheduleParticipantRepositoryImpl.removeParticipated(userId)

        val result1 = scheduleParticipantJpaRepository.findById(
            ScheduleParticipantId.of(
                scheduleId = scheduleId1,
                userId = userId,
            ),
        )
        val result2 = scheduleParticipantJpaRepository.findById(
            ScheduleParticipantId.of(
                scheduleId = scheduleId2,
                userId = userId,
            ),
        )
        assert(result1.isPresent)
        assert(result1.get().toParticipant().status == ScheduleParticipantStatus.DELETED)
        assert(result2.isPresent)
        assert(result2.get().toParticipant().status == ScheduleParticipantStatus.DELETED)
    }

    fun generateScheduleId(): String {
        return UUID.randomUUID().toString()
    }

    private fun generateUserId() = UserId.of(UUID.randomUUID().toString())
}
