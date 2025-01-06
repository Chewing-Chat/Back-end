package org.chewing.v1.service

import io.mockk.every
import io.mockk.mockk
import org.chewing.v1.TestDataFactory
import org.chewing.v1.implementation.emoticon.EmoticonAggregator
import org.chewing.v1.implementation.emoticon.EmoticonReader
import org.chewing.v1.implementation.user.emoticon.UserEmoticonReader
import org.chewing.v1.repository.emoticon.EmoticonRepository
import org.chewing.v1.repository.user.UserEmoticonRepository
import org.chewing.v1.service.emoticon.EmoticonService
import org.junit.jupiter.api.Test

class EmoticonServiceTest {
    private val emoticonRepository: EmoticonRepository = mockk()
    private val userEmoticonRepository: UserEmoticonRepository = mockk()

    private val emoticonReader = EmoticonReader(emoticonRepository)
    private val userEmoticonReader = UserEmoticonReader(userEmoticonRepository)
    private val emoticonAggregator = EmoticonAggregator()

    private val emoticonService = EmoticonService(emoticonReader, userEmoticonReader, emoticonAggregator)

    @Test
    fun `유저가 가진 이모티콘 팩 리스트 가져오기`() {
        val userId = "userId"
        val emoticonId = "emoticonId"
        val emoticonId2 = "emoticonId2"
        val emoticonId3 = "emoticonId3"
        val emoticonPackId = "emoticonPackId"
        val emoticonInfo = TestDataFactory.createEmoticonInfo(emoticonId, emoticonPackId)
        val emoticonInfo2 = TestDataFactory.createEmoticonInfo(emoticonId2, emoticonPackId)
        val emoticonInfo3 = TestDataFactory.createEmoticonInfo(emoticonId3, emoticonPackId)
        val emoticonPackInfo = TestDataFactory.createEmoticonPackInfo(emoticonPackId)
        val userEmoticonInfo = TestDataFactory.createUserEmoticonPackInfo(userId, emoticonPackId)

        every { userEmoticonRepository.readUserEmoticons(userId) } returns listOf(userEmoticonInfo)
        every { emoticonRepository.readEmoticonPacks(listOf(emoticonPackId)) } returns listOf(emoticonPackInfo)
        every { emoticonRepository.readEmoticons(listOf(emoticonPackId)) } returns listOf(emoticonInfo, emoticonInfo2, emoticonInfo3)

        val emoticonPacks = emoticonService.fetchUserEmoticonPacks(userId)

        assert(emoticonPacks.size == 1)
        assert(emoticonPacks[0].id == emoticonPackId)
        assert(emoticonPacks[0].media.url == emoticonPackInfo.url)
        assert(emoticonPacks[0].media.type.value() == "image/png")
        assert(emoticonPacks[0].name == emoticonPackInfo.name)

        assert(emoticonPacks[0].emoticons.size == 3)
        assert(emoticonPacks[0].emoticons[0].id == emoticonId)
        assert(emoticonPacks[0].emoticons[0].media.type.value() == "image/png")
        assert(emoticonPacks[0].emoticons[0].name == emoticonInfo.name)
        assert(emoticonPacks[0].emoticons[0].media.url == emoticonInfo.url)
    }

    @Test
    fun `유저가 가진 이모티콘 팩 리스트 가져오기 - 이모티콘이 없다면 빈 리시트 반환`() {
        val userId = "userId"
        val emoticonId = "emoticonId"
        val emoticonId2 = "emoticonId2"
        val emoticonId3 = "emoticonId3"
        val emoticonPackId = "emoticonPackId"
        val wrongEmoticonPackId = "wrongEmoticonPackId"
        val emoticonInfo = TestDataFactory.createEmoticonInfo(emoticonId, wrongEmoticonPackId)
        val emoticonInfo2 = TestDataFactory.createEmoticonInfo(emoticonId2, wrongEmoticonPackId)
        val emoticonInfo3 = TestDataFactory.createEmoticonInfo(emoticonId3, wrongEmoticonPackId)
        val emoticonPackInfo = TestDataFactory.createEmoticonPackInfo(emoticonPackId)
        val userEmoticonInfo = TestDataFactory.createUserEmoticonPackInfo(userId, emoticonPackId)

        every { userEmoticonRepository.readUserEmoticons(userId) } returns listOf(userEmoticonInfo)
        every { emoticonRepository.readEmoticonPacks(listOf(emoticonPackId)) } returns listOf(emoticonPackInfo)
        every { emoticonRepository.readEmoticons(listOf(emoticonPackId)) } returns listOf(emoticonInfo, emoticonInfo2, emoticonInfo3)

        val emoticonPacks = emoticonService.fetchUserEmoticonPacks(userId)

        assert(emoticonPacks.isEmpty())
    }
}
