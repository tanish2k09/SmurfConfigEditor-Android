package com.tanish2k09.sce.helpers.config

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.tanish2k09.sce.R
import com.tanish2k09.sce.interfaces.IScriptCallback
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.io.SuFile
import java.lang.ref.WeakReference

class ConfigScript(private val ctx: WeakReference<Context>) {
    private fun detectScript(layer: Int): String {
        val maxConfigPoints = 2

        if (layer > maxConfigPoints)
            return ""

        Log.d("SCE-CIE", " -- Checking layer $layer")

        var path = ""

        if (layer == 1)
            path = ctx.get()?.resources?.getString(R.string.ConfigPath1)?:""
        else if (layer == 2)
            path = ctx.get()?.resources?.getString(R.string.ConfigPath2)?:""

        val script = SuFile(path)
        return if (script.exists()) path else detectScript(layer + 1)

    }

    fun runScript(cb: IScriptCallback?) {
        if (!Shell.rootAccess()) {
            if (ctx.get() != null) {
                Toast.makeText(
                        ctx.get(),
                        ctx.get()!!
                                .resources
                                .getString(R.string.noRoot),
                        Toast.LENGTH_LONG
                ).show()
            }
            Log.e("SCE-CIE", "--- NO ROOT ACCESS ---")
            cb?.callback()
            return
        }

        Log.d("SCE-CIE", "--- RunScript true ---")
        val scriptPath = detectScript(1)

        bashSh(scriptPath, cb)
    }

    private fun bashSh(path: String, cb: IScriptCallback?) {
        Log.d("SCE-CIE", "Executing script path: $path")
        Shell.su("sh $path").submit {
            cb?.callback()
        }
    }
}