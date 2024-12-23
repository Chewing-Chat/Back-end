package org.chewing.v1.repository

import io.mockk.every
import io.mockk.mockk
import org.chewing.v1.jparepository.feed.FeedJpaRepository
import org.chewing.v1.model.feed.FeedTarget
import org.chewing.v1.repository.jpa.feed.FeedRepositoryImpl
import org.junit.jupiter.api.Test
import java.util.*

class FeedRepositoryTest2 {
    private val feedJpaRepository: FeedJpaRepository = mockk()

    private var feedRepositoryImpl: FeedRepositoryImpl = FeedRepositoryImpl(feedJpaRepository)

    @Test
    fun `피드 업데이트 - 실패(피드가 존재하지 않음)`() {
        val feedId = generateFeedId()
        every { feedJpaRepository.findById(feedId) }.returns(Optional.empty())
        val result = feedRepositoryImpl.update(feedId, FeedTarget.UNHIDE)
        assert(result == null)
    }

    private fun generateFeedId() = UUID.randomUUID().toString()
}
