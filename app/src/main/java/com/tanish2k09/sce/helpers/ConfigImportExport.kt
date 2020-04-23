package com.tanish2k09.sce.helpers

import com.tanish2k09.sce.R
import com.tanish2k09.sce.data.ConfigCache
import com.tanish2k09.sce.data.TopCommentStore
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.io.SuFile

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

import com.tanish2k09.sce.R.string.ConfigPath1
import com.tanish2k09.sce.R.string.ConfigPath2

class ConfigImportExport(private val ctx: Context) {
    private var configFile: File? = null

    private fun openConfig(): Int {
        configFile = File(Environment.getExternalStorageDirectory().path + "/SmurfKernel",
                ctx.resources.getString(R.string.configFile))
        Log.d("SCE_CIE", "DIR PATH: " + configFile!!.path)
        return if (configFile!!.exists()) {
            0
        } else {
            -1
        }
    }

    private fun isValidConfigLine(line: String): Boolean {
        return line.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size == 2
    }

    /* The config file expects a certain notation/format:
     * The top value must be the profile.version
     *
     *  ##~ - Comment/Description
     *  ##* - Category
     *  ##: - Title
     *  # - Inactive Option (if it has exactly 1 '=')
     *  # - Comment (if not an inactive option)
     *  ##/ - Top comment (should be cached)
     */
    fun configImport(): Boolean {
        if (openConfig() != 0) {
            if (configDumpRoot()) {
                Toast.makeText(ctx, ctx.getString(R.string.importRoot), Toast.LENGTH_SHORT).show()
                return configImport()
            }
            return false
        } else {
            try {
                ConfigCache.clearAll()
                val inBR = BufferedReader(FileReader(configFile!!))
                var cache: String? = inBR.readLine()
                var title = ""
                var category = ""
                var description = StringBuilder()

                while (cache != null) {
                    if (isValidConfigLine(cache)) {
                        val configPair: Array<String>
                        if (cache.startsWith("#")) {
                            cache = cache.substring(1)
                            configPair = cache.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            ConfigCache.addConfig(configPair[0], configPair[1], false, title, description.toString(), category)
                        } else {
                            configPair = cache.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            ConfigCache.addConfig(configPair[0], configPair[1], true, title, description.toString(), category)
                        }
                        description = StringBuilder()
                    } else if (cache.startsWith("##:")) {
                        if (cache.length > 3)
                            title = cache.substring(3)
                    } else if (cache.startsWith("##~")) {
                        description.append(cache.substring(3)).append('\n')
                    } else if (cache.startsWith("##*")) {
                        if (cache.length > 3)
                            category = cache.substring(3)
                    } else if (cache.startsWith("##/")) {
                        TopCommentStore.appendLine(cache)
                    }
                    cache = inBR.readLine()
                }
                return true
            } catch (e: IOException) {
                Toast.makeText(ctx, R.string.swwRC, Toast.LENGTH_SHORT).show()
                Log.e("SCE-CIE", "SWW Read", e)
                return false
            }

        }
    }

    private fun detectScript(layer: Int): String {
        val maxConfigPoints = 2

        if (layer > maxConfigPoints)
            return ""
        Log.d("SCE-CIE", " -- Checking layer $layer")

        var path = ""

        if (layer == 1)
            path = ctx.resources.getString(ConfigPath1)
        else if (layer == 2)
            path = ctx.resources.getString(ConfigPath2)

        val script = SuFile(path)
        return if (script.exists()) path else detectScript(layer + 1)

    }

    private fun runScript() {
        if (!Shell.rootAccess()) {
            Toast.makeText(ctx, ctx.resources.getString(R.string.noRoot), Toast.LENGTH_LONG).show()
            Log.e("SCE-CIE", "--- NO ROOT ACCESS ---")
            return
        }
        Log.d("SCE-CIE", "--- RunScript true ---")
        val scriptPath = detectScript(1)

        bashSh(scriptPath)
    }

    private fun bashSh(path: String) {
        Log.d("SCE-CIE", "Executing script path: $path")
        Shell.su("sh $path").submit()
    }

    fun saveConfig(runScript: Boolean) {
        try {
            Log.d("SCE-CIE", "New config file created? " + configFile!!.createNewFile())

            val outBW = BufferedWriter(FileWriter(configFile!!))
            outBW.write(TopCommentStore.comment)

            for (idx in 0 until ConfigCache.configListSize) {
                val svc = ConfigCache.getStringVal(idx)!!

                val category = svc.category

                if (category!!.isNotEmpty() && svc.name != ctx.resources.getString(R.string.profileVersion)) {
                    outBW.write("##*$category")
                    outBW.newLine()
                }

                if (svc.title!!.isNotEmpty()) {
                    outBW.write("##:" + svc.title!!)
                    outBW.newLine()
                }

                if (svc.descriptionString!!.isNotEmpty()) {
                    val lines = svc.descriptionString!!.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    for (string in lines) {
                        outBW.write("##~$string")
                        outBW.newLine()
                    }
                }

                for (option in 0 until svc.numOptions) {
                    val lineToWrite = StringBuilder()

                    if (svc.getOption(option) != svc.activeVal && svc.name != ctx.getString(R.string.profileVersion))
                        lineToWrite.append("#")

                    lineToWrite.append(svc.name).append("=").append(svc.getOption(option))
                    outBW.write(lineToWrite.toString())
                    outBW.newLine()
                }
                outBW.newLine()
                outBW.newLine()
            }
            outBW.flush()
            outBW.close()
            Toast.makeText(ctx, "Saved successfully", Toast.LENGTH_SHORT).show()
            if (runScript)
                runScript()
        } catch (e: IOException) {
            Toast.makeText(ctx, R.string.swwRC, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }

    private fun configDumpRoot(): Boolean {
        val filename = ctx.resources.getString(R.string.configFile)
        val rootConfig = SuFile.open("/$filename")
        if (rootConfig.exists())
            Shell.su("cp /" + filename + " " + configFile!!.absolutePath).exec()
        return configFile!!.exists()
    }
}
