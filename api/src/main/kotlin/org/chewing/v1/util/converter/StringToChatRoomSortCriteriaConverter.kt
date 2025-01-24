package org.chewing.v1.util.converter

import org.chewing.v1.model.chat.room.ChatRoomSortCriteria
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class StringToChatRoomSortCriteriaConverter : Converter<String, ChatRoomSortCriteria> {
    override fun convert(source: String): ChatRoomSortCriteria {
        return ChatRoomSortCriteria.valueOf(source.uppercase())
    }
}
