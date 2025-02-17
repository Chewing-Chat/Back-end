package org.chewing.v1.util.converter

import org.chewing.v1.model.feed.FeedType
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class StringToFeedTypeConverter : Converter<String, FeedType> {
    override fun convert(source: String): FeedType {
        return FeedType.valueOf(source.uppercase())
    }
}
