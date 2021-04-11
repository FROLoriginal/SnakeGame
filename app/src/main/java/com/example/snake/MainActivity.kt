package com.example.snake

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TableLayout
import android.widget.TableRow
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity(), ISnakeUI {
    private val height = 16
    private val width = 10
    private val speed = 150L
    private lateinit var game: SnakeGame

    private var scoreActionBar: ActionBar? = null

    private val state = arrayOf(intArrayOf(android.R.attr.state_checked))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scoreActionBar = supportActionBar

        val table: TableLayout = findViewById(R.id.field)
        initField(table)
        val color = intArrayOf(ContextCompat.getColor(this, R.color.design_default_color_error))

        game = SnakeGame(this, table, speed)
        game.mealStyle = ColorStateList(state, color)
        findViewById<Button>(R.id.turnUp).run { setOnClickListener { game.turnUp() } }
        findViewById<Button>(R.id.turnDown).run { setOnClickListener { game.turnDown() } }
        findViewById<Button>(R.id.turnRight).run { setOnClickListener { game.turnRight() } }
        findViewById<Button>(R.id.turnLeft).run { setOnClickListener { game.turnLeft() } }
        game.start()

    }

    override fun onScoreChanged(score: Int) {
        supportActionBar?.title = "Score: $score"
    }

    override fun onGameEnded(score: Int, gameOver: Boolean) {
        AlertDialog.Builder(this)
            .setTitle(if (gameOver) "Game over" else "You won")
            .setMessage("Your score: $score")
            .setPositiveButton("Restart") { _, _ -> game.restart() }
            .setCancelable(false)
            .create()
            .show()

    }

    private fun initField(tableLayout: TableLayout) {
        val color = intArrayOf(ContextCompat.getColor(this, R.color.design_default_color_primary))

        for (y in 0 until height) {
            val tableRow = TableRow(this)
            for (x in 0 until width) {
                val checkBox = CheckBox(this).apply {
                    isClickable = false
                    buttonTintList = ColorStateList(state, color)
                }
                tableRow.addView(checkBox)
            }
            tableLayout.addView(tableRow)
        }
    }
}
