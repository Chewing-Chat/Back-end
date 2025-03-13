package org.chewing.v1.dto

data class ExpoPushTicket(
    val id: String,
    val status: String? = null,
    val message: String? = null,
    val error: String? = null,
    val error_description: String? = null,
)
