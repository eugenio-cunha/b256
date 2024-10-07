plugins {
    alias(libs.plugins.b256.jvm.library)
}

dependencies {
    api(libs.kotlinx.datetime)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
}
