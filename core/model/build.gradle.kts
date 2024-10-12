plugins {
    alias(libs.plugins.b256.jvm.library)
}

dependencies {
    implementation(projects.core.common)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
}
