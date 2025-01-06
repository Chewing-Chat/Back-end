package org.chewing.v1.implementation.emoticon

import org.chewing.v1.model.emoticon.EmoticonInfo
import org.chewing.v1.model.emoticon.EmoticonPackInfo
import org.chewing.v1.repository.emoticon.EmoticonRepository
import org.springframework.stereotype.Component

@Component
class EmoticonReader(
    private val emoticonRepository: EmoticonRepository,
) {
    fun readsPack(emoticonPackIds: List<String>): List<EmoticonPackInfo> {
        return emoticonRepository.readEmoticonPacks(emoticonPackIds)
    }

    fun reads(emoticonPackIds: List<String>): List<EmoticonInfo> {
        return emoticonRepository.readEmoticons(emoticonPackIds)
    }
}
