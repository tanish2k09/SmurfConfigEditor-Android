package com.tanish2k09.sce.ui.components

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.annotation.IntRange
import android.text.InputFilter
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.tanish2k09.sce.helpers.components.ColorFormatHelper.Companion.assertColorValueInRange
import com.tanish2k09.sce.helpers.components.ColorFormatHelper.Companion.formatColorValues
import com.tanish2k09.sce.interfaces.IColorPickerCallback
import com.tanish2k09.sce.R
import com.tanish2k09.sce.databinding.DialogColorPickerBinding
import kotlin.math.roundToInt

/**
 * This is the only class of the project. It consists in a custom dialog that shows the GUI
 * used for choosing a color using three sliders or an input field.
 *
 * @author Simone Pessotto
 * @author Tanish Manku
 */
class ColorPicker(
        private val activity: Activity)
    : Dialog(activity) {

    private lateinit var binding: DialogColorPickerBinding
    private var red: Int = 0
    private var green: Int = 0
    private var blue: Int = 0
    private lateinit var callback: IColorPickerCallback

    private val rangeMax = 255
    private val rangeMin = 0

    var autoclose: Boolean = true

    init {
        if (activity is IColorPickerCallback) {
            callback = activity
        }
    }

    /**
     * Creator of the class. It will initialize the class with the rgb color passed as default
     *
     * @param red      Red color for RGB values (0 - 255)
     * @param green    Green color for RGB values (0 - 255)
     * @param blue     Blue color for RGB values (0 - 255)
     *
     *                 If the value of the colors it's not in the right range (0 - 255) it will
     *                 be place at 0.
     */
    constructor(activity: Activity,
                @IntRange(from = 0, to = 255) red: Int,
                @IntRange(from = 0, to = 255) green: Int,
                  @IntRange(from = 0, to = 255) blue: Int)
    : this(activity)
    {
        this.red = assertColorValueInRange(red)
        this.green = assertColorValueInRange(green)
        this.blue = assertColorValueInRange(blue)
    }

    fun setCallback(listener: IColorPickerCallback) {
        callback = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogColorPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()

        binding.hexCode.filters = arrayOf(InputFilter.LengthFilter(6))

        binding.hexCode.setOnEditorActionListener(
                TextView.OnEditorActionListener { v, actionId, event ->
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event.action == KeyEvent.ACTION_DOWN &&
                                event.keyCode == KeyEvent.KEYCODE_ENTER)
                        {
                            updateColorView(v.text.toString())
                            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE)
                                as InputMethodManager

                            imm.hideSoftInputFromWindow(binding.hexCode.windowToken, 0)

                            return@OnEditorActionListener true
                        }
                        false
                })

        binding.selectColorButton.setOnClickListener {
            sendColor()
        }
    }

    private fun initUi() {
        binding.colorView.setBackgroundColor(getColor())

        initSeekbarTexts()
        initSeekbarListeners()
        setSeekbarValues()

        binding.hexCode.setText(formatColorValues(red, green, blue))
    }

    private fun initSeekbarTexts() {
        binding.redSeekBar.startText = rangeMin.toString()
        binding.greenSeekBar.startText = rangeMin.toString()
        binding.blueSeekBar.startText = rangeMin.toString()

        binding.redSeekBar.endText = rangeMax.toString()
        binding.greenSeekBar.endText = rangeMax.toString()
        binding.blueSeekBar.endText = rangeMax.toString()
    }

    private fun setSeekbarValues() {
        binding.redSeekBar.position = getSeekbarFraction(red)
        binding.greenSeekBar.position = getSeekbarFraction(green)
        binding.blueSeekBar.position = getSeekbarFraction(blue)
    }

    private fun initSeekbarListeners() {
        binding.redSeekBar.positionListener = { pos ->
            red = getFractionToValue(pos)
            binding.redSeekBar.bubbleText = "$red"
            syncViewsToColorUpdate()
        }

        binding.greenSeekBar.positionListener = { pos ->
            green = getFractionToValue(pos)
            binding.greenSeekBar.bubbleText = "$green"
            syncViewsToColorUpdate()
        }

        binding.blueSeekBar.positionListener = { pos ->
            blue = getFractionToValue(pos)
            binding.blueSeekBar.bubbleText = "$blue"
            syncViewsToColorUpdate()
        }
    }

    private fun getSeekbarFraction(value: Int): Float {
        return (value.toFloat() / (rangeMax - rangeMin))
    }

    private fun getFractionToValue(fraction: Float): Int {
        return ((rangeMax - rangeMin) * fraction).roundToInt()
    }

    private fun sendColor() {
        callback.onColorChosen(getColor())

        if(autoclose){
            dismiss()
        }
    }

    /**
     * Method that synchronizes the color between the bars, the view, and the HEX code text.
     *
     * @param input HEX Code of the color.
     */
    private fun updateColorView(input: String) {
        try {
            val color = Color.parseColor("#$input")
            red = Color.red(color)
            green = Color.green(color)
            blue = Color.blue(color)

            setSeekbarValues()
        } catch (ignored: IllegalArgumentException) {
            binding.hexCode.error = activity.resources.getText(R.string.materialcolorpicker__errHex)
        }
    }

    private fun syncViewsToColorUpdate() {
        binding.colorView.setBackgroundColor(getColor())
        binding.hexCode.setText(formatColorValues(red, green, blue))
    }

    /**
     * Getter for the color as Android Color class value.
     *
     * From Android Reference: The Color class defines methods for creating and converting color
     * ints.
     * Colors are represented as packed ints, made up of 3 bytes: red, green, blue.\
     *
     * @return Selected color as Android Color class value.
     */
    private fun getColor(): Int {
        return Color.rgb(red, green, blue)
    }
}