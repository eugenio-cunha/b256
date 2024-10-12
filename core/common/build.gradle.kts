plugins {
    alias(libs.plugins.b256.jvm.library)
    alias(libs.plugins.b256.hilt)
}

dependencies {
    api(libs.kotlinx.datetime)
    api(libs.kotlinx.coroutines.core)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
