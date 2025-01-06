package org.chewing.v1

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors

object RestDocsUtils {

    fun requestPreprocessor(): OperationRequestPreprocessor = Preprocessors.preprocessRequest(
        Preprocessors.modifyUris().scheme("http").host("org.chewing").removePort(),
        Preprocessors.prettyPrint(),
    )

    fun responsePreprocessor(): OperationResponsePreprocessor = Preprocessors.preprocessResponse(Preprocessors.prettyPrint())
}
