package org.chewing.v1.util.helper

import org.chewing.v1.response.HttpResponse
import org.chewing.v1.response.SuccessCreateResponse
import org.chewing.v1.response.SuccessOnlyResponse
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

object
ResponseHelper {

    fun <T> success(data: T): SuccessResponseEntity<T> {
        val response = HttpResponse(status = HttpStatus.OK.value(), data = data)
        return ResponseEntity(response, HttpStatus.OK)
    }

    fun successOnly(): SuccessResponseEntity<SuccessOnlyResponse> {
        val response = HttpResponse(status = HttpStatus.OK.value(), data = SuccessOnlyResponse())
        return ResponseEntity(response, HttpStatus.OK)
    }

    fun successCreateOnly(): SuccessResponseEntity<SuccessCreateResponse> {
        val response = HttpResponse(status = HttpStatus.CREATED.value(), data = SuccessCreateResponse())
        return ResponseEntity(response, HttpStatus.CREATED)
    }

    fun <T> successCreate(data: T): SuccessResponseEntity<T> {
        val response = HttpResponse(status = HttpStatus.CREATED.value(), data = data)
        return ResponseEntity(response, HttpStatus.CREATED)
    }

    fun <T> error(status: HttpStatus, data: T): SuccessResponseEntity<T> {
        val response = HttpResponse(status = status.value(), data = data)
        return ResponseEntity(response, status)
    }
}
