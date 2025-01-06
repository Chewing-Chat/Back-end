package org.chewing.v1.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.S3Configuration
import java.net.URI

@Configuration
class NCPStorageConfig(
    @Value("\${ncp.storage.accessKey}")
    private val accessKey: String,

    @Value("\${ncp.storage.secretKey}")
    private val secretKey: String,

    @Value("\${ncp.storage.region}")
    private val region: String,

    @Value("\${ncp.storage.endpoint}")
    private val endPoint: String,
) {

    @Bean
    fun ncpStorageClient(): S3AsyncClient {
        val credentials = AwsBasicCredentials.create(accessKey, secretKey)

        println("Initializing S3AsyncClient")
        println("AccessKey: $accessKey")
        println("SecretKey: $secretKey")
        println("Region: $region")
        println("Endpoint: $endPoint")

        return S3AsyncClient.builder()
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.of(region))
            .endpointOverride(URI.create(endPoint))
            .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
            .build()
    }
}
