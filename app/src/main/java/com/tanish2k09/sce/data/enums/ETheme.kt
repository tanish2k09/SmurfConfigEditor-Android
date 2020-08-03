package com.tanish2k09.sce.data.enums

import com.tanish2k09.sce.data.constants.DefaultSettings

enum class ETheme(val code: Int, val hex: String) {
    DARK(0, "#121212"),
    BLACK(1, "#000000");

    companion object {
        private val map = ETheme.values().associateBy(ETheme::code)
        fun fromCode(type: Int) = map[type] ?: DefaultSettings.DEFAULT_THEME
    }
}