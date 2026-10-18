plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.roborazzi) apply false
}

detekt {
    buildUponDefaultConfig = true
    parallel = true
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
}
