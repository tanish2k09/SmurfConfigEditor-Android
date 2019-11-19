package com.pes.androidmaterialcolorpickerdialog

import androidx.annotation.IntRange

internal object ColorFormatHelper {

    /**
     * Checks whether the specified value is between (including bounds) 0 and 255
     *
     * @param colorValue Color value
     * @return Specified input value if between 0 and 255, otherwise 0
     */
    fun assertColorValueInRange(@IntRange(from = 0, to = 255) colorValue: Int): Int {
        return if (colorValue in 0..255) colorValue else 0
    }

    /**
     * Formats individual RGB values to be output as a HEX string.
     *
     * Beware: If color value is lower than 0 or higher than 255, it's reset to 0.
     *
     * @param red   Red color value
     * @param green Green color value
     * @param blue  Blue color value
     * @return HEX String containing the three values
     */
    fun formatColorValues(
            @IntRange(from = 0, to = 255) red: Int,
            @IntRange(from = 0, to = 255) green: Int,
            @IntRange(from = 0, to = 255) blue: Int): String {

        return String.format("%02X%02X%02X",
                assertColorValueInRange(red),
                assertColorValueInRange(green),
                assertColorValueInRange(blue)
        )
    }

    /**
     * Formats individual ARGB values to be output as an 8 character HEX string.
     *
     * Beware: If any value is lower than 0 or higher than 255, it's reset to 0.
     *
     * @param alpha Alpha value
     * @param red   Red color value
     * @param green Green color value
     * @param blue  Blue color value
     * @return HEX String containing the three values
     * @since v1.1.0
     */
    fun formatColorValues(
            @IntRange(from = 0, to = 255) alpha: Int,
            @IntRange(from = 0, to = 255) red: Int,
            @IntRange(from = 0, to = 255) green: Int,
            @IntRange(from = 0, to = 255) blue: Int): String {

        return String.format("%02X%02X%02X%02X",
                assertColorValueInRange(alpha),
                assertColorValueInRange(red),
                assertColorValueInRange(green),
                assertColorValueInRange(blue)
        )
    }

}
