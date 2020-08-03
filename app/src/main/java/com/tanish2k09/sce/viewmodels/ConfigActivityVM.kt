package com.tanish2k09.sce.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.tanish2k09.sce.data.config.ConfigStore
import com.tanish2k09.sce.data.constants.DefaultSettings
import com.tanish2k09.sce.data.constants.StringConstants
import com.tanish2k09.sce.helpers.config.ConfigExporter
import com.tanish2k09.sce.helpers.config.ConfigImporter

class ConfigActivityVM(application: Application) : AndroidViewModel(application) {
    private var _settingsPrefs =
            application.getSharedPreferences(
                    StringConstants.SHARED_PREF_SETTINGS,
                    Context.MODE_PRIVATE
            )

    init {
        registerSettingsChangeListener()
    }

    private fun registerSettingsChangeListener() {
        _settingsPrefs.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == StringConstants.SETTING_RUN_SCRIPT_ON_SAVE) {
                runScript = sharedPreferences.getBoolean(StringConstants.SETTING_RUN_SCRIPT_ON_SAVE, DefaultSettings.RUN_SCRIPT)
            }
        }
    }

    var configStore = ConfigStore()
        private set

    var runScript = DefaultSettings.RUN_SCRIPT
        private set

    fun importConfigFile(folderPath: String, name: String) {
        val importer = ConfigImporter()

        importer.importConfig(folderPath, name)
        configStore = importer.getConfigStore()
    }

    fun exportConfigFile(folderPath: String, name: String) {
        val exporter = ConfigExporter(configStore)
        exporter.exportToStorage(folderPath, name)
    }
}