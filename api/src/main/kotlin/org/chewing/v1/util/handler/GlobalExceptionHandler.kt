package org.chewing.v1.util.handler

import mu.KotlinLogging
import org.chewing.v1.error.AuthorizationException
import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.response.ErrorResponse
import org.chewing.v1.util.aliases.ErrorResponseEntity
import org.chewing.v1.util.helper.ResponseHelper
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = KotlinLogging.logger {}

    private fun handleException(e: Exception, errorCode: ErrorCode, status: HttpStatus): ErrorResponseEntity {
        logger.info { "ErrorCode: ${errorCode.code}, Message: ${errorCode.message}, Class: ${e.stackTrace.first().className}" }
        return ResponseHelper.error(status, ErrorResponse.Companion.from(errorCode))
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(e: MissingServletRequestParameterException): ErrorResponseEntity = handleException(e, ErrorCode.VARIABLE_WRONG, HttpStatus.BAD_REQUEST)

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(e: HttpRequestMethodNotSupportedException): ErrorResponseEntity = handleException(e, ErrorCode.PATH_WRONG, HttpStatus.BAD_REQUEST)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ErrorResponseEntity = handleException(e, ErrorCode.VARIABLE_WRONG, HttpStatus.BAD_REQUEST)

    @ExceptionHandler(AuthorizationException::class)
    protected fun handleAuthorizationException(e: AuthorizationException): ErrorResponseEntity = handleException(e, e.errorCode, HttpStatus.UNAUTHORIZED)

    @ExceptionHandler(HttpMessageNotReadableException::class)
    protected fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ErrorResponseEntity = handleException(e, ErrorCode.VARIABLE_WRONG, HttpStatus.BAD_REQUEST)

    @ExceptionHandler(NotFoundException::class)
    protected fun handleNotFoundException(e: NotFoundException): ErrorResponseEntity = handleException(e, e.errorCode, HttpStatus.NOT_FOUND)

    @ExceptionHandler(ConflictException::class)
    protected fun handleConflictException(e: ConflictException): ErrorResponseEntity = handleException(e, e.errorCode, HttpStatus.CONFLICT)

    // Optional: 처리되지 않은 예외를 위한 핸들러 추가
    @ExceptionHandler(Exception::class)
    fun handleGenericException(e: Exception): ErrorResponseEntity {
        logger.error(e) { "예기치 않은 오류 발생: ${e.message}" }
        return ResponseHelper.error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorResponse.Companion.from(ErrorCode.INTERNAL_SERVER_ERROR))
    }
}
