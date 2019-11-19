package com.pes.androidmaterialcolorpickerdialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import android.text.InputFilter
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar

import com.tanish2k09.sce.R

import com.pes.androidmaterialcolorpickerdialog.ColorFormatHelper.assertColorValueInRange
import com.pes.androidmaterialcolorpickerdialog.ColorFormatHelper.formatColorValues

/**
 * This is the only class of the project. It consists in a custom dialog that shows the GUI
 * used for choosing a color using three sliders or an input field.
 *
 * @author Simone Pessotto
 */
class ColorPicker
/**
 * Creator of the class. It will initialize the class with black color as default
 *
 * @param activity The reference to the activity where the color picker is called
 */
private constructor(private val activity: Activity) : Dialog(activity), SeekBar.OnSeekBarChangeListener {

    private var colorView: View? = null
    private var alphaSeekBar: SeekBar? = null
    private var redSeekBar: SeekBar? = null
    private var greenSeekBar: SeekBar? = null
    private var blueSeekBar: SeekBar? = null
    private var hexCode: EditText? = null
    private var alpha: Int = 0
    private var red: Int = 0
    private var green: Int = 0
    private var blue: Int = 0
    private var callback: ColorPickerCallback? = null

    private val withAlpha: Boolean
    private var autoclose: Boolean = false

    /**
     * Getter for the color as Android Color class value.
     *
     * From Android Reference: The Color class defines methods for creating and converting color
     * ints.
     * Colors are represented as packed ints, made up of 4 bytes: alpha, red, green, blue.
     * The values are unpremultiplied, meaning any transparency is stored solely in the alpha
     * component, and not in the color components.
     *
     * @return Selected color as Android Color class value.
     */
    var color: Int
        get() = if (withAlpha) Color.argb(alpha, red, green, blue) else Color.rgb(red, green, blue)
        set(@ColorInt color) {
            alpha = Color.alpha(color)
            red = Color.red(color)
            green = Color.green(color)
            blue = Color.blue(color)
        }

    init {

        if (activity is ColorPickerCallback) {
            callback = activity
        }

        this.alpha = 255
        this.red = 0
        this.green = 0
        this.blue = 0

        this.withAlpha = false
        this.autoclose = false
    }

    /**
     * Creator of the class. It will initialize the class with the rgb color passed as default
     *
     * @param activity The reference to the activity where the color picker is called
     * @param red      Red color for RGB values (0 - 255)
     * @param green    Green color for RGB values (0 - 255)
     * @param blue     Blue color for RGB values (0 - 255)
     *
     * If the value of the colors it's not in the right range (0 - 255) it will
     * be place at 0.
     */
    constructor(activity: Activity,
                @IntRange(from = 0, to = 255) red: Int,
                @IntRange(from = 0, to = 255) green: Int,
                @IntRange(from = 0, to = 255) blue: Int) : this(activity) {

        this.red = assertColorValueInRange(red)
        this.green = assertColorValueInRange(green)
        this.blue = assertColorValueInRange(blue)
    }

    /**
     * Enable auto-dismiss for the dialog
     */
    fun enableAutoClose() {
        this.autoclose = true
    }

    fun setCallback(listener: ColorPickerCallback) {
        callback = listener
    }

    /**
     * Simple onCreate function. Here there is the init of the GUI.
     *
     * @param savedInstanceState As usual ...
     */
    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.materialcolorpicker__layout_color_picker)

        colorView = findViewById(R.id.colorView)

        hexCode = findViewById(R.id.hexCode)

        alphaSeekBar = findViewById(R.id.alphaSeekBar)
        redSeekBar = findViewById(R.id.redSeekBar)
        greenSeekBar = findViewById(R.id.greenSeekBar)
        blueSeekBar = findViewById(R.id.blueSeekBar)

        alphaSeekBar!!.setOnSeekBarChangeListener(this)
        redSeekBar!!.setOnSeekBarChangeListener(this)
        greenSeekBar!!.setOnSeekBarChangeListener(this)
        blueSeekBar!!.setOnSeekBarChangeListener(this)

        hexCode!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(if (withAlpha) 8 else 6))

        hexCode!!.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)
            {
                updateColorView(v.text.toString())
                val imm = activity
                        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(hexCode!!.windowToken, 0)

                return@setOnEditorActionListener true
            }
            false
        }

        val okColor = findViewById<Button>(R.id.okColorButton)
        okColor.setOnClickListener { sendColor() }
    }

    private fun initUi() {
        colorView!!.setBackgroundColor(color)

        alphaSeekBar!!.progress = alpha
        redSeekBar!!.progress = red
        greenSeekBar!!.progress = green
        blueSeekBar!!.progress = blue

        if (!withAlpha) {
            alphaSeekBar!!.visibility = View.GONE
        }

        hexCode!!.setText(if (withAlpha)
            formatColorValues(alpha, red, green, blue)
        else
            formatColorValues(red, green, blue)
        )
    }

    private fun sendColor() {
        if (callback != null)
            callback!!.onColorChosen(color)
        if (autoclose) {
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
            alpha = Color.alpha(color)
            red = Color.red(color)
            green = Color.green(color)
            blue = Color.blue(color)

            colorView!!.setBackgroundColor(color)

            alphaSeekBar!!.progress = alpha
            redSeekBar!!.progress = red
            greenSeekBar!!.progress = green
            blueSeekBar!!.progress = blue
        } catch (ignored: IllegalArgumentException) {
            hexCode!!.error = activity.resources.getText(R.string.materialcolorpicker__errHex)
        }

    }

    /**
     * Method called when the user change the value of the bars. This sync the colors.
     *
     * @param seekBar  SeekBar that has changed
     * @param progress The new progress value
     * @param fromUser Whether the user is the reason for the method call
     */
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

        when {
            seekBar.id == R.id.alphaSeekBar -> alpha = progress
            seekBar.id == R.id.redSeekBar -> red = progress
            seekBar.id == R.id.greenSeekBar -> green = progress
            seekBar.id == R.id.blueSeekBar -> blue = progress
        }

        colorView!!.setBackgroundColor(color)

        //Setting the inputText hex color
        hexCode!!.setText(if (withAlpha)
            formatColorValues(alpha, red, green, blue)
        else
            formatColorValues(red, green, blue)
        )

    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun show() {
        super.show()
        initUi()
    }
}