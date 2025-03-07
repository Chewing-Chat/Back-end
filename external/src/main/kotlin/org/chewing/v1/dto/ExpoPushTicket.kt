package org.chewing.v1.dto

data class ExpoPushTicket(
    val id: String,
    val status: String,
    val message: String? = null,
)
