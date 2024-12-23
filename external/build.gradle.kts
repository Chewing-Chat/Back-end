dependencies {
    implementation(project(":common"))
    compileOnly(project(":domain"))
    compileOnly("org.springframework:spring-context")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.6")
    implementation("com.google.firebase:firebase-admin:9.2.0")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.security:spring-security-oauth2-client")
    implementation("software.amazon.awssdk:s3:2.29.6")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // 테스트용
    testImplementation(project(":domain"))
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}
