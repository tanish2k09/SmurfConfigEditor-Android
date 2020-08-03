package com.tanish2k09.sce.utils.exceptions

import com.tanish2k09.sce.data.enums.ConfigResponse
import java.lang.Exception

class ConfigFormatException(val response: ConfigResponse) : Exception() {
}