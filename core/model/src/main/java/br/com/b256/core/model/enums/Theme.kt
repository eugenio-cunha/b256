package br.com.b256.core.model.enums

/**
 * Representa os temas disponíveis para a aplicação.
 *
 * Cada tema possui um [value] de string correspondente que pode ser usado para armazenamento ou comunicação.
 */
enum class Theme(val value: String) {
    LIGHT(value = "light"),
    DARK(value = "dark"),
    FOLLOW_SYSTEM(value = "follow_system");

    companion object {
        fun from(value: String): Theme = entries
            .find {
                it.name.lowercase() == value.lowercase() ||
                        it.value.lowercase() == value.lowercase()
            } ?: FOLLOW_SYSTEM
    }
}
