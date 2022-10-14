package com.batuhandemirbas.nobetcieczane

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class PharmacyView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    // Paint object for coloring and styling
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Some colors for the face background, eyes and mouth.
    private var faceColor = Color.YELLOW
    private var eyesColor = Color.BLACK
    private var mouthColor = Color.BLACK
    private var borderColor = Color.BLACK

    // Face border width in pixels
    private var borderWidth = 4.0f

    // View size in pixels
    private var size = 320

    private val mouthPath = Path()

    private val rectF = RectF(0f,200f,0f,200f)

    /*
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        size = Math.min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(size, size)
    }

     */


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawFaceBackground(canvas)

    }

    private fun drawFaceBackground(canvas: Canvas?) {

        // 1
        paint.color = faceColor
        paint.style = Paint.Style.FILL

        // 3
        canvas?.drawRect(rectF, paint)



    }

}