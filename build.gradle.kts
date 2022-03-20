plugins {
    kotlin("multiplatform") version "1.6.10"
    id("com.diffplug.spotless") version "6.3.0"
    id("maven-publish")
}

group = "com.ekezet"
version = "0.1.2"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jvmMain by getting

        val jvmTest by getting

        val nativeMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
            }
        }

        val nativeTest by getting
    }
}

spotless {
    kotlin {
        ktlint()
    }
    kotlinGradle {
        ktlint()
    }
}

publishing {
    publications.withType<MavenPublication> {
        pom {
            name.set("kif")
            description.set("Multiplatform logging library")
            url.set("https://github.com/atomgomba/kif")

            developers {
                developer {
                    id.set("atomgomba")
                    name.set("Károly Kiripolszky")
                    email.set("karcsi@ekezet.com")
                }
            }
            licenses {
                license {
                    name.set("Apache License 2.0")
                    url.set("https://opensource.org/licenses/apache-2.0")
                }
            }
            scm {
                url.set("https://github.com/atomgomba/kif")
            }
        }
    }
}
