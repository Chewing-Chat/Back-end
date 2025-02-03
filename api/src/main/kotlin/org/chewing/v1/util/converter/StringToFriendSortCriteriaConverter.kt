package org.chewing.v1.util.converter

import org.chewing.v1.model.friend.FriendSortCriteria
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class StringToFriendSortCriteriaConverter : Converter<String, FriendSortCriteria> {
    override fun convert(source: String): FriendSortCriteria {
        return FriendSortCriteria.valueOf(source.uppercase())
    }
}
