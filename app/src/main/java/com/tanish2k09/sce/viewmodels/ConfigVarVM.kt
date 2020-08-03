package com.tanish2k09.sce.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tanish2k09.sce.data.config.ConfigVar

class ConfigVarVM : ViewModel() {
    private val _displayName = MutableLiveData<String>()
    private val _changed = MutableLiveData<Boolean>()
    private val _value = MutableLiveData<String>()


    val displayName: LiveData<String>
        get() = _displayName

    val changed: LiveData<Boolean>
        get() = _changed

    val value: LiveData<String>
        get() = _value

    lateinit var configVar: ConfigVar
        private set

    fun handleUseTitles(value: Boolean) {
        // Set display name based on title setting
        _displayName.value = if (value) {
            configVar.title
        } else {
            configVar.code
        }
    }

    private fun updateChangeValue() {
        _changed.value = configVar.activeValue != configVar.originalActiveValue
    }

    private fun updateValue() {
        _value.value = configVar.activeValue
        updateChangeValue()
    }

    fun setDataFromConfigVar(configVar: ConfigVar, useTitles: Boolean) {
        this.configVar = configVar

        handleUseTitles(useTitles)
        updateValue()
    }

    fun getInfoText(): String {
        return configVar.description.getCommentString()
    }

    fun setActiveValue(position: Int) {
        configVar.activeValue = configVar.options[position]
        updateValue()
    }
}