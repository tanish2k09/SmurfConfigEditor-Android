package com.pes.androidmaterialcolorpickerdialog

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.appcompat.widget.AppCompatSeekBar
import android.util.AttributeSet
import android.util.TypedValue

import com.tanish2k09.sce.R

/**
 * Created by Patrick Geselbracht on 2017-03-04
 *
 * @author Patrick Geselbracht <github></github>@pattafeufeu.de>
 * @since v1.1.0
 */
internal class MaterialColorPickerTextSeekBar : AppCompatSeekBar {

    private var textPaint: Paint? = null
    private var textRect: Rect? = null

    @ColorInt
    private var textColor: Int = 0

    @Dimension(unit = 2)
    private var textSize: Float = 0.toFloat()

    private var text: String? = null

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        textPaint = Paint(Paint.LINEAR_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG)
        textRect = Rect()

        if (attrs != null) {

            val typedArray = context.obtainStyledAttributes(
                    attrs, R.styleable.MaterialColorPickerTextSeekBar
            )

            try {

                textColor = typedArray.getColor(
                        R.styleable.MaterialColorPickerTextSeekBar_android_textColor,
                        -0x1000000
                )

                textSize = typedArray.getDimension(
                        R.styleable.MaterialColorPickerTextSeekBar_android_textSize,
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                                18f, resources.displayMetrics)
                )

                text = typedArray.getString(R.styleable
                        .MaterialColorPickerTextSeekBar_android_text)

            } finally {

                typedArray.recycle()

            }

        }

        textPaint!!.color = textColor
        textPaint!!.typeface = Typeface.DEFAULT_BOLD
        textPaint!!.textSize = textSize
        textPaint!!.textAlign = Paint.Align.CENTER

        /* Measures 255 instead of the actual text because otherwise the padding would jump up
         * and down each time the text with its ascender and descenders changes.
         *
         * --
         *
         * Since we're only interested in a roundabout height depending on the text's font size
         * anyway, calculating the text bounds of this value is enough in this case.
         */
        textPaint!!.getTextBounds("255", 0, 3, textRect)

        setPadding(paddingLeft, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (0.6 * textRect!!.height()).toFloat(), resources.displayMetrics).toInt(),
                paddingRight, paddingBottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawText(
                (if (text == null) progress.toString() else text)!!,
                (thumb.bounds.left + paddingLeft).toFloat(),
                (textRect!!.height() + (paddingTop shr 2)).toFloat(),
                textPaint!!
        )

    }
}
