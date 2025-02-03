package org.chewing.v1.util.aliases

import org.chewing.v1.response.ErrorResponse
import org.chewing.v1.response.HttpResponse
import org.springframework.http.ResponseEntity

typealias SuccessResponseEntity<T> = ResponseEntity<HttpResponse<T>>
typealias ErrorResponseEntity = ResponseEntity<HttpResponse<ErrorResponse>>
