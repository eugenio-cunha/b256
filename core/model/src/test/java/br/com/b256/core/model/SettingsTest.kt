package br.com.b256.core.model

import org.junit.Test

import org.junit.Assert.*

class SettingsTest {
    @Test
    fun settings_biometrics_true() {
        val settings = Settings(biometrics = true, theme = Theme.LIGHT)

        assertEquals(settings.biometrics, true)
    }

    @Test
    fun settings_biometrics_false() {
        val settings = Settings(biometrics = false, theme = Theme.LIGHT)

        assertEquals(settings.biometrics, false)
    }

    @Test
    fun settings_theme_light() {
        val settings = Settings(biometrics = false, theme = Theme.LIGHT)

        assertEquals(settings.theme, Theme.LIGHT)
    }

    @Test
    fun settings_theme_dark() {
        val settings = Settings(biometrics = false, theme = Theme.DARK)

        assertEquals(settings.theme, Theme.DARK)
    }
}

