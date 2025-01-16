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

val apiProject = project(":api")
val snippetsDir by extra { apiProject.file("build/generated-snippets") }
val resultDir = apiProject.file("src/main/resources/static/docs")

tasks {
    test {
        finalizedBy("asciidoctor")
    }
    asciidoctor {
        dependsOn(":api:test")
        outputs.dir(snippetsDir)
        inputs.dir(apiProject.file("src/docs/asciidoc"))
        setOutputDir(snippetsDir)
        sourceDir(apiProject.file("src/docs/asciidoc"))
        baseDirFollowsSourceDir()
        attributes(mapOf("snippets" to snippetsDir.toURI().path))

        doLast {
            copy {
                from(outputDir)
                include("**/*.html")
                into(resultDir)
                includeEmptyDirs = false
            }
        }
    }
}
