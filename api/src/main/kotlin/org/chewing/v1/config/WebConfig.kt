package org.chewing.v1.config

import org.chewing.v1.util.converter.StringToChatRoomSortCriteriaConverter
import org.chewing.v1.util.converter.StringToFileCategoryConverter
import org.chewing.v1.util.converter.StringToFriendSortCriteriaConverter
import org.chewing.v1.util.security.UserArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverter(StringToChatRoomSortCriteriaConverter())
        registry.addConverter(StringToFileCategoryConverter())
        registry.addConverter(StringToFriendSortCriteriaConverter())
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
        super.addResourceHandlers(registry)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        println("🔍 [DEBUG] ProviderArgumentResolver 등록됨") // 디버깅 로그 추가
        resolvers.add(UserArgumentResolver())
    }
}
