package org.chewing.v1.service.emoticon

import org.chewing.v1.implementation.emoticon.EmoticonAggregator
import org.chewing.v1.implementation.emoticon.EmoticonReader
import org.chewing.v1.implementation.user.emoticon.UserEmoticonReader
import org.chewing.v1.model.emoticon.EmoticonPack
import org.springframework.stereotype.Service

@Service
class EmoticonService(
    private val emoticonReader: EmoticonReader,
    private val userEmoticonReader: UserEmoticonReader,
    private val emoticonAggregator: EmoticonAggregator,
) {
    fun fetchUserEmoticonPacks(userId: String): List<EmoticonPack> {
        val userEmoticons = userEmoticonReader.readUserEmoticonPacks(userId)
        val userEmotionsPackIds = userEmoticons.map { it.emoticonPackId }
        val emoticonPacksInfo = emoticonReader.readsPack(userEmotionsPackIds)
        val emoticonsInfo = emoticonReader.reads(userEmotionsPackIds)
        val emoticonPacks = emoticonAggregator.aggregateEmoticons(emoticonPacksInfo, emoticonsInfo)
        return emoticonPacks
    }
}
