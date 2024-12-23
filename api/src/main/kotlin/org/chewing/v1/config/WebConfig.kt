package org.chewing.v1.config

import org.chewing.v1.util.converter.StringToChatRoomSortCriteriaConverter
import org.chewing.v1.util.converter.StringToDateTargetConverter
import org.chewing.v1.util.converter.StringToFileCategoryConverter
import org.chewing.v1.util.converter.StringToFriendSortCriteriaConverter
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverter(StringToChatRoomSortCriteriaConverter())
        registry.addConverter(StringToFileCategoryConverter())
        registry.addConverter(StringToDateTargetConverter())
        registry.addConverter(StringToFriendSortCriteriaConverter())
    }
}
