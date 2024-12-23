package org.chewing.v1

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.core.convert.converter.Converter
import org.springframework.format.support.DefaultFormattingConversionService
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

@Tag("restdocs")
@ExtendWith(RestDocumentationExtension::class)
abstract class RestDocsTest {
    protected lateinit var mockMvc: MockMvc
    private lateinit var restDocumentation: RestDocumentationContextProvider

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        this.restDocumentation = restDocumentation
    }

    protected fun mockController(
        controller: Any,
    ): MockMvc =
        createMockMvc(controller, null, null)

    protected fun mockControllerWithAdvice(controller: Any, advice: Any): MockMvc =
        createMockMvc(controller, advice, null)

    protected fun mockControllerWithCustomConverter(
        controller: Any,
        customConverter: Converter<String, *>,
    ): MockMvc = createMockMvc(controller, null, customConverter)

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

    protected fun performCommonSuccessResponse(result: ResultActions) {
        result.andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.message").value("성공"))
    }

    protected fun performCommonSuccessCreateResponse(result: ResultActions) {
        result.andExpect(status().isCreated)
            .andExpect(jsonPath("$.status").value(201))
            .andExpect(jsonPath("$.data.message").value("생성 완료"))
    }

    protected fun jsonBody(obj: Any): String = objectMapper().writeValueAsString(obj)
}
