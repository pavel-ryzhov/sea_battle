package com.example.sea_battle.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent.*
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.sea_battle.R
import com.example.sea_battle.entities.Ship
import kotlin.math.floor


@SuppressLint("ClickableViewAccessibility")
class FieldView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    companion object {
        private const val DRAG_SHIP_1 = 1
        private const val DRAG_SHIP_2 = 2
        private const val DRAG_SHIP_3 = 3
        private const val DRAG_SHIP_4 = 4
        private const val DRAG_ROTATED_SHIP_1 = 5
        private const val DRAG_ROTATED_SHIP_2 = 6
        private const val DRAG_ROTATED_SHIP_3 = 7
        private const val DRAG_ROTATED_SHIP_4 = 8
    }

    private lateinit var coords: Array<String>
    private var locationX = 0f
    private var locationY = 0f
    private var marginX = 0f
    private var marginY = 0f
    private var cellWidth = 0f
    private var drag: Boolean = false
    private var action = -1
    private var dragStartedX = 0f
    private var dragStartedY = 0f
    private var numberOfShipsLocated1 = 0
    private var numberOfShipsLocated2 = 0
    private var numberOfShipsLocated3 = 0
    private var numberOfShipsLocated4 = 0
    private var rotatedShips = true
    val ships: MutableList<Ship> = mutableListOf()

    init {
        setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                ACTION_DOWN -> {
                    drag = true
                    dragStartedX = motionEvent.x
                    dragStartedY = motionEvent.y
                    locationX = dragStartedX
                    locationY = dragStartedY
                    if (rotatedShips) {
                        if (numberOfShipsLocated4 != 1 && dragStartedX > marginX + cellWidth && dragStartedX < marginX + cellWidth * 2 && dragStartedY > marginY + cellWidth * 12 + 10 && dragStartedY < marginY + cellWidth * 16 + 10) {
                            action = DRAG_ROTATED_SHIP_4
                        } else if (numberOfShipsLocated3 != 2 && dragStartedX > marginX + cellWidth * 3 && dragStartedX < marginX + cellWidth * 4 && dragStartedY > marginY + cellWidth * 12 + 10 && dragStartedY < marginY + cellWidth * 15 + 10) {
                            action = DRAG_ROTATED_SHIP_3
                        } else if (numberOfShipsLocated2 != 3 && dragStartedX > marginX + cellWidth * 5 && dragStartedX < marginX + cellWidth * 6 && dragStartedY > marginY + cellWidth * 12 + 10 && dragStartedY < marginY + cellWidth * 14 + 10) {
                            action = DRAG_ROTATED_SHIP_2
                        } else if (numberOfShipsLocated1 != 4 && dragStartedX > marginX + cellWidth * 7 && dragStartedX < marginX + cellWidth * 8 && dragStartedY > marginY + cellWidth * 12 + 10 && dragStartedY < marginY + cellWidth * 13 + 10) {
                            action = DRAG_ROTATED_SHIP_1
                        }
                    } else {
                        if (numberOfShipsLocated4 != 1 && dragStartedX > marginX + cellWidth && dragStartedX < marginX + 5 * cellWidth && dragStartedY > marginY + cellWidth * 12 + 10 && dragStartedY < marginY + cellWidth * 13 + 10) {
                            action = DRAG_SHIP_4
                        } else if (numberOfShipsLocated3 != 2 && dragStartedX > marginX + cellWidth && dragStartedX < marginX + 4 * cellWidth && dragStartedY > marginY + cellWidth * 14 + 10 && dragStartedY < marginY + cellWidth * 15 + 10) {
                            action = DRAG_SHIP_3
                        } else if (numberOfShipsLocated1 != 4 && dragStartedX > marginX + cellWidth * 6 && dragStartedX < marginX + cellWidth * 7 && dragStartedY > marginY + cellWidth * 12 + 10 && dragStartedY < marginY + cellWidth * 13 + 10) {
                            action = DRAG_SHIP_1
                        } else if (numberOfShipsLocated2 != 3 && dragStartedX > marginX + cellWidth * 5 && dragStartedX < marginX + cellWidth * 7 && dragStartedY > marginY + cellWidth * 14 + 10 && dragStartedY < marginY + cellWidth * 15 + 10) {
                            action = DRAG_SHIP_2
                        }
                    }
                    val coords = getCoordsByLocation(dragStartedX, dragStartedY)
                    getShipByCoords(coords[0], coords[1])?.let {
                        action = if (it.rotate) it.type + 4 else it.type
                        ships.remove(it)
                        when (it.type) {
                            4 -> {
                                numberOfShipsLocated4--
                            }
                            3 -> {
                                numberOfShipsLocated3--
                            }
                            2 -> {
                                numberOfShipsLocated2--
                            }
                            1 -> {
                                numberOfShipsLocated1--
                            }
                            else -> {
                            }
                        }
                    }
                    if (dragStartedX > marginX + cellWidth * 9 && dragStartedX < marginX + cellWidth * 10.5f && dragStartedY > marginY + cellWidth * 12 + 10 && dragStartedY < marginY + cellWidth * 13.5f + 10) {
                        rotatedShips = !rotatedShips
                    }
                }
                ACTION_UP -> {
                    drag = false
                    when (action) {
                        DRAG_SHIP_4 -> {
                            val coords =
                                getCoordsByLocation(locationX - 1.5f * cellWidth, locationY)
                            if (coords[0] != -1 && coords[1] != -1) {
                                val ship = Ship(coords[0], coords[1], 4, false)
                                if (checkShip(ship)) {
                                    ships.add(ship)
                                    numberOfShipsLocated4++
                                }
                            }
                        }
                        DRAG_SHIP_3 -> {
                            val coords = getCoordsByLocation(locationX - 1f * cellWidth, locationY)
                            if (coords[0] != -1 && coords[1] != -1) {
                                val ship = Ship(coords[0], coords[1], 3, false)
                                if (checkShip(ship)) {
                                    ships.add(ship)
                                    numberOfShipsLocated3++
                                }
                            }
                        }
                        DRAG_SHIP_2 -> {
                            val coords =
                                getCoordsByLocation(locationX - 0.5f * cellWidth, locationY)
                            if (coords[0] != -1 && coords[1] != -1) {
                                val ship = Ship(coords[0], coords[1], 2, false)
                                if (checkShip(ship)) {
                                    ships.add(ship)
                                    numberOfShipsLocated2++
                                }
                            }
                        }
                        DRAG_SHIP_1 -> {
                            val coords = getCoordsByLocation(locationX, locationY)
                            if (coords[0] != -1 && coords[1] != -1) {
                                val ship = Ship(coords[0], coords[1], 1, false)
                                if (checkShip(ship)) {
                                    ships.add(ship)
                                    numberOfShipsLocated1++
                                }
                            }
                        }
                        DRAG_ROTATED_SHIP_4 -> {
                            val coords =
                                getCoordsByLocation(locationX, locationY - 1.5f * cellWidth)
                            if (coords[0] != -1 && coords[1] != -1) {
                                val ship = Ship(coords[0], coords[1], 4, true)
                                if (checkShip(ship)) {
                                    ships.add(ship)
                                    numberOfShipsLocated4++
                                }
                            }
                        }
                        DRAG_ROTATED_SHIP_3 -> {
                            val coords = getCoordsByLocation(locationX, locationY - cellWidth)
                            if (coords[0] != -1 && coords[1] != -1) {
                                val ship = Ship(coords[0], coords[1], 3, true)
                                if (checkShip(ship)) {
                                    ships.add(ship)
                                    numberOfShipsLocated3++
                                }
                            }
                        }
                        DRAG_ROTATED_SHIP_2 -> {
                            val coords =
                                getCoordsByLocation(locationX, locationY - 0.5f * cellWidth)
                            if (coords[0] != -1 && coords[1] != -1) {
                                val ship = Ship(coords[0], coords[1], 2, true)
                                if (checkShip(ship)) {
                                    ships.add(ship)
                                    numberOfShipsLocated2++
                                }
                            }
                        }
                        DRAG_ROTATED_SHIP_1 -> {
                            val coords = getCoordsByLocation(locationX, locationY)
                            if (coords[0] != -1 && coords[1] != -1) {
                                val ship = Ship(coords[0], coords[1], 1, true)
                                if (checkShip(ship)) {
                                    ships.add(ship)
                                    numberOfShipsLocated1++
                                }
                            }
                        }
                    }
                    action = -1
                }
                ACTION_MOVE -> {
                    if (drag) {
                        locationX = motionEvent.x
                        locationY = motionEvent.y
                    }
                }
            }
            true
        }
    }

    private fun checkShip(ship: Ship): Boolean {
        val correct = checkShipWithoutMessage(ship)
        if (!correct) {
            Toast.makeText(
                context,
                resources.getString(R.string.incorrect_location),
                Toast.LENGTH_SHORT
            ).show()
        }
        return correct
    }

    private fun checkShipWithoutMessage(ship: Ship): Boolean {
        if (ship.rotate) {
            if (ship.y + ship.type > 10) return false
            for (i in (ship.y - 1)..(ship.y + ship.type)) {
                for (j in (ship.x - 1)..(ship.x + 1)) {
                    if (getShipByCoords(j, i) != null) return false
                }
            }
        } else {
            if (ship.x + ship.type > 10) return false
            for (i in (ship.x - 1)..(ship.x + ship.type)) {
                for (j in (ship.y - 1)..(ship.y + 1)) {
                    if (getShipByCoords(i, j) != null) return false
                }
            }
        }
        return true
    }

    private fun getShipByCoords(x: Int, y: Int): Ship? {
        for (ship in ships) {
            if (ship.rotate) {
                if (x == ship.x && y >= ship.y && y < ship.y + ship.type) {
                    return ship
                }
            } else {
                if (y == ship.y && x >= ship.x && x < ship.x + ship.type) {
                    return ship
                }
            }
        }
        return null
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            val paint = Paint()
            it.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint.apply {
                color = ContextCompat.getColor(context, R.color.backgroundColor)
                style = Paint.Style.FILL
            })
            paint.apply {
                color = Color.LTGRAY
                strokeWidth = 3f
                style = Paint.Style.STROKE
            }
            it.drawLines(FloatArray(36) { i ->
                when (i % 4) {
                    0, 2 -> marginX + cellWidth * (i / 4 + 2)
                    1 -> marginY + cellWidth
                    else -> marginY + cellWidth * 11
                }
            }, paint)
            it.drawLines(FloatArray(36) { i ->
                when (i % 4) {
                    1, 3 -> marginY + cellWidth * (i / 4 + 2)
                    0 -> marginX + cellWidth
                    else -> marginX + cellWidth * 11
                }
            }, paint)
            it.drawRect(
                marginX + cellWidth,
                marginY + cellWidth,
                marginX + 11 * cellWidth,
                marginY + 11 * cellWidth,
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
                    marginY + cellWidth - (cellWidth - textHeight) / 2f,
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
                    marginY + cellWidth * i + (cellWidth - textHeight) / 2f + textHeight,
                    paint
                )
            }
            textWidth = paint.measureText("0")
            it.drawText(
                "0",
                marginX + (cellWidth - textWidth) / 2f + textWidth / 2f - 8,
                marginY + cellWidth * 10 + (cellWidth - textHeight) / 2f + textHeight,
                paint
            )

            if (rotatedShips) {
                it.drawRect(
                    marginX + cellWidth,
                    marginY + cellWidth * 12 + 10,
                    marginX + cellWidth * 2,
                    marginY + cellWidth * 16 + 10,
                    paint.apply {
                        color =
                            if (numberOfShipsLocated4 == 1) Color.GRAY else ContextCompat.getColor(
                                context,
                                R.color.foregroundColor
                            )
                    })

                it.drawRect(
                    marginX + cellWidth * 3,
                    marginY + cellWidth * 12 + 10,
                    marginX + cellWidth * 4,
                    marginY + cellWidth * 15 + 10,
                    paint.apply {
                        color =
                            if (numberOfShipsLocated3 == 2) Color.GRAY else ContextCompat.getColor(
                                context,
                                R.color.foregroundColor
                            )
                    })

                it.drawRect(
                    marginX + cellWidth * 5,
                    marginY + cellWidth * 12 + 10,
                    marginX + cellWidth * 6,
                    marginY + cellWidth * 14 + 10,
                    paint.apply {
                        color =
                            if (numberOfShipsLocated2 == 3) Color.GRAY else ContextCompat.getColor(
                                context,
                                R.color.foregroundColor
                            )
                    })

                it.drawRect(
                    marginX + cellWidth * 7,
                    marginY + cellWidth * 12 + 10,
                    marginX + cellWidth * 8,
                    marginY + cellWidth * 13 + 10,
                    paint.apply {
                        color =
                            if (numberOfShipsLocated1 == 4) Color.GRAY else ContextCompat.getColor(
                                context,
                                R.color.foregroundColor
                            )
                    })
            } else {
                it.drawRect(
                    marginX + cellWidth,
                    marginY + cellWidth * 12 + 10,
                    marginX + 5 * cellWidth,
                    marginY + cellWidth * 13 + 10,
                    paint.apply {
                        color =
                            if (numberOfShipsLocated4 == 1) Color.GRAY else ContextCompat.getColor(
                                context,
                                R.color.foregroundColor
                            )
                    })

                it.drawRect(
                    marginX + cellWidth,
                    marginY + cellWidth * 14 + 10,
                    marginX + 4 * cellWidth,
                    marginY + cellWidth * 15 + 10,
                    paint.apply {
                        color =
                            if (numberOfShipsLocated3 == 2) Color.GRAY else ContextCompat.getColor(
                                context,
                                R.color.foregroundColor
                            )
                    })

                it.drawRect(
                    marginX + cellWidth * 6,
                    marginY + cellWidth * 12 + 10,
                    marginX + 7 * cellWidth,
                    marginY + cellWidth * 13 + 10,
                    paint.apply {
                        color =
                            if (numberOfShipsLocated1 == 4) Color.GRAY else ContextCompat.getColor(
                                context,
                                R.color.foregroundColor
                            )
                    })

                it.drawRect(
                    marginX + cellWidth * 5,
                    marginY + cellWidth * 14 + 10,
                    marginX + 7 * cellWidth,
                    marginY + cellWidth * 15 + 10,
                    paint.apply {
                        color =
                            if (numberOfShipsLocated2 == 3) Color.GRAY else ContextCompat.getColor(
                                context,
                                R.color.foregroundColor
                            )
                    })
            }

            it.drawBitmap(
                BitmapFactory.decodeResource(resources, android.R.drawable.ic_menu_rotate),
                null,
                Rect(
                    (marginX + cellWidth * 9).toInt(),
                    (marginY + cellWidth * 12 + 10).toInt(),
                    (marginX + cellWidth * 10.5f).toInt(),
                    (marginY + cellWidth * 13.5f + 10).toInt(),
                ),
                paint
            )


            paint.apply {
                color = ContextCompat.getColor(context, R.color.foregroundColor)
                style = Paint.Style.FILL
            }

            if (drag) {
                when (action) {
                    DRAG_SHIP_4 -> {
                        it.drawRect(
                            locationX - cellWidth * 2f,
                            locationY - cellWidth / 2f,
                            locationX + cellWidth * 2f,
                            locationY + cellWidth / 2f,
                            paint
                        )
                    }
                    DRAG_SHIP_3 -> {
                        it.drawRect(
                            locationX - cellWidth * 1.5f,
                            locationY - cellWidth / 2f,
                            locationX + cellWidth * 1.5f,
                            locationY + cellWidth / 2f,
                            paint
                        )
                    }
                    DRAG_SHIP_2 -> {
                        it.drawRect(
                            locationX - cellWidth,
                            locationY - cellWidth / 2f,
                            locationX + cellWidth,
                            locationY + cellWidth / 2f,
                            paint
                        )
                    }
                    DRAG_SHIP_1 -> {
                        it.drawRect(
                            locationX - cellWidth * 0.5f,
                            locationY - cellWidth / 2f,
                            locationX + cellWidth * 0.5f,
                            locationY + cellWidth / 2f,
                            paint
                        )
                    }
                    DRAG_ROTATED_SHIP_4 -> {
                        it.drawRect(
                            locationX - cellWidth / 2f,
                            locationY - cellWidth * 2f,
                            locationX + cellWidth / 2f,
                            locationY + cellWidth * 2f,
                            paint
                        )
                    }
                    DRAG_ROTATED_SHIP_3 -> {
                        it.drawRect(
                            locationX - cellWidth / 2f,
                            locationY - cellWidth * 1.5f,
                            locationX + cellWidth / 2f,
                            locationY + cellWidth * 1.5f,
                            paint
                        )
                    }
                    DRAG_ROTATED_SHIP_2 -> {
                        it.drawRect(
                            locationX - cellWidth / 2f,
                            locationY - cellWidth,
                            locationX + cellWidth / 2f,
                            locationY + cellWidth,
                            paint
                        )
                    }
                    DRAG_ROTATED_SHIP_1 -> {
                        it.drawRect(
                            locationX - cellWidth / 2f,
                            locationY - cellWidth / 2f,
                            locationX + cellWidth / 2f,
                            locationY + cellWidth / 2f,
                            paint
                        )
                    }
                }
            }

            for (ship in ships) {
                if (ship.rotate) {
                    it.drawRect(
                        marginX + cellWidth * (ship.x + 1),
                        marginY + cellWidth * (ship.y + 1),
                        marginX + cellWidth * (ship.x + 2),
                        marginY + cellWidth * (ship.y + ship.type + 1),
                        paint
                    )
                } else {
                    it.drawRect(
                        marginX + cellWidth * (ship.x + 1),
                        marginY + cellWidth * (ship.y + 1),
                        marginX + cellWidth * (ship.x + ship.type + 1),
                        marginY + cellWidth * (ship.y + 2),
                        paint
                    )
                }
            }
        }
        postInvalidateOnAnimation()
    }

    private fun getCoordsByLocation(x: Float, y: Float): IntArray {
        val absoluteX = x - marginX - cellWidth - 5
        val absoluteY = y - marginY - cellWidth - 5
        if (absoluteX < 0 || absoluteX > cellWidth * 10 || absoluteY < 0 || absoluteY > cellWidth * 10) return intArrayOf(
            -1,
            -1
        )
        return intArrayOf(
            floor(absoluteX / cellWidth).toInt(),
            floor(absoluteY / cellWidth).toInt()
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        coords = resources.getStringArray(R.array.coords)
        cellWidth = floor((width - 42) / 11.0).toFloat()
        marginX = (width - 11 * cellWidth - 10) / 2f
        marginY = marginX
    }
}