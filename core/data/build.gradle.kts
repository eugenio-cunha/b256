plugins {
    alias(libs.plugins.b256.android.library)
    alias(libs.plugins.b256.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "br.com.b256.core.data"
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation(projects.core.network)
    implementation(projects.core.common)
    implementation(projects.core.model)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)
}
