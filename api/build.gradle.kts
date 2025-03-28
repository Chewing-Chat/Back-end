dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("net.coobird:thumbnailator:0.4.14")
    implementation("com.sksamuel.scrimage:scrimage-core:4.0.32")
    implementation("com.sksamuel.scrimage:scrimage-webp:4.0.32")
    implementation(project(":common"))
    implementation(project(":domain"))
    runtimeOnly(project(":storage"))
    runtimeOnly(project(":external"))
    testImplementation(project(":external"))
    testImplementation(project(":storage"))
    testImplementation(project(":tests:api-docs"))
    testRuntimeOnly("com.h2database:h2")
    testRuntimeOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo.spring3x:4.18.0")
    implementation("io.jsonwebtoken:jjwt-api:${property("jjwtVersion")}")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:${property("jjwtVersion")}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${property("jjwtVersion")}")
}

tasks {

    jar {
        enabled = false
    }

    bootJar {
        enabled = true
    }
}

val snippetsDir by extra { file("build/generated-snippets") }

tasks {
    test {
        outputs.dir(snippetsDir)
        useJUnitPlatform()
    }

    asciidoctor {
        inputs.dir(snippetsDir)
        dependsOn(test)

        attributes(
            mapOf("snippets" to snippetsDir.absolutePath),
        )
        baseDirFollowsSourceDir()
    }

    bootJar {
        dependsOn(asciidoctor)
        from("build/docs/asciidoc") {
            into("static/docs")
        }
    }
    register<Copy>("copyAsciidoctor") {
        dependsOn(asciidoctor)
        from(layout.buildDirectory.dir("docs/asciidoc"))
        into(layout.projectDirectory.dir("src/main/resources/static/docs"))
    }

    build {
        dependsOn("copyAsciidoctor")
    }
}
