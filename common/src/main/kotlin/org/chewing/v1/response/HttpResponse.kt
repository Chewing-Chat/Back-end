package org.chewing.v1.response

data class HttpResponse<T>(
    val status: Int,
    val data: T,
)
