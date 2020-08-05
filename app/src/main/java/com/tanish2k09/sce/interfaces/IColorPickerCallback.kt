package com.tanish2k09.sce.interfaces;

import androidx.annotation.ColorInt;

/**
 * Created by Patrick Geselbracht on 2017-03-04
 *
 * @author Patrick Geselbracht
 */
interface IColorPickerCallback {
    /**
     * Gets called whenever a user chooses a color from the ColorPicker, i.e., presses the
     * "Choose" button.
     *
     * @param color Color chosen
     */
    fun onColorChosen(@ColorInt color: Int)
}

