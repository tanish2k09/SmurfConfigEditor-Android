package com.tanish2k09.sce.helpers.config

import android.content.ContentResolver
import android.net.Uri
import com.tanish2k09.sce.data.config.ConfigDetail
import com.tanish2k09.sce.data.config.ConfigStore

class ConfigExporter(private val store: ConfigStore) {
    fun exportToStorage(resolver: ContentResolver, uri: Uri) {
        val outBW = resolver.openOutputStream(uri)?.bufferedWriter() ?: return

        // Write top comment
        outBW.write(store.topComment.getCommentString())

        val configVarSB = StringBuilder()

        for (code in store.linearCachedCodes) {
            val configVar = store.getVar(code)!!
            configVarSB.appendLine(ConfigDetail.CAT_PREFIX + configVar.category)
            configVarSB.appendLine(ConfigDetail.TITLE_PREFIX + configVar.title)
            configVarSB.appendLine(stringifyComment(configVar.description.getCommentString()))
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
                    "\n${ConfigDetail.COMMENT_PREFIX}"
                )
    }
}