package org.chewing.v1.util.converter

import org.chewing.v1.model.media.FileCategory
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class StringToFileCategoryConverter : Converter<String, FileCategory> {
    override fun convert(source: String): FileCategory {
        return FileCategory.valueOf(source.uppercase())
    }
}
