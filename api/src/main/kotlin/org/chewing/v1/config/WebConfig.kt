package org.chewing.v1.config

import org.chewing.v1.util.converter.StringToFeedTypeConverter
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
        registry.addConverter(StringToFileCategoryConverter())
        registry.addConverter(StringToFriendSortCriteriaConverter())
        registry.addConverter(StringToFeedTypeConverter())
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/docs/**")
            .addResourceLocations("classpath:/static/docs/")
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(UserArgumentResolver())
    }
}
