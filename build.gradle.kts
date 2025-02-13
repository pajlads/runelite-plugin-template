plugins {
    java
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.runelite.net")
        content {
            includeGroupByRegex("net\\.runelite.*")
        }
    }
    mavenCentral()
}

dependencies {
    val lombokVersion = if (JavaVersion.current() > JavaVersion.VERSION_21) "latest.release" else "1.18.30"
    compileOnly(group = "org.projectlombok", name = "lombok", version = lombokVersion)
    annotationProcessor(group = "org.projectlombok", name = "lombok", version = lombokVersion)
    testCompileOnly(group = "org.projectlombok", name = "lombok", version = lombokVersion)
    testAnnotationProcessor(group = "org.projectlombok", name = "lombok", version = lombokVersion)

    compileOnly(group = "org.jetbrains", name = "annotations", version = "23.0.0")

    val runeLiteVersion = "latest." + if (project.hasProperty("use.snapshot")) "integration" else "release"
    compileOnly(group = "net.runelite", name = "client", version = runeLiteVersion)
    testImplementation(group = "net.runelite", name = "client", version = runeLiteVersion)
    testImplementation(group = "net.runelite", name = "jshell", version = runeLiteVersion)

    val junitVersion = "5.5.2"
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = junitVersion)
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-params", version = junitVersion)
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = junitVersion)
    testRuntimeOnly(group = "org.junit.platform", name = "junit-platform-launcher", version = "1.5.2")

    testImplementation(group = "org.mockito", name = "mockito-core", version = "4.11.0")
    testImplementation(group = "com.google.inject.extensions", name = "guice-testlib", version = "4.1.0") {
        exclude(group = "com.google.inject", module = "guice")
    }
}

group = "com.example"
version = "1.0.0"

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    val version = JavaVersion.VERSION_11.toString()
    sourceCompatibility = version
    targetCompatibility = version
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

tasks.register(name = "shadowJar", type = Jar::class) {
    dependsOn(configurations.testRuntimeClasspath)
    manifest {
        attributes(
            mapOf(
                "Main-Class" to "com.example.ExamplePluginTest",
                "Multi-Release" to true
            )
        )
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(sourceSets.main.get().output)
    from(sourceSets.test.get().output)
    from({
        configurations.testRuntimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        }
    })
    exclude("META-INF/INDEX.LIST")
    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
    exclude("**/module-info.class")

    group = BasePlugin.BUILD_GROUP
    archiveClassifier.set("shadow")
    archiveFileName.set(rootProject.name + "-" + project.version + "-all.jar")
}
