plugins {
    alias(libs.plugins.b256.android.feature)
    alias(libs.plugins.b256.android.library.compose)
}

android {
    namespace = "br.com.b256.feature.home"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.model)

    testImplementation(libs.robolectric)
    testImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
}
