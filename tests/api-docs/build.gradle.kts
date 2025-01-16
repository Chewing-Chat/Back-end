
dependencies {
    compileOnly("jakarta.servlet:jakarta.servlet-api")
    compileOnly("org.springframework.boot:spring-boot-starter-test")
    api("org.springframework.restdocs:spring-restdocs-mockmvc")
    api("org.springframework.restdocs:spring-restdocs-restassured")
    api("io.rest-assured:spring-mock-mvc")
    api("org.springframework.restdocs:spring-restdocs-asciidoctor")
    implementation(project(":common"))
}

val apiProject = project(":api")
val snippetsDir by extra { apiProject.file("build/generated-snippets") }
val resultDir = apiProject.file("src/main/resources/static/docs")

tasks {

    test {
        finalizedBy(asciidoctor)
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
