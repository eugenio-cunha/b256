plugins {
    alias(libs.plugins.b256.android.library)
    alias(libs.plugins.b256.hilt)
}

android {
    namespace = "br.com.b256.core.common"
}

dependencies {
    api(libs.kotlinx.datetime)
    api(libs.kotlinx.coroutines.core)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
