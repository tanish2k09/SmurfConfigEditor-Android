package com.tanish2k09.sce.viewmodels

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import com.tanish2k09.sce.R
import com.tanish2k09.sce.data.config.ConfigStore
import com.tanish2k09.sce.data.constants.DefaultSettings
import com.tanish2k09.sce.data.constants.StringConstants
import com.tanish2k09.sce.helpers.config.ConfigExporter
import com.tanish2k09.sce.helpers.config.ConfigImporter

class ConfigActivityVM(private val application: Application) : AndroidViewModel(application) {
    private var _settingsPrefs =
        application.getSharedPreferences(
            StringConstants.SHARED_PREF_SETTINGS,
            Context.MODE_PRIVATE
        )

    private var _configUriPref = application.getSharedPreferences(
        StringConstants.SHARED_PREF_CONFIG_URI,
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

    private var runScript = DefaultSettings.RUN_SCRIPT
//        private set

    fun importConfigFile() {
        if (configUri.value == null) {
            return
        }

        val importer = ConfigImporter()

        importer.importConfig(application.contentResolver, getSmurfFileUri()!!)
        configStore = importer.getConfigStore()
    }

    fun exportConfigFile() {
        val exporter = ConfigExporter(configStore)
        exporter.exportToStorage(application.contentResolver, getSmurfFileUri()!!)
    }

    private var _configUri = MutableLiveData<Uri?>(null)
    val configUri: LiveData<Uri?>
        get() = _configUri.distinctUntilChanged()

    fun readPersistentUri() {
        val uriString = _configUriPref.getString(StringConstants.CONFIG_URI_STRING, "")!!
        if (uriString.isNotEmpty()) {
            _configUri.value = Uri.parse(uriString)
        } else {
            _configUri.value = null
        }
    }

    fun getSmurfFileUri(): Uri? {
        if (configUri.value == null) {
            return null
        }

        val docFile = DocumentFile.fromTreeUri(application, configUri.value!!)
        val smurfConfigFileUri = docFile?.findFile(application.getString(R.string.configFile))?.uri
        return smurfConfigFileUri
    }

    fun storePersistentURI(uri: Uri?) {
        // Store the persistent URI in shared prefs and update VM
        _configUriPref.edit().putString(StringConstants.CONFIG_URI_STRING, uri?.toString()?:"").apply()
        _configUri.value = uri
    }
}