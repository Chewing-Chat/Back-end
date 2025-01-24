package org.chewing.v1

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.restassured.module.mockmvc.RestAssuredMockMvc
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification
import org.chewing.v1.error.ErrorCode
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.core.convert.converter.Converter
import org.springframework.format.support.DefaultFormattingConversionService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

@Tag("restdocs")
@ExtendWith(RestDocumentationExtension::class)
abstract class RestDocsTest {
    protected lateinit var mockMvc: MockMvcRequestSpecification
    private lateinit var restDocumentation: RestDocumentationContextProvider

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        this.restDocumentation = restDocumentation
    }

    protected fun given(): MockMvcRequestSpecification {
        return mockMvc
    }

    protected fun mockController(
        controller: Any,
        handler: Any,
    ): MockMvcRequestSpecification =
        RestAssuredMockMvc.given().mockMvc(createMockMvc(controller, handler, null))

    protected fun mockControllerWithAdvice(controller: Any, advice: Any): MockMvcRequestSpecification =
        RestAssuredMockMvc.given().mockMvc(createMockMvc(controller, advice, null))

    protected fun mockControllerWithCustomConverter(
        controller: Any,
        customConverter: Converter<String, *>,
    ): MockMvcRequestSpecification = RestAssuredMockMvc.given().mockMvc(createMockMvc(controller, null, customConverter))

    protected fun mockControllerWithAdviceAndCustomConverter(
        controller: Any,
        advice: Any,
        customConverter: Converter<String, *>,
    ): MockMvcRequestSpecification = RestAssuredMockMvc.given().mockMvc(createMockMvc(controller, advice, customConverter))

    private fun createMockMvc(controller: Any, advice: Any?, customConverter: Converter<String, *>?): MockMvc {
        val converter = MappingJackson2HttpMessageConverter(objectMapper())

        val builder = MockMvcBuilders.standaloneSetup(controller)
            .apply<StandaloneMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
            .setMessageConverters(converter)

        advice?.let {
            builder.setControllerAdvice(it)
        }

        customConverter?.let {
            val conversionService = DefaultFormattingConversionService()
            conversionService.addConverter(it)
            builder.setConversionService(conversionService)
        }
        return builder.build()
    }

    private fun objectMapper(): ObjectMapper = jacksonObjectMapper()
        .findAndRegisterModules()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)

    fun MockMvcRequestSpecification.setupAuthenticatedJsonRequest(userId: String = "testUserId", token: String = "accessToken"): MockMvcRequestSpecification {
        return this
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .attribute("userId", userId)
            .header("Authorization", "Bearer $token")
    }

    fun MockMvcRequestSpecification.setupAuthenticatedMultipartRequest(userId: String = "testUserId", token: String = "accessToken"): MockMvcRequestSpecification {
        return this
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .attribute("userId", userId)
            .header("Authorization", "Bearer $token")
    }

    protected fun ValidatableMockMvcResponse.assertErrorResponse(
        status: HttpStatus,
        errorCode: ErrorCode,
    ): ValidatableMockMvcResponse {
        return this
            .statusCode(status.value())
            .body("status", equalTo(status.value()))
            .body("data.errorCode", equalTo(errorCode.code))
            .body("data.message", equalTo(errorCode.message))
    }

    protected fun ValidatableMockMvcResponse.assertCommonSuccessResponse(): ValidatableMockMvcResponse {
        return this
            .statusCode(200)
            .body("status", equalTo(200))
            .body("data.message", equalTo("성공"))
    }

    protected fun ValidatableMockMvcResponse.assertCommonSuccessCreateResponse(): ValidatableMockMvcResponse {
        return this
            .statusCode(201)
            .body("status", equalTo(201))
            .body("data.message", equalTo("생성 완료"))
    }

    protected fun jsonBody(obj: Any): String = objectMapper().writeValueAsString(obj)
}
