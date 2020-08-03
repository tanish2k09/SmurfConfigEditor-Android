package com.tanish2k09.sce.helpers.config

import android.os.Environment
import android.util.Log
import com.tanish2k09.sce.data.config.ConfigDetail
import com.tanish2k09.sce.data.enums.ConfigResponse
import com.tanish2k09.sce.data.config.ConfigStore
import com.tanish2k09.sce.data.config.ConfigVar
import com.tanish2k09.sce.utils.exceptions.ConfigFormatException
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

class ConfigImporter {
    private val configStore: ConfigStore = ConfigStore()
    private val equalSplitPattern: Regex = "=".toRegex()

    private fun checkFile(file: File): Boolean {
        Log.d("SCE_CIE", "DIR PATH: " + file.path)

        return file.exists()
    }

    fun importConfig(folderPath: String, name: String): ConfigResponse {
        val configFile = File(Environment.getExternalStorageDirectory().path + folderPath, name)
        if (!checkFile(configFile)) {
            throw ConfigFormatException(ConfigResponse.CONFIG_FILE_BAD)
        }

        try {
            importConfigFromFile(configFile)
        } catch (e: IOException) {
            throw ConfigFormatException(ConfigResponse.CONFIG_IOE)
        }

        return ConfigResponse.OK
    }

    private fun isValidOptionLine(line: String): Boolean {
        val splice = line.trim().split(equalSplitPattern)

        return splice.size == 2 && splice[0].isNotEmpty()
    }

    private fun getOptionFromOptionLine(line: String): String {
        return line.split(equalSplitPattern)[1]
    }

    private fun getCodeFromOptionLine(line: String): String {
        return line.split(equalSplitPattern)[0]
    }

    /* @return: Is the code consistent? */
    private fun checkCodeMismatch(configVar: ConfigVar, line: String): Boolean {
        if (configVar.code.isNotEmpty() && configVar.code != getCodeFromOptionLine(line)) {
            throw ConfigFormatException(ConfigResponse.VAR_CODE_MISMATCH)
        }

        return true
    }

    private fun commitConfigVar(newVar: ConfigVar) {
        val response = configStore.addEntry(newVar)

        if (response != ConfigResponse.OK &&
            response != ConfigResponse.EMPTY_VAR)
        {
            throw ConfigFormatException(response)
        }
    }

    private fun importConfigFromFile(configFile: File): ConfigResponse {
        val inBR = BufferedReader(FileReader(configFile))
        var lastLine = inBR.readLine()
        var newVar = ConfigVar()
        var ignoreWhiteLines = true
        var lineCount = 0

        while (lastLine != null) {
            lineCount++
            Log.d("CI","Reading line $lineCount")
            Log.d("CI","Line is: $lastLine")
            when {
                lastLine.startsWith(ConfigDetail.TOP_COMMENT_PREFIX) -> {
                    configStore.topComment.appendLine(lastLine)
                }

                lastLine.startsWith(ConfigDetail.CAT_PREFIX) -> {
                    if (newVar.category.isNotEmpty()) {
                        throw ConfigFormatException(ConfigResponse.CAT_ALREADY_SET)
                    }
                    newVar.category = lastLine.substring(ConfigDetail.CAT_PREFIX.length)
                    ignoreWhiteLines = false
                }

                lastLine.startsWith(ConfigDetail.TITLE_PREFIX) -> {
                    if (newVar.title.isNotEmpty()) {
                        throw ConfigFormatException(ConfigResponse.TITLE_ALREADY_SET)
                    }
                    newVar.title = lastLine.substring(ConfigDetail.TITLE_PREFIX.length)
                    ignoreWhiteLines = false
                }

                lastLine.startsWith(ConfigDetail.COMMENT_PREFIX) -> {
                    newVar.description.appendLine(lastLine.substring(ConfigDetail.COMMENT_PREFIX.length))
                    ignoreWhiteLines = false
                }

                lastLine.startsWith(ConfigDetail.INACTIVE_OPTION) -> run {
                    if (!isValidOptionLine(lastLine)) {
                        return@run
                    }

                    val cutLine = lastLine.substring(ConfigDetail.INACTIVE_OPTION.length)

                    checkCodeMismatch(newVar, cutLine)

                    if (newVar.code.isBlank()) { newVar.code = getCodeFromOptionLine(cutLine) }

                    newVar.addOption(getOptionFromOptionLine(cutLine))
                    ignoreWhiteLines = false
                }

                isValidOptionLine(lastLine) && !lastLine.startsWith("profile.version") -> {
                    if (checkCodeMismatch(newVar, lastLine)) {
                        newVar.code = getCodeFromOptionLine(lastLine)
                        newVar.activeValue = getOptionFromOptionLine(lastLine)
                        newVar.addOption(newVar.activeValue)
                        newVar.originalActiveValue = newVar.activeValue
                    }
                }

                lastLine.trim().isBlank() -> {
                    if (!ignoreWhiteLines) {
                        commitConfigVar(newVar)
                        newVar = ConfigVar()
                        ignoreWhiteLines = false
                    }
                }
            }

            lastLine = inBR.readLine()
        }

        return ConfigResponse.OK
    }

    fun getConfigStore(): ConfigStore {
        return configStore
    }
}