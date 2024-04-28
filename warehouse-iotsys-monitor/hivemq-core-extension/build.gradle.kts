plugins {
    alias(libs.plugins.hivemq.extension)
    alias(libs.plugins.defaults)
    alias(libs.plugins.license)
    id("io.freefair.lombok") version "8.6"
}

group = "org.iotwarehouse"
description = "HiveMQ 4 Hello World Extension - a simple reference for all extension developers"

repositories {
    mavenCentral()
}

hivemqExtension {
    name.set("Warehouse Monitor Extension")
    author.set("HiveMQ")
    priority.set(1000)
    startPriority.set(1000)
    mainClass.set("$group.extension.core.ExtensionMain")
    sdkVersion.set("4.27.0")

    resources {
        from("LICENSE")
    }
}

dependencies {
    implementation(libs.mssql.jdbc)
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        withType<JvmTestSuite> {
            useJUnitJupiter(libs.versions.junit.jupiter)
        }
        "test"(JvmTestSuite::class) {
            dependencies {
                implementation(libs.mockito)
                implementation(libs.log4j)
                implementation(libs.logback.classic)
            }
        }
        "integrationTest"(JvmTestSuite::class) {
            dependencies {
                compileOnly(libs.jetbrains.annotations)
                implementation(libs.hivemq.mqttClient)
                implementation(libs.testcontainers.junitJupiter)
                implementation(libs.testcontainers.hivemq)
                runtimeOnly(libs.logback.classic)
            }
        }
    }
}

/* ******************** checks ******************** */

license {
    header = rootDir.resolve("HEADER")
    mapping("java", "SLASHSTAR_STYLE")
}

/* ******************** debugging ******************** */

tasks.prepareHivemqHome {
    hivemqHomeDirectory.set(file("/your/path/to/hivemq-<VERSION>"))

}

tasks.withType<Test> {
    //jvmArgs("-Djdbc.drivers=com.microsoft.sqlserver.jdbc.SQLServerDriver")
    useJUnitPlatform()
}

tasks.test {
    //systemProperty("jdbc.drivers", "com.microsoft.sqlserver.jdbc.SQLServerDriver")
}



tasks.runHivemqWithExtension {
    debugOptions {
        enabled.set(false)
    }
}