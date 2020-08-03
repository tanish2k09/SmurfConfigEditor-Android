package com.tanish2k09.sce.helpers.config

import android.os.Environment
import com.tanish2k09.sce.data.config.ConfigDetail
import com.tanish2k09.sce.data.config.ConfigStore
import java.io.BufferedWriter
import java.io.FileWriter

class ConfigExporter(private val store: ConfigStore) {
    fun exportToStorage(folderPath: String, name: String) {
        val outBW = BufferedWriter(
                FileWriter(
                        Environment.getExternalStorageDirectory().path +
                                folderPath + '/' +
                                name
                ))

        // Write top comment
        outBW.write(store.topComment.getCommentString())

        val configVarSB = StringBuilder()

        for (code in store.linearCachedCodes) {
            val configVar = store.getVar(code)!!
            configVarSB.appendln(ConfigDetail.CAT_PREFIX + configVar.category)
            configVarSB.appendln(ConfigDetail.TITLE_PREFIX + configVar.title)
            configVarSB.appendln(stringifyComment(configVar.description.getCommentString()))
            configVarSB.append(
                    stringifyOptions(configVar.options, configVar.activeValue, configVar.code)
            )

            outBW.write(configVarSB.toString())
            outBW.newLine()
            configVarSB.clear()
        }

        outBW.flush()
        outBW.close()
    }

    private fun stringifyOptions(options: List<String>, activeOption: String, code: String): String {
        val builder = StringBuilder()

        for (option in options) {
            if (option != activeOption) {
                builder.append(ConfigDetail.INACTIVE_OPTION)
            }

            builder.append("${code}=${option}\n")
        }

        return builder.toString()
    }

    private fun stringifyComment(comment: String): String {
        return ConfigDetail.COMMENT_PREFIX +
                comment.replace(
                        "\n",
                        "\n${ConfigDetail.COMMENT_PREFIX}")
    }
}