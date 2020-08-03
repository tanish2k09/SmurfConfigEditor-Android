package com.tanish2k09.sce.data.config

import com.tanish2k09.sce.data.enums.ConfigResponse
import com.tanish2k09.sce.utils.exceptions.ConfigFormatException

class ConfigVar {
    private val _options = mutableListOf<String>()
    val options: List<String>
        get() = _options

    var originalActiveValue = ""

    var activeValue = ""

    var description = CommentStore()
        internal set

    var category = ""

    var title = ""

    var code = ""

    internal fun addOption(option: String) {
        if (_options.contains(option)) {
            throw ConfigFormatException(ConfigResponse.DUPLICATE_OPTION)
        }

        _options.add(option)
    }

    internal fun isEmpty(): Boolean {
        return (
            originalActiveValue.isEmpty() &&
            activeValue.isEmpty() &&
            description.getCommentString().isEmpty() &&
            category.isEmpty() &&
            title.isEmpty() &&
            code.isEmpty() &&
            _options.isEmpty()
        )
    }
}