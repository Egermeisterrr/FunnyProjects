package com.example.snake

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

const val SIZE = 100

class MainActivity : AppCompatActivity() {

    private val tails = mutableListOf<Tail>()

    private val android by lazy {
        ImageView(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cont = findViewById<FrameLayout>(R.id.cont)
        cont.layoutParams = FrameLayout.LayoutParams(SIZE * 15, SIZE * 19)

        val snakeHead = View(this)
        snakeHead.background = ContextCompat.getDrawable(this, R.drawable.snake_head)
        snakeHead.layoutParams = FrameLayout.LayoutParams(SIZE, SIZE)

        SnakeHead.start()
        makeAndroids(cont)
        SnakeHead.nextMove = { move(Directions.DOWN, snakeHead, cont) }

        val buttonUp: ImageView = findViewById(R.id.up)
        val buttonDown: ImageView = findViewById(R.id.down)
        val buttonLeft: ImageView = findViewById(R.id.left)
        val buttonRight: ImageView = findViewById(R.id.right)
        val buttonPause: ImageView = findViewById(R.id.pause)

        buttonUp.setOnClickListener {
            SnakeHead.nextMove =
                { move(Directions.UP, snakeHead, cont) }
        }

        buttonDown.setOnClickListener {
            SnakeHead.nextMove =
                { move(Directions.DOWN, snakeHead, cont) }
        }

        buttonLeft.setOnClickListener {
            SnakeHead.nextMove =
                { move(Directions.LEFT, snakeHead, cont) }
        }

        buttonRight.setOnClickListener {
            SnakeHead.nextMove =
                { move(Directions.RIGHT, snakeHead, cont) }
        }

        buttonPause.setOnClickListener {
            SnakeHead.flag = !SnakeHead.flag
            if (SnakeHead.flag) {
                buttonPause.setImageResource(R.drawable.ic_pause)
            }
            else {
                buttonPause.setImageResource(R.drawable.ic_play)
            }
        }
    }

    private fun move(directions: Directions, head: View, layout: FrameLayout) {
        when (directions) {
            Directions.UP -> (head.layoutParams as FrameLayout.LayoutParams).topMargin -= SIZE
            Directions.DOWN -> (head.layoutParams as FrameLayout.LayoutParams).topMargin += SIZE
            Directions.LEFT -> (head.layoutParams as FrameLayout.LayoutParams).leftMargin -= SIZE
            Directions.RIGHT -> (head.layoutParams as FrameLayout.LayoutParams).leftMargin += SIZE
        }

        runOnUiThread {
            if(gameOver(head)) {
                SnakeHead.flag = false
                score()
                return@runOnUiThread
            }

            tailMoving(head.top, head.left, layout)
            eat(head, layout)
            layout.removeView(head)
            layout.addView(head)
        }
    }

    private fun score() {
        AlertDialog.Builder(this)
            .setTitle("GAME OVER!\nYour score: ${tails.size}")
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                this.recreate()
            }
            .create()
            .show()
    }

    private fun gameOver(snakeHead: View) : Boolean{
        for (tale in tails) {
            if (tale.left == snakeHead.left && tale.top == snakeHead.top) {
                return true
            }
        }

        if (snakeHead.top < 0 || snakeHead.left < 0 || snakeHead.left > SIZE * 15
            || snakeHead.top > SIZE * 19 ) {
            return true
        }

        return false
    }

    private fun makeAndroids(layout: FrameLayout) {
        android.layoutParams = FrameLayout.LayoutParams(SIZE, SIZE)
        android.setImageResource(R.drawable.ic_android)
        (android.layoutParams as FrameLayout.LayoutParams).topMargin = (0..9).random() * SIZE
        (android.layoutParams as FrameLayout.LayoutParams).leftMargin = (0..9).random() * SIZE

        runOnUiThread {
            layout.removeView(android)
            layout.addView(android)
        }
    }

    private fun eat(head: View, layout: FrameLayout) {
        if (head.left == android.left && head.top == android.top) {
            makeAndroids(layout)
            addTail(head.top, head.left, layout)
        }
    }

    private fun addTail(top: Int, left: Int, layout: FrameLayout) {
        val talePart = drawTail(top, left, layout)
        tails.add(Tail(top, left, talePart))
    }

    private fun drawTail(top: Int, left: Int, layout: FrameLayout): View {
        val taleImage = View(this)
        taleImage.background = ContextCompat.getDrawable(this, R.drawable.tail)
        taleImage.layoutParams = FrameLayout.LayoutParams(SIZE, SIZE)
        (taleImage.layoutParams as FrameLayout.LayoutParams).topMargin = top
        (taleImage.layoutParams as FrameLayout.LayoutParams).leftMargin = left

        layout.addView(taleImage)
        return taleImage
    }

    private fun tailMoving(headTop: Int, headLeft: Int, layout: FrameLayout) {
        var tempTalePart: Tail? = null
        for (index in 0 until tails.size) {
            val talePart = tails[index]
            layout.removeView(talePart.view)
            if (index == 0) {
                tempTalePart = talePart
                tails[index] = Tail(headTop, headLeft, drawTail(headTop, headLeft, layout))
            }
            else {
                val anotherTempPartOfTale = tails[index]
                tempTalePart?.let {
                    tails[index] = Tail(it.top, it.left, drawTail(it.top, it.left, layout))
                }
                tempTalePart = anotherTempPartOfTale
            }
        }
    }
}

enum class Directions {
    UP,
    DOWN,
    LEFT,
    RIGHT
}