package org.chewing.v1.util

import org.springframework.data.domain.Sort

enum class SortType {
    LATEST {
        override fun toSort(): Sort = Sort.by(Sort.Direction.DESC, "createdAt")
    },
    OLDEST {
        override fun toSort(): Sort = Sort.by(Sort.Direction.ASC, "createdAt")
    },
    FAVORITE_NAME_ASC {
        override fun toSort(): Sort = Sort.by(Sort.Order.asc("favorite"), Sort.Order.asc("name"))
    },
    FAVORITE_NAME_DESC {
        override fun toSort(): Sort = Sort.by(Sort.Order.desc("favorite"), Sort.Order.desc("name"))
    },
    SMALLEST {
        override fun toSort(): Sort = Sort.by(Sort.Direction.ASC, "sequence")
    },
    LARGEST {
        override fun toSort(): Sort = Sort.by(Sort.Direction.DESC, "index")
    },
    SEQUENCE_ASC {
        override fun toSort(): Sort = Sort.by(Sort.Direction.ASC, "sequence")
    },
    SEQUENCE_DESC {
        override fun toSort(): Sort = Sort.by(Sort.Direction.DESC, "sequence")
    }, ;

    abstract fun toSort(): Sort
}
