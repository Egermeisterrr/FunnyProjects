package com.example.snake

object SnakeHead {

    var nextMove: () -> Unit = {}
    var flag = true

    private val thread: Thread = Thread{
        while (true) {
            Thread.sleep(500)
            if (flag) {
                nextMove()
            }
        }
    }

    init {
        thread.start()
    }

    fun start() {
        flag = true
    }
}