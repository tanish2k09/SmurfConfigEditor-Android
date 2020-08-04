package com.tanish2k09.sce.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tanish2k09.sce.R
import com.tanish2k09.sce.data.constants.DefaultSettings
import com.tanish2k09.sce.data.constants.StringConstants
import com.tanish2k09.sce.data.enums.ETheme
import java.lang.IllegalArgumentException

class SharedPrefsVM(application: Application) : AndroidViewModel(application) {
    private var _settingsPrefs =
            application.getSharedPreferences(
                    StringConstants.SHARED_PREF_SETTINGS,
                    Context.MODE_PRIVATE
            )

    private val _useTitles = MutableLiveData<Boolean>()
    private val _theme = MutableLiveData<ETheme>()
    private val _autoImport = MutableLiveData<Boolean>()
    private val _runScript = MutableLiveData<Boolean>()

    val useTitles: LiveData<Boolean>
        get() = _useTitles

    val theme: LiveData<ETheme>
        get() = _theme

    val autoImport: LiveData<Boolean>
        get() = _autoImport

    val runScript: LiveData<Boolean>
        get() = _runScript

    lateinit var accentColor: String
        private set

    init {
        readSettingsToFields()
    }

    private fun isValidHexCode(value: String): Boolean {
        return try {
            Color.parseColor(value)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun readSettingsToFields() {
        _theme.value =
                ETheme.fromCode(
                        _settingsPrefs.getInt(
                                StringConstants.SETTING_THEME,
                                DefaultSettings.DEFAULT_THEME.code
                        ))

        _useTitles.value =
                _settingsPrefs.getBoolean(
                        StringConstants.SETTING_USE_TITLES,
                        DefaultSettings.USE_TITLES
                )

        _autoImport.value =
                _settingsPrefs.getBoolean(
                        StringConstants.SETTING_IMPORT_ON_START,
                        DefaultSettings.IMPORT_ON_START
                )

        _runScript.value =
                _settingsPrefs.getBoolean(
                        StringConstants.SETTING_RUN_SCRIPT_ON_SAVE,
                        DefaultSettings.RUN_SCRIPT
                )

        accentColor =
                _settingsPrefs.getString(
                        StringConstants.SETTING_ACCENT_COLOR,
                        getApplication<Application>().getString(0+R.color.colorTeal)
                ).toString()
    }

    @SuppressLint("ApplySharedPref")
    fun setUseTitles(useTitles: Boolean) {
        _settingsPrefs.edit().putBoolean(StringConstants.SETTING_USE_TITLES, useTitles).commit()
        _useTitles.value = useTitles
    }

    @SuppressLint("ApplySharedPref")
    fun setTheme(theme: ETheme) {
        _settingsPrefs.edit().putInt(StringConstants.SETTING_THEME, theme.code).commit()
        _theme.value = theme
    }

    @SuppressLint("ApplySharedPref")
    fun setAutoImport(autoImport: Boolean) {
        _settingsPrefs.edit().putBoolean(StringConstants.SETTING_IMPORT_ON_START, autoImport).commit()
        _autoImport.value = autoImport
    }

    @SuppressLint("ApplySharedPref")
    fun setRunScript(runScript: Boolean) {
        _settingsPrefs.edit().putBoolean(StringConstants.SETTING_RUN_SCRIPT_ON_SAVE, runScript).commit()
        _runScript.value = runScript
    }
}