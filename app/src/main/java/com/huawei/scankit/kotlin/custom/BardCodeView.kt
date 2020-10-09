package com.huawei.scankit.kotlin.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.huawei.scankit.kotlin.R

class BardCodeView : View {

    private var borderRectangles: Array<Rect> = emptyArray()
    private lateinit var strokePaint: Paint

    constructor(context: Context?) : super(context) {
        initPaint()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initPaint()
    }

    private fun initPaint() {
        strokePaint = Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context, R.color.colorAccent)
            strokeWidth = 2f
        }
    }

    fun setBorderRectangles(rectangles: Array<Rect>) {
        borderRectangles = rectangles
        invalidate()
    }

    fun clear() {
        borderRectangles = arrayOf()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (rect in borderRectangles) {
            canvas.drawRect(rect, strokePaint)
        }
    }
}