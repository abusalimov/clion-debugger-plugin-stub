plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
    id("org.jetbrains.intellij") version "1.14.1"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("232-EAP-SNAPSHOT")
    type.set("CL") // Target IDE Platform

    plugins.set(listOf("com.intellij.clion", "com.intellij.cidr.lang", "com.intellij.cidr.base"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("223")
        untilBuild.set("232.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    test {
        systemProperty("idea.log.config.properties.file", "${project.rootDir}/src/test/resources/test-log.properties")
        systemProperty("idea.test.logs.echo.debug.to.stdout", true)

        // optional properties to enable running tests in a different environment (choose at most one of these):
//        systemProperty("cpp.test.wsl.name", "Ubuntu2204")
//        systemProperty("cpp.test.docker.image.name", "clion-ubuntu-devenv:latest")
//        systemProperty("cpp.test.mingw.home", "C:\\tools\\mingw64\\i686-8.1.0-release-posix-dwarf-rt_v6-rev0\\mingw32")
    }
}
