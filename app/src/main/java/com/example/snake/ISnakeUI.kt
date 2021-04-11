package com.example.snake

interface ISnakeUI {

    fun onScoreChanged(score: Int)
    fun onGameEnded(score: Int, gameOver: Boolean)
}
