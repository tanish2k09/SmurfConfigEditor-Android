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
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import android.widget.TextView
import com.pes.androidmaterialcolorpickerdialog.ColorFormatHelper.Companion.assertColorValueInRange
import com.pes.androidmaterialcolorpickerdialog.ColorFormatHelper.Companion.formatColorValues
import com.tanish2k09.sce.R
import com.tanish2k09.sce.databinding.DialogColorPickerBinding

/**
 * This is the only class of the project. It consists in a custom dialog that shows the GUI
 * used for choosing a color using three sliders or an input field.
 *
 * @author Simone Pessotto
 */
class ColorPicker(
        private val activity: Activity)
    : Dialog(activity), SeekBar.OnSeekBarChangeListener {

    private lateinit var binding: DialogColorPickerBinding
    private var red: Int = 0
    private var green: Int = 0
    private var blue: Int = 0
    private lateinit var callback: ColorPickerCallback

    private var autoclose: Boolean = true

    init {
        if (activity is ColorPickerCallback) {
            callback = activity
        }
    }

    /**
     * Creator of the class. It will initialize the class with the rgb color passed as default
     *
     * @param activity The reference to the activity where the color picker is called
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
    : this(activity) {

        this.red = assertColorValueInRange(red)
        this.green = assertColorValueInRange(green)
        this.blue = assertColorValueInRange(blue)
    }

    fun setCallback(listener: ColorPickerCallback) {
        callback = listener
    }

    @Override
    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)

        binding = DialogColorPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.redSeekBar.setOnSeekBarChangeListener(this)
        binding.greenSeekBar.setOnSeekBarChangeListener(this)
        binding.blueSeekBar.setOnSeekBarChangeListener(this)

        binding.hexCode.filters = arrayOf(InputFilter.LengthFilter(6))

        binding.hexCode.setOnEditorActionListener(
                TextView.OnEditorActionListener { v, actionId, event ->
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                        {
                            updateColorView(v.text.toString())
                            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE)
                                as InputMethodManager

                            imm.hideSoftInputFromWindow(binding.hexCode.windowToken, 0)

                            return@OnEditorActionListener true
                        }
                        false
                });


        binding.okColorButton.setOnClickListener {
            sendColor()
        }
    }

    private fun initUi() {
        binding.colorView.setBackgroundColor(getColor())

        binding.redSeekBar.progress = red
        binding.greenSeekBar.progress = green
        binding.blueSeekBar.progress = blue

        binding.hexCode.setText(formatColorValues(red, green, blue))
    }

    private fun sendColor() {
        callback.onColorChosen(getColor())

        if(autoclose){
            dismiss()
        }
    }

    fun setColor(@ColorInt color: Int) {
        red = Color.red(color)
        green = Color.green(color)
        blue = Color.blue(color)
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

            binding.colorView.setBackgroundColor(getColor())

            binding.redSeekBar.progress = red
            binding.greenSeekBar.progress = green
            binding.blueSeekBar.progress = blue
        } catch (ignored: IllegalArgumentException) {
            binding.hexCode.error = activity.resources.getText(R.string.materialcolorpicker__errHex)
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

        when (seekBar.id) {
            R.id.redSeekBar -> {
                red = progress
            }
            R.id.greenSeekBar -> {
                green = progress
            }
            R.id.blueSeekBar -> {
                blue = progress
            }
        }

        binding.colorView.setBackgroundColor(getColor())

        //Setting the inputText hex color
        binding.hexCode.setText(formatColorValues(red, green, blue))
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

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
        return Color.rgb(red, green, blue);
    }

    override fun show() {
        super.show()
        initUi()
    }
}