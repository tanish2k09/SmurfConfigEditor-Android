package com.tanish2k09.sce.utils.exceptions

import com.tanish2k09.sce.data.enums.ConfigResponse
import java.lang.Exception

class ConfigFormatException(val response: ConfigResponse) : Exception() {
    private var lineNumber: Int? = null

    constructor(response: ConfigResponse, lineNum: Int): this(response) {
        lineNumber = lineNum
    }

    fun getLineNumber(): Int? {
        return lineNumber
    }
}