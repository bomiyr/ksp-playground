plugins {
    kotlin("jvm") version "2.1.0"
    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
}

group = "com.example"
version = "1.0"

dependencies {
    implementation(project(":annotations"))
    ksp(project(":ksp-processors"))
}
