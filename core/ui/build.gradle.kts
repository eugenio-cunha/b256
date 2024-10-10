plugins {
    alias(libs.plugins.b256.android.library)
    alias(libs.plugins.b256.android.library.compose)
}

android {
    namespace = "br.com.b256.core.ui"
}

dependencies {
    implementation(projects.core.designsystem)

    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
    implementation(libs.androidx.browser)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
}
