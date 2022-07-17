package com.example.sea_battle.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.sea_battle.R
import com.example.sea_battle.entities.Ship
import kotlin.math.floor

class ShipsLocationView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private lateinit var ships: List<Ship>
    private var cellWidth = 0f
    private var marginX = 0f

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            it.drawColor(context.getColor(R.color.backgroundColor))

            if (::ships.isInitialized && cellWidth != 0f){

                val paint = Paint().apply {
                    color = Color.LTGRAY
                    strokeWidth = 3f
                    style = Paint.Style.STROKE
                }
                it.drawLines(FloatArray(36) { i ->
                    when (i % 4) {
                        0, 2 -> marginX + cellWidth * (i / 4 + 2)
                        1 -> cellWidth
                        else -> cellWidth * 11
                    }
                }, paint)
                it.drawLines(FloatArray(36) { i ->
                    when (i % 4) {
                        1, 3 -> cellWidth * (i / 4 + 2)
                        0 -> marginX + cellWidth
                        else -> marginX + cellWidth * 11
                    }
                }, paint)
                it.drawRect(
                    marginX + cellWidth,
                    cellWidth,
                    marginX + 11 * cellWidth,
                    11 * cellWidth,
                    paint.apply {
                        strokeWidth = 5f
                        color = ContextCompat.getColor(context, R.color.foregroundColor)
                        style = Paint.Style.STROKE
                    })
                paint.apply {
                    color = ContextCompat.getColor(context, R.color.foregroundColor)
                    style = Paint.Style.FILL
                    textSize = cellWidth - 6
                }
                val coords = context.resources.getStringArray(R.array.coords)
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
                        cellWidth - (cellWidth - textHeight) / 2f,
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
                        cellWidth * i + (cellWidth - textHeight) / 2f + textHeight,
                        paint
                    )
                }
                textWidth = paint.measureText("0")
                it.drawText(
                    "0",
                    marginX + (cellWidth - textWidth) / 2f + textWidth / 2f - 8,
                    cellWidth * 10 + (cellWidth - textHeight) / 2f + textHeight,
                    paint
                )

                for (ship in ships) {
                    if (ship.rotate) {
                        it.drawRect(
                            marginX + cellWidth * (ship.x + 1),
                            cellWidth * (ship.y + 1),
                            marginX + cellWidth * (ship.x + 2),
                            cellWidth * (ship.y + ship.type + 1),
                            paint
                        )
                    } else {
                        it.drawRect(
                            marginX + cellWidth * (ship.x + 1),
                            cellWidth * (ship.y + 1),
                            marginX + cellWidth * (ship.x + ship.type + 1),
                            cellWidth * (ship.y + 2),
                            paint
                        )
                    }
                }
            }else{
                postInvalidateOnAnimation()
            }
        }
    }

    fun init(ships: List<Ship>){
        this.ships = ships
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        cellWidth = floor(width / 13f)
        marginX = (width - (cellWidth * 11)) / 2
//        layoutParams = ViewGroup.LayoutParams((cellWidth * 11).toInt(), (cellWidth * 11).toInt())
    }
}