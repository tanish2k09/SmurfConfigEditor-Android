package com.tanish2k09.sce.utils

import java.util.ArrayList

class ConfigCache {
    companion object {

        private val configList = ArrayList<StringVal>()

        fun addConfig(name: String,
                      `val`: String,
                      isValActive: Boolean,
                      title: String?,
                      description: String?,
                      category: String?): Int {

            for (idx in configList.indices) {
                val tmp = configList[idx]
                if (tmp.name == name) {
                    tmp.addVal(`val`)

                    if (isValActive)
                        tmp.activeVal = `val`

                    return idx
                }
            }

            val newStringVal = StringVal(name)
            newStringVal.addVal(`val`)

            if (isValActive)
                newStringVal.activeVal = `val`

            if (title != null)
                newStringVal.title = title

            if (description != null)
                newStringVal.setDescription(description)

            if (category != null && category.isNotEmpty())
                newStringVal.category = category
            else
                newStringVal.category = "Not categorized"

            configList.add(newStringVal)
            return configList.size - 1
        }

        val configListSize: Int
            get() = configList.size

        fun getStringVal(index: Int): StringVal? {
            return if (index <= configList.size) configList[index] else null
        }

        fun clearAll() {
            for (idx in 0 until configListSize)
                configList[idx].clearOptions()
        }
    }
}
