package com.tanish2k09.sce.utils

import java.util.ArrayList

class StringVal internal constructor(val name: String) {
    private val options: ArrayList<String> = ArrayList()
    var activeVal = "<no value set>"

    var descriptionString: String? = null
        private set

    var title: String? = null
        internal set

    var category: String? = null
        internal set

    val numOptions: Int
        get() = options.size

    init {
        descriptionString = ""
        title = ""
    }

    internal fun addVal(`val`: String) {
        for (idx in options.indices)
            if (options[idx] == `val`)
                return
        options.add(`val`)
    }

    fun getOption(index: Int): String? {
        return if (index < options.size) options[index] else null
    }

    internal fun clearOptions() {
        options.clear()
    }

    internal fun setDescription(desc: String) {
        descriptionString = desc
    }
}
