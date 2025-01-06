package org.chewing.v1.repository.emoticon

import org.chewing.v1.model.emoticon.EmoticonInfo
import org.chewing.v1.model.emoticon.EmoticonPackInfo

interface EmoticonRepository {
    fun readEmoticons(emoticonPackIds: List<String>): List<EmoticonInfo>
    fun readEmoticonPacks(emoticonPackIds: List<String>): List<EmoticonPackInfo>
}
