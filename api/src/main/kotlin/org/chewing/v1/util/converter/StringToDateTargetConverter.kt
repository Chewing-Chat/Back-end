package org.chewing.v1.util.converter

import org.chewing.v1.model.ai.DateTarget
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class StringToDateTargetConverter : Converter<String, DateTarget> {
    override fun convert(source: String): DateTarget = DateTarget.valueOf(source.uppercase())
}
