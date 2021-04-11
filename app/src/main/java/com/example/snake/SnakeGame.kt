package com.example.snake

import android.widget.CheckBox
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.view.get
import androidx.core.view.size
import kotlinx.coroutines.*


class SnakeGame(
    private val uiInterface: ISnakeUI,
    private val tableLayout: TableLayout,
    private val snakeSpeed: Long
) : ISnakeGameController {

    private val height = tableLayout.size
    private val width = (tableLayout[0] as TableRow).size
    private val field =
        Array(height) { y -> Array(width) { x -> (tableLayout[y] as TableRow)[x] as CheckBox } }

    private var right = true
    private var left = false
    private var up = false
    private var down = false
    private var isStarted = false
    private var isGameOver = false

    private val currentPoint = Point(0, 0)
    private val body: ArrayList<CheckBox> = arrayListOf(field[currentPoint.y][currentPoint.x])
    var mealStyle = field[currentPoint.y][currentPoint.x].buttonTintList

    private var score: Int = body.size

    private lateinit var job: Job

    fun start() {
        if (!isStarted) {
            uiInterface.onScoreChanged(score)
            job = GlobalScope.launch(Dispatchers.Main) { game() }
        }
    }

    fun restart() {
        job.run { if (isActive) cancel("The game was restarted") }
        right = true
        left = false
        up = false
        down = false
        isStarted = false
        isGameOver = false
        currentPoint.run { x = 0; y = 0 }
        field.forEach { it.forEach { it.isChecked = false } }
        body.run { clear(); add(field[currentPoint.y][currentPoint.x]) }
        score = body.size
        start()
    }


    private suspend fun game() {
        generateMeal()
        while (!isGameOver) {
            delay(snakeSpeed)
            val snakeLength = body.size
            when {
                right -> moveRight()
                left -> moveLeft()
                up -> moveUp()
                down -> moveDown()
            }
            if (snakeLength != body.size) lengthChanged()
        }
    }

    private fun lengthChanged() {
        generateMeal()
        score = body.size
        uiInterface.onScoreChanged(score)
    }

    private fun generateMeal() {
        val temp = field.flatten().toMutableList()
        body.forEach { temp.remove(it) }
        temp.random().run {
            isChecked = true
            buttonTintList = mealStyle
        }
    }

    private fun moveRight() {
        drawMovement(currentPoint.apply { ++x })
    }

    private fun moveLeft() {
        drawMovement(currentPoint.apply { --x })
    }

    private fun moveUp() {
        drawMovement(currentPoint.apply { --y })
    }

    private fun moveDown() {
        drawMovement(currentPoint.apply { ++y })
    }

    private fun drawMovement(nextPoint: Point) {
        if (!isBorderConnected(nextPoint)) {
            if (!isBodyIntersected(nextPoint)) {
                val nextCheckBox = field[nextPoint.y][nextPoint.x]
                nextCheckBox.buttonTintList = body[0].buttonTintList
                if (!nextCheckBox.isChecked) {
                    body.removeAt(body.lastIndex).isChecked = false
                }
                body.add(0, nextCheckBox.also { it.isChecked = true })
            } else isGameOver = true
        } else isGameOver = true
        if (isGameOver || score == width * height) uiInterface.onGameEnded(score, isGameOver)
    }

    private fun isBorderConnected(nextPoint: Point): Boolean {
        return nextPoint.y !in 0 until height || nextPoint.x !in 0 until width
    }

    private fun isBodyIntersected(nextPoint: Point): Boolean {
        return body.contains(field[nextPoint.y][nextPoint.x])
    }

    override fun turnRight() {
        if (!left) right = true.also { up = !it; down = !it }
    }

    override fun turnLeft() {
        if (!right) left = true.also { up = !it; down = !it }
    }

    override fun turnUp() {
        if (!down) up = true.also { left = !it; right = !it }
    }

    override fun turnDown() {
        if (!up) down = true.also { left = !it; right = !it }
    }

    private data class Point(var x: Int, var y: Int)

}
