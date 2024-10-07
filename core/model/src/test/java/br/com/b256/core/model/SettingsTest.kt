package br.com.b256.core.model

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class SettingsTest {

    private lateinit var settings: Settings

    @Before
    fun setup() {
        settings = Settings(biometrics = true, theme = Theme.LIGHT)
    }

    @Test
    fun settings_biometrics_true() {
        assertEquals(settings.biometrics, true)
    }

    @Test
    fun settings_theme_light() {
        assertEquals(settings.theme, Theme.LIGHT)
    }
}

