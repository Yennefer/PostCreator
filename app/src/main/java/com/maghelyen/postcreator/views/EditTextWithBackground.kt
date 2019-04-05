package com.maghelyen.postcreator.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.EditText
import androidx.core.content.ContextCompat

class EditTextWithBackground : EditText {
    private val rect = Rect()
    private val paint = Paint()
    private var backgroundColor: Int? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setBgdColor(color: Int?) {
        backgroundColor = color
    }

    override fun onDraw(canvas: Canvas?) {
        val color = backgroundColor
        color?.let {
            val lineCount = layout.lineCount
            paint.color = ContextCompat.getColor(context, color)
            paint.strokeCap = Paint.Cap.ROUND
            for (i in 0 until lineCount) {
                rect.top = layout.getLineTop(i)
                rect.left = layout.getLineLeft(i).toInt() + paddingLeft
                rect.right = layout.getLineRight(i).toInt() + paddingRight
                rect.bottom = layout.getLineBottom(i) - if (i + 1 == lineCount) 0 else layout.spacingAdd.toInt()

                canvas?.drawRect(rect, paint)
            }
        }

        super.onDraw(canvas)
    }
}