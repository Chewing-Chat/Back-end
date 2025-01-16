dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation(project(":common"))
    implementation(project(":domain"))
    runtimeOnly(project(":storage"))
    runtimeOnly(project(":external"))
    testImplementation(project(":external"))
    testImplementation(project(":storage"))
    testImplementation(project(":tests:api-docs"))
    testRuntimeOnly("com.h2database:h2")
    testRuntimeOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo.spring3x:4.18.0")
}

tasks {

    jar {
        enabled = false
    }

    bootJar {
        enabled = true
    }

    test {
        finalizedBy(":tests:api-docs:asciidoctor")
    }
}
