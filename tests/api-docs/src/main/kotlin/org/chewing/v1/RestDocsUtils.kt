package org.chewing.v1

import org.chewing.v1.error.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.headers.RequestHeadersSnippet
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.payload.ResponseFieldsSnippet

object RestDocsUtils {

    fun requestPreprocessor(): OperationRequestPreprocessor = Preprocessors.preprocessRequest(
        Preprocessors.modifyUris().scheme("http").host("118.67.142.14").removePort(),
        Preprocessors.prettyPrint(),
    )

    fun responsePreprocessor(): OperationResponsePreprocessor =
        Preprocessors.preprocessResponse(Preprocessors.prettyPrint())

    fun responseSuccessFields(): ResponseFieldsSnippet = responseFields(
        PayloadDocumentation.fieldWithPath("status").description("상태 코드"),
        PayloadDocumentation.fieldWithPath("data.message").description("성공 메시지"),
    )

    fun responseErrorFields(status: HttpStatus, errorCode: ErrorCode, description: String): ResponseFieldsSnippet = responseFields(
        PayloadDocumentation.fieldWithPath("status").description(status.value()),
        PayloadDocumentation.fieldWithPath("data.errorCode").description(errorCode.code),
        PayloadDocumentation.fieldWithPath("data.message").description(errorCode.message).description(description),
    )

    fun requestAccessTokenFields(): RequestHeadersSnippet = requestHeaders(
        headerWithName("Authorization").description("Bearer 액세스 토큰"),
    )

    fun requestRefreshTokenFields(): RequestHeadersSnippet = requestHeaders(
        headerWithName("Authorization").description("Bearer 리프레시 토큰"),
    )
}
