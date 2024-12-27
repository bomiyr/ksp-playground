plugins {
    kotlin("jvm") version "2.1.0"
}

group = "com.example"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":annotations"))
    implementation("com.google.devtools.ksp:symbol-processing-api:2.1.0-1.0.29")
}
