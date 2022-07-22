package com.example.sea_battle.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.sea_battle.R
import com.example.sea_battle.data.services.game.GameService
import com.example.sea_battle.entities.Ship
import com.example.sea_battle.extensions.SetExtensions.Companion.addIntArray
import com.example.sea_battle.extensions.SetExtensions.Companion.containsAllIntArrays
import com.example.sea_battle.utils.Vibrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.floor

@SuppressLint("ClickableViewAccessibility")
class PlaygroundView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    companion object {
        const val THIS_PLAYER_FIELD = 0
        const val OTHER_PLAYER_FIELD = 1

        const val THIS_PLAYER_TURN = 0
        const val OTHER_PLAYER_TURN = 1

        const val THIS_PLAYER_VICTORY = 0
        const val OTHER_PLAYER_VICTORY = 1
    }

    private var gameService: GameService? = null

    private lateinit var coords: Array<String>
    private var otherPlayerName = "otherPlayerName"
    private var timeBound = -1
    private var cellWidth = 0f
    private var marginX = 0f
    private var marginY = 0f
    private var turn = -1
    private var turnStartTime: Long = -1
    private val crosses = mutableSetOf<IntArray>()
    private val hits = mutableSetOf<IntArray>()


    init {
        setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (turn == THIS_PLAYER_TURN) {
                    if (isClickInField(OTHER_PLAYER_FIELD, motionEvent.x, motionEvent.y)) {
                        val coords = getCoordsByLocation(motionEvent.x, motionEvent.y)
                        if (!crosses.containsAllIntArrays(setOf(coords))) {
                            CoroutineScope(Dispatchers.IO + Job()).launch(Dispatchers.IO) {
                                gameService?.executeClick(coords.copyOfRange(1, 3))
                            }
                            crosses.addIntArray(coords)
                            val ship = getShipByCoords(
                                OTHER_PLAYER_FIELD,
                                coords.component2(),
                                coords.component3()
                            )
                            turnStartTime = System.currentTimeMillis()
                            if (ship != null) {
                                hits.add(coords)
                                checkIfShipIsSunk(OTHER_PLAYER_FIELD, ship)
                                Vibrator.vibrate(context, 100)
                            } else {
                                turn = (turn + 1) % 2
                            }
                        }
                    }
                }
            }

            true
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {

        if (hits.filter { (if (turn == OTHER_PLAYER_TURN) THIS_PLAYER_FIELD else OTHER_PLAYER_FIELD) == it[0] }.size == 20){
            gameService?.finishGame(if (turn == OTHER_PLAYER_TURN) OTHER_PLAYER_VICTORY else THIS_PLAYER_VICTORY)
        }
        if (turn == THIS_PLAYER_TURN) {
            if ((System.currentTimeMillis() - turnStartTime) / 1000 >= timeBound){
                gameService?.finishGame(OTHER_PLAYER_VICTORY)
            }
        }

        canvas?.let { it ->
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
                    resources.getFont(R.font.montserrat) else ResourcesCompat.getFont(
                    context,
                    R.font.montserrat
                ))
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

            gameService?.let { gameService ->
                drawShips(it, THIS_PLAYER_FIELD, gameService.thisPlayerShips)
            }

            drawShips(
                it,
                THIS_PLAYER_FIELD,
                hits.filter { it.component1() == THIS_PLAYER_FIELD }
                    .map { Ship(it.component2(), it.component3(), 1, false) })
            drawShips(
                it,
                OTHER_PLAYER_FIELD,
                hits.filter { it.component1() == OTHER_PLAYER_FIELD }
                    .map { Ship(it.component2(), it.component3(), 1, false) })

            for (cross in crosses) {
                drawCross(it, cross.component1(), cross.component2(), cross.component3())
            }


            it.drawText(
                "$otherPlayerName:",
                marginX + (11 * cellWidth - textWidth) / 2f,
                marginY + cellWidth * 13 + (cellWidth - textHeight) / 2f + textHeight,
                paint
            )
            paint.apply {
                strokeWidth = 5f
                color = Color.RED
                style = Paint.Style.STROKE
            }

            it.drawRect(
                marginX + cellWidth,
                marginY + 2 * cellWidth,
                marginX + cellWidth * 11,
                marginY + 12 * cellWidth,
                paint
            )

            paint.apply {
                strokeWidth = 5f
                color = Color.GREEN
                style = Paint.Style.STROKE
            }
            it.drawRect(
                marginX + cellWidth,
                marginY + 15 * cellWidth,
                marginX + cellWidth * 11,
                marginY + 25 * cellWidth,
                paint
            )

            drawTime(it)


        }

        postInvalidateOnAnimation()
    }

    private fun checkIfShipIsSunk(field: Int, ship: Ship) {
        val shipCells = mutableSetOf<IntArray>()
        if (!ship.rotate) {
            for (i in 0 until ship.type) {
                shipCells.add(intArrayOf(field, ship.x + i, ship.y))
            }
        } else {
            for (i in 0 until ship.type) {
                shipCells.add(intArrayOf(field, ship.x, ship.y + i))
            }
        }
        if (hits.containsAllIntArrays(shipCells)) {
            if (!ship.rotate) {
                if (ship.y > 0) {
                    if (ship.x > 0) {
                        crosses.addIntArray(intArrayOf(field, ship.x - 1, ship.y - 1))
                    }
                    if (ship.x + ship.type - 1 < 9) {
                        crosses.addIntArray(intArrayOf(field, ship.x + ship.type, ship.y - 1))
                    }
                    for (i in 0 until ship.type) {
                        crosses.addIntArray(intArrayOf(field, ship.x + i, ship.y - 1))
                    }
                }
                if (ship.y < 9) {
                    if (ship.x > 0) {
                        crosses.addIntArray(intArrayOf(field, ship.x - 1, ship.y + 1))
                    }
                    if (ship.x + ship.type - 1 < 9) {
                        crosses.addIntArray(intArrayOf(field, ship.x + ship.type, ship.y + 1))
                    }
                    for (i in 0 until ship.type) {
                        crosses.addIntArray(intArrayOf(field, ship.x + i, ship.y + 1))
                    }
                }
                if (ship.x > 0) {
                    crosses.addIntArray(intArrayOf(field, ship.x - 1, ship.y))
                }
                if (ship.x + ship.type - 1 < 9) {
                    crosses.addIntArray(intArrayOf(field, ship.x + ship.type, ship.y))
                }
            } else {
                if (ship.x > 0) {
                    if (ship.y > 0) {
                        crosses.addIntArray(intArrayOf(field, ship.x - 1, ship.y - 1))
                    }
                    if (ship.y + ship.type - 1 < 9) {
                        crosses.addIntArray(intArrayOf(field, ship.x - 1, ship.y + ship.type))
                    }
                    for (i in 0 until ship.type) {
                        crosses.addIntArray(intArrayOf(field, ship.x - 1, ship.y + i))
                    }
                }
                if (ship.x < 9) {
                    if (ship.y > 0) {
                        crosses.addIntArray(intArrayOf(field, ship.x + 1, ship.y - 1))
                    }
                    if (ship.y + ship.type - 1 < 9) {
                        crosses.addIntArray(intArrayOf(field, ship.x + 1, ship.y + ship.type))
                    }
                    for (i in 0 until ship.type) {
                        crosses.addIntArray(intArrayOf(field, ship.x + 1, ship.y + i))
                    }
                }
                if (ship.y > 0) {
                    crosses.addIntArray(intArrayOf(field, ship.x, ship.y - 1))
                }
                if (ship.y + ship.type - 1 < 9) {
                    crosses.addIntArray(intArrayOf(field, ship.x, ship.y + ship.type))
                }
            }
        }
    }

    private fun drawProgress(canvas: Canvas, color: Int, field: Int, percent: Float) {
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

        if (field == OTHER_PLAYER_FIELD) {
            point1Y += 13 * cellWidth
            point2Y += 13 * cellWidth
            point3Y += 13 * cellWidth
            point4Y += 13 * cellWidth
        }

        if (percent > 12.5f) {
            canvas.drawLine(point1X + (point2X - point1X) / 2f, point1Y, point2X, point2Y, paint)
            if (percent > 37.5f) {
                canvas.drawLine(point2X, point2Y, point3X, point3Y, paint)
                if (percent > 62.5f) {
                    canvas.drawLine(point3X, point3Y, point4X, point4Y, paint)
                    if (percent > 87.5f) {
                        canvas.drawLine(point4X, point4Y, point1X, point1Y, paint)
                        canvas.drawLine(
                            point1X,
                            point1Y,
                            point1X + 5 * cellWidth * (percent - 87.5f) / 12.5f,
                            point1Y,
                            paint
                        )
                    } else {
                        canvas.drawLine(
                            point4X,
                            point4Y,
                            point4X,
                            point4Y - 10 * cellWidth * (percent - 62.5f) / 25f,
                            paint
                        )
                    }
                } else {
                    canvas.drawLine(
                        point3X,
                        point3Y,
                        point3X - 10 * cellWidth * (percent - 37.5f) / 25f,
                        point3Y,
                        paint
                    )
                }
            } else {
                canvas.drawLine(
                    point2X,
                    point2Y,
                    point2X,
                    point2Y + 10 * cellWidth * (percent - 12.5f) / 25f,
                    paint
                )
            }
        } else {
            canvas.drawLine(
                point1X + (point2X - point1X) / 2f,
                point1Y,
                point1X + (point2X - point1X) / 2f + 5 * cellWidth * percent / 12.5f,
                point1Y,
                paint
            )
        }

    }

    private fun drawCross(canvas: Canvas, field: Int, x: Int, y: Int) {
        val paint = Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 4f
            color = ContextCompat.getColor(
                context,
                if (getShipByCoords(
                        field,
                        x,
                        y
                    ) == null
                ) R.color.foregroundColor else R.color.backgroundColor
            )
        }
        val plY = if (field == OTHER_PLAYER_FIELD) 13 else 0
        canvas.drawLine(
            marginX + cellWidth * (x + 1) + 7,
            marginY + cellWidth * (y + 2 + plY) + 7,
            marginX + cellWidth * (x + 2) - 7,
            marginY + cellWidth * (y + 3 + plY) - 7,
            paint
        )
        canvas.drawLine(
            marginX + cellWidth * (x + 1) + 7,
            marginY + cellWidth * (y + 3 + plY) - 7,
            marginX + cellWidth * (x + 2) - 7,
            marginY + cellWidth * (y + 2 + plY) + 7,
            paint
        )
    }

    private fun getShipByCoords(field: Int, x: Int, y: Int): Ship? {
        gameService?.let {
            for (ship in (if (field == THIS_PLAYER_FIELD) it.thisPlayerShips else it.otherPlayerShips)) {
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
        }
        return null
    }

    private fun drawTime(canvas: Canvas) {
        if (turn != -1 && turnStartTime != (-1).toLong()) {
            drawProgress(
                canvas,
                ContextCompat.getColor(context, R.color.foregroundColor),
                if (turn == OTHER_PLAYER_TURN) THIS_PLAYER_FIELD else OTHER_PLAYER_FIELD,
                (System.currentTimeMillis() - turnStartTime) / timeBound.toFloat() / 10f
            )
            drawProgress(
                canvas,
                ContextCompat.getColor(context, R.color.foregroundColor),
                if (turn == THIS_PLAYER_TURN) THIS_PLAYER_FIELD else OTHER_PLAYER_FIELD,
                100f
            )
        } else {
            drawProgress(
                canvas,
                ContextCompat.getColor(context, R.color.foregroundColor),
                THIS_PLAYER_FIELD,
                100f
            )
            drawProgress(
                canvas,
                ContextCompat.getColor(context, R.color.foregroundColor),
                OTHER_PLAYER_FIELD,
                100f
            )
        }
    }

    private fun drawShips(canvas: Canvas, field: Int, ships: List<Ship>) {
        val paint = Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, R.color.foregroundColor)
        }
        val plY = if (field == THIS_PLAYER_FIELD) 0 else 13
        for (ship in ships) {
            if (ship.rotate) {
                canvas.drawRect(
                    marginX + cellWidth * (ship.x + 1),
                    marginY + cellWidth * (ship.y + 2 + plY),
                    marginX + cellWidth * (ship.x + 2),
                    marginY + cellWidth * (ship.y + ship.type + 2 + plY),
                    paint
                )
            } else {
                canvas.drawRect(
                    marginX + cellWidth * (ship.x + 1),
                    marginY + cellWidth * (ship.y + 2 + plY),
                    marginX + cellWidth * (ship.x + ship.type + 1),
                    marginY + cellWidth * (ship.y + 3 + plY),
                    paint
                )
            }
        }
    }

    fun start() {
        turnStartTime = System.currentTimeMillis()
    }

    fun setFirstTurn(firstTurn: Int) {
        turn = firstTurn
    }

    fun init(otherPlayerName: String, timeBound: Int, gameService: GameService) {
        this.otherPlayerName = otherPlayerName
        this.timeBound = timeBound
        this.gameService = gameService
    }


    private fun getCoordsByLocation(x: Float, y: Float): IntArray {
        val absoluteX = x - marginX - cellWidth
        val absoluteY = y - marginY - cellWidth * 2
        if (absoluteX < 0 || absoluteX > cellWidth * 10 || absoluteY < 0 || absoluteY > cellWidth * 23 || (absoluteY > 10 && absoluteY < 13)) {
            return intArrayOf(-1, -1, -1)
        }
        return if (absoluteY > cellWidth * 10) {
            intArrayOf(
                OTHER_PLAYER_FIELD,
                floor(absoluteX / cellWidth).toInt(),
                floor(absoluteY / cellWidth).toInt() - 13
            )
        } else {
            intArrayOf(
                THIS_PLAYER_FIELD,
                floor(absoluteX / cellWidth).toInt(),
                floor(absoluteY / cellWidth).toInt()
            )
        }
    }

    private fun isClickInField(field: Int, x: Float, y: Float): Boolean {
        return x > marginX + cellWidth && x < marginX + cellWidth * 11 && (if (field == THIS_PLAYER_FIELD) y > marginY + cellWidth * 2 && y < marginY + cellWidth * 12 else y > marginY + cellWidth * 15 && y < marginY + cellWidth * 25)
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        coords = resources.getStringArray(R.array.coords)
        cellWidth = floor(height / 26f)
        marginX = (width - cellWidth * 11) / 2f
        marginY = 16f

        gameService?.apply {
            clickLiveData.observeForever {
                it?.let {
                    crosses.addIntArray(intArrayOf(THIS_PLAYER_FIELD, it.component1(), it.component2()))
                    val ship = getShipByCoords(THIS_PLAYER_FIELD, it.component1(), it.component2())
                    turnStartTime = System.currentTimeMillis()
                    if (ship != null) {
                        hits.add(intArrayOf(THIS_PLAYER_FIELD, it.component1(), it.component2()))
                        checkIfShipIsSunk(THIS_PLAYER_FIELD, ship)
                        Vibrator.vibrate(context, 100)
                    } else {
                        turn = (turn + 1) % 2
                    }
                }
            }
        }
    }
}