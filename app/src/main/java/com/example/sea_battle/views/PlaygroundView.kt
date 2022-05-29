package com.example.sea_battle.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.sea_battle.R
import kotlin.math.floor

@SuppressLint("ClickableViewAccessibility")
class PlaygroundView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    companion object{
        private const val YOUR_FIELD = 0
        private const val ENEMY_FIELD = 1
    }

    private lateinit var coords: Array<String>
    private var otherPlayerName = "otherPlayerName"
    private var timeBound: Int = 0
    private var cellWidth = 0f
    private var marginX = 0f
    private var marginY = 0f
    private var yourTurn = false


    init {
        setOnTouchListener { _, motionEvent ->
            when(motionEvent.action){
                MotionEvent.ACTION_MOVE -> {

                }
            }

            true
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {

        canvas?.let {
            val paint = Paint()
            it.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint.apply {
                style = Paint.Style.FILL
                color = ContextCompat.getColor(context, R.color.backgroundColor)
            })

            paint.apply {
                color = Color.LTGRAY
                strokeWidth = 3f
                style = Paint.Style.STROKE
            }
            it.drawLines(FloatArray(36) { i ->
                when (i % 4) {
                    0, 2 -> marginX + cellWidth * (i / 4 + 2)
                    1 -> marginY + cellWidth * 2
                    else -> marginY + cellWidth * 12
                }
            }, paint)
            it.drawLines(FloatArray(36) { i ->
                when (i % 4) {
                    1, 3 -> marginY + cellWidth * (i / 4 + 3)
                    0 -> marginX + cellWidth
                    else -> marginX + cellWidth * 11
                }
            }, paint)
            it.drawLines(FloatArray(36) { i ->
                when (i % 4) {
                    0, 2 -> marginX + cellWidth * (i / 4 + 2)
                    1 -> marginY + cellWidth * 15
                    else -> marginY + cellWidth * 25
                }
            }, paint)
            it.drawLines(FloatArray(36) { i ->
                when (i % 4) {
                    1, 3 -> marginY + cellWidth * (i / 4 + 16)
                    0 -> marginX + cellWidth
                    else -> marginX + cellWidth * 11
                }
            }, paint)

            paint.apply {
                color = ContextCompat.getColor(context, R.color.foregroundColor)
                style = Paint.Style.FILL
                textSize = cellWidth - 6
            }
            var textWidth: Float
            var textHeight = 0f
            val rect = Rect()
            for (i in 0..9) {
                paint.getTextBounds(coords[i], 0, 1, rect)
                textHeight = rect.height().toFloat()
                textWidth = rect.width().toFloat()
                it.drawText(
                    coords[i],
                    marginX + cellWidth * (i + 1) + (cellWidth - textWidth) / 2f,
                    marginY + cellWidth * 2 - (cellWidth - textHeight) / 2f,
                    paint
                )
            }
            for (i in 1..10) {
                paint.getTextBounds(i.toString(), 0, i.toString().length, rect)
                textHeight = rect.height().toFloat()
                textWidth = paint.measureText(i.toString())
                it.drawText(
                    i.toString(),
                    0,
                    1,
                    marginX + (cellWidth - textWidth) / 2f,
                    marginY + cellWidth * (i + 1) + (cellWidth - textHeight) / 2f + textHeight,
                    paint
                )
            }
            textWidth = paint.measureText("0")
            it.drawText(
                "0",
                marginX + (cellWidth - textWidth) / 2f + textWidth / 2f - 8,
                marginY + cellWidth * 11 + (cellWidth - textHeight) / 2f + textHeight,
                paint
            )
            for (i in 0..9) {
                paint.getTextBounds(coords[i], 0, 1, rect)
                textHeight = rect.height().toFloat()
                textWidth = rect.width().toFloat()
                it.drawText(
                    coords[i],
                    marginX + cellWidth * (i + 1) + (cellWidth - textWidth) / 2f,
                    marginY + cellWidth * 15 - (cellWidth - textHeight) / 2f,
                    paint
                )
            }
            for (i in 1..10) {
                paint.getTextBounds(i.toString(), 0, i.toString().length, rect)
                textHeight = rect.height().toFloat()
                textWidth = paint.measureText(i.toString())
                it.drawText(
                    i.toString(),
                    0,
                    1,
                    marginX + (cellWidth - textWidth) / 2f,
                    marginY + cellWidth * (i + 14) + (cellWidth - textHeight) / 2f + textHeight,
                    paint
                )
            }
            textWidth = paint.measureText("0")
            it.drawText(
                "0",
                marginX + (cellWidth - textWidth) / 2f + textWidth / 2f - 8,
                marginY + cellWidth * 24 + (cellWidth - textHeight) / 2f + textHeight,
                paint
            )


            paint.apply {
                textSize = cellWidth - 12
                typeface = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    resources.getFont(R.font.montserrat) else ResourcesCompat.getFont(context, R.font.montserrat))
            }

            val str = resources.getString(R.string.your_field)
            paint.getTextBounds(str, 0, str.length, rect)
            textHeight = rect.height().toFloat()
            textWidth = paint.measureText(str)

            it.drawText(
                str,
                marginX + (11 * cellWidth - textWidth) / 2f,
                marginY + (cellWidth - textHeight) / 2f + textHeight,
                paint
            )

            paint.getTextBounds(otherPlayerName, 0, otherPlayerName.length, rect)
            textHeight = rect.height().toFloat()
            textWidth = paint.measureText(otherPlayerName)

            it.drawText(
                otherPlayerName,
                marginX + (11 * cellWidth - textWidth) / 2f,
                marginY + cellWidth * 13 + (cellWidth - textHeight) / 2f + textHeight,
                paint
            )
            paint.apply {
                strokeWidth = 5f
                color = Color.GREEN
                style = Paint.Style.STROKE
            }

            it.drawRect(
                marginX + cellWidth,
                marginY + 2 * cellWidth,
                marginX + cellWidth * 11,
                marginY + 12 * cellWidth,
                paint
            )
            drawProgress(it, ContextCompat.getColor(context, R.color.foregroundColor), YOUR_FIELD, if (yourTurn) 3f else 100f)

            paint.apply {
                strokeWidth = 5f
                color = Color.RED
                style = Paint.Style.STROKE
            }
            it.drawRect(
                marginX + cellWidth,
                marginY + 15 * cellWidth,
                marginX + cellWidth * 11,
                marginY + 25 * cellWidth,
                paint
            )
            drawProgress(it, ContextCompat.getColor(context, R.color.foregroundColor), ENEMY_FIELD, if (!yourTurn) 3f else 100f)


        }

        postInvalidateOnAnimation()
    }

    private fun drawProgress(canvas: Canvas, color: Int, field: Int, percent: Float){
        val paint = Paint().apply {
            this.color = color
            style = Paint.Style.FILL
            strokeWidth = 5f
        }
        val point1X = marginX + cellWidth
        var point1Y = marginY + cellWidth * 2
        val point2X = marginX + cellWidth * 11
        var point2Y = marginY + cellWidth * 2
        val point3X = marginX + cellWidth * 11
        var point3Y = marginY + cellWidth * 12
        val point4X = marginX + cellWidth
        var point4Y = marginY + cellWidth * 12

        if (field == ENEMY_FIELD){
            point1Y += 13 * cellWidth
            point2Y += 13 * cellWidth
            point3Y += 13 * cellWidth
            point4Y += 13 * cellWidth
        }

        if (percent > 12.5f){
            canvas.drawLine(point1X + (point2X - point1X) / 2f, point1Y, point2X, point2Y, paint)
            if (percent > 37.5f){
                canvas.drawLine(point2X, point2Y, point3X, point3Y, paint)
                if (percent > 62.5f){
                    canvas.drawLine(point3X, point3Y, point4X, point4Y, paint)
                    if (percent > 87.5f){
                        canvas.drawLine(point4X, point4Y, point1X, point1Y, paint)
                        canvas.drawLine(point1X, point1Y, point1X + 5 * cellWidth * (percent - 87.5f) / 12.5f, point1Y, paint)
                    }else{
                        canvas.drawLine(point4X, point4Y, point4X, point4Y - 10 * cellWidth * (percent - 62.5f) / 25f, paint)
                    }
                }else{
                    canvas.drawLine(point3X, point3Y, point3X - 10 * cellWidth * (percent - 37.5f) / 25f, point3Y, paint)
                }
            }else{
                canvas.drawLine(point2X, point2Y, point2X, point2Y + 10 * cellWidth * (percent - 12.5f) / 25f, paint)
            }
        }else{
            canvas.drawLine(point1X + (point2X - point1X) / 2f, point1Y, point1X + (point2X - point1X) / 2f + 5 * cellWidth * percent / 12.5f, point1Y, paint)
        }

    }

    fun setTimeBound(timeBound: Int){
        this.timeBound = timeBound
    }
    fun setOtherPlayerName(name: String){
        otherPlayerName = name
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        coords = resources.getStringArray(R.array.coords)
        cellWidth = floor(height / 26f)
        marginX = (width - cellWidth * 11) / 2f
        marginY = 16f

//        cellWidth = floor((width - 42) / 11.0).toFloat()
//        marginX = (width - 11 * cellWidth - 10) / 2f
//        marginY = marginX
    }
}