dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation(project(":common"))
    implementation(project(":domain"))
    runtimeOnly(project(":storage"))
    runtimeOnly(project(":external"))
    testImplementation(project(":external"))
    testImplementation(testFixtures(project(":storage")))
    testImplementation(project(":tests:api-docs"))
}

val snippetsDir by extra { file("build/generated-snippets") }

tasks {
    val resultDir = file("src/main/resources/static/docs")

    test {
        outputs.dir(snippetsDir)
        useJUnitPlatform()
        finalizedBy(asciidoctor)
    }

    asciidoctor {
        baseDirFollowsSourceDir()
        doLast {
            copy {
                from(outputDir)
                into(resultDir)
            }
        }
        finalizedBy(bootJar)
    }

    jar {
        enabled = false
    }

    bootJar {
        enabled = true
    }
}
