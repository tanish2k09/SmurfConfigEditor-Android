package com.tanish2k09.sce.data.enums

enum class ConfigResponse(val description: String) {
    OK("Ok"),
    VAR_NO_CODE("Variable has no code"),
    VAR_NO_TITLE("Variable has no title"),
    VAR_NO_OPTION("Variable has no option"),
    VAR_CODE_MISMATCH("Variable has inconsistent codename"),
    DUPLICATE_VAR("Variable appeared twice, duplicate"),
    DUPLICATE_OPTION("Option appeared twice, duplicate"),
    CONFIG_FILE_BAD("Bad config file"),
    CONFIG_IOE("IOException trying to read config file"),
    CAT_ALREADY_SET("Category overwrite. Maybe missing newline between variables."),
    TITLE_ALREADY_SET("Title overwrite. Maybe missing newline between variables."),
    EMPTY_VAR("Variable is empty")
}