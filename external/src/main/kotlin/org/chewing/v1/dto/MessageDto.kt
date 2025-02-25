package org.chewing.v1.dto

data class MessageDto(
    val to: String,
    val subject: String,
    val content: String,
)
