package com.tanish2k09.sce.data.config

import com.tanish2k09.sce.data.enums.ConfigResponse
import java.util.Hashtable

class ConfigStore {
    // A Hashtable to store <code, Object> pairs
    private val configVars: Hashtable<String, ConfigVar> = Hashtable()

    // Maintain a linear searchable list for search adapters
    private val _linearCachedCodes = mutableListOf<String>()
    val linearCachedCodes: List<String>
        get() = _linearCachedCodes

    val topComment: CommentStore = CommentStore()

    /* @return: Status whether configVar was added to store */
    fun addEntry(configVar: ConfigVar): ConfigResponse {
        if (configVar.isEmpty()) return ConfigResponse.EMPTY_VAR
        if (configVar.code.isEmpty()) return ConfigResponse.VAR_NO_CODE
        if (configVar.title.isEmpty()) return ConfigResponse.VAR_NO_TITLE
        if (configVar.options.isEmpty()) return ConfigResponse.VAR_NO_OPTION

        return if (configVars.putIfAbsent(configVar.code, configVar) == null) {
            _linearCachedCodes.add(configVar.code)
            ConfigResponse.OK
        } else {
            ConfigResponse.DUPLICATE_VAR
        }
    }

    fun getVar(code: String): ConfigVar? {
        return configVars[code]
    }

    fun commitActiveValues() {
        for (code in _linearCachedCodes) {
            configVars[code]!!.originalActiveValue = configVars[code]!!.activeValue
        }
    }
}