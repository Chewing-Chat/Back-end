package org.chewing.v1.implementation.emoticon

import org.chewing.v1.model.emoticon.Emoticon
import org.chewing.v1.model.emoticon.EmoticonInfo
import org.chewing.v1.model.emoticon.EmoticonPack
import org.chewing.v1.model.emoticon.EmoticonPackInfo
import org.springframework.stereotype.Component

@Component
class EmoticonAggregator {

    fun aggregateEmoticons(
        emotionPacks: List<EmoticonPackInfo>,
        emoticons: List<EmoticonInfo>,
    ): List<EmoticonPack> {
        val emoticonGroup = emoticons.groupBy { it.emoticonPackId }
        return emoticonGroup.mapNotNull { emoticonInfo ->
            val pack = emotionPacks.find { it.id == emoticonInfo.key }
            pack?.let {
                val emoticonList = emoticonInfo.value.map { info ->
                    Emoticon.of(info.id, info.name, info.url)
                }
                EmoticonPack.of(
                    it.id,
                    it.name,
                    it.url,
                    emoticonList,
                )
            }
        }
    }
}
