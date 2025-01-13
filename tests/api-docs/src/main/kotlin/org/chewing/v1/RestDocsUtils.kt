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
        Preprocessors.modifyUris().scheme("http").host("org.chewing").removePort(),
        Preprocessors.prettyPrint(),
    )

    fun responsePreprocessor(): OperationResponsePreprocessor =
        Preprocessors.preprocessResponse(Preprocessors.prettyPrint())

    fun responseSuccessFields(): ResponseFieldsSnippet = responseFields(
        PayloadDocumentation.fieldWithPath("status").description("200"),
        PayloadDocumentation.fieldWithPath("data.message").description("성공"),
    )

    fun responseErrorFields(status: HttpStatus,errorCode: ErrorCode): ResponseFieldsSnippet = responseFields(
        PayloadDocumentation.fieldWithPath("status").description(status.value()),
        PayloadDocumentation.fieldWithPath("data.errorCode").description(errorCode.code),
        PayloadDocumentation.fieldWithPath("data.message").description(errorCode.message),
    )

    fun responseSuccessCreateFields(): ResponseFieldsSnippet = responseFields(
        PayloadDocumentation.fieldWithPath("status").description("201"),
        PayloadDocumentation.fieldWithPath("data.message").description("생성 완료"),
    )

    fun requestJwtTokenFields(): RequestHeadersSnippet = requestHeaders(
        headerWithName("Authorization").description("Bearer 토큰 정보"),
    )

}
