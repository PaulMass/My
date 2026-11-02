package com.tenno.rpg8bit.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    val input = Input()
    private val game = Game(input)

    private val buffer = Bitmap.createBitmap(Game.W, Game.H, Bitmap.Config.ARGB_8888)
    private val bufferCanvas = Canvas(buffer)
    private val paint = Paint().apply { isFilterBitmap = false }

    private var thread: GameThread? = null
    private var scale = 1
    private var offX = 0
    private var offY = 0

    init {
        holder.addCallback(this)
        isFocusable = true
        isFocusableInTouchMode = true
        requestFocus()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        startThread()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        scale = maxOf(1, minOf(width / Game.W, height / Game.H))
        offX = (width - Game.W * scale) / 2
        offY = (height - Game.H * scale) / 2
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopThread()
    }

    private fun startThread() {
        stopThread()
        val t = GameThread()
        thread = t
        t.start()
    }

    private fun stopThread() {
        val t = thread ?: return
        t.running = false
        var retry = true
        while (retry) {
            try {
                t.join()
                retry = false
            } catch (e: InterruptedException) {
                // tenta de novo
            }
        }
        thread = null
    }

    private inner class GameThread : Thread() {
        var running = true

        override fun run() {
            while (running) {
                val start = System.currentTimeMillis()
                try {
                    game.update()
                    val c = holder.lockCanvas()
                    if (c != null) {
                        synchronized(holder) {
                            game.render(bufferCanvas)
                            c.drawColor(Color.BLACK)
                            c.drawBitmap(
                                buffer,
                                Rect(0, 0, Game.W, Game.H),
                                Rect(offX, offY, offX + Game.W * scale, offY + Game.H * scale),
                                paint
                            )
                        }
                        holder.unlockCanvasAndPost(c)
                    }
                } catch (e: Exception) {
                    // um frame ruim nao derruba o jogo
                }
                val elapsed = System.currentTimeMillis() - start
                val sleep = 33 - elapsed
                if (sleep > 0) {
                    try {
                        Thread.sleep(sleep)
                    } catch (e: InterruptedException) {
                        // ignorado
                    }
                }
                input.clearFrame()
            }
        }
    }

    private fun buttonAt(x: Int, y: Int): Input.Act? {
        val dax = x - 302
        val day = y - 156
        if (dax * dax + day * day <= 14 * 14) return Input.Act.A
        val dbx = x - 274
        val dby = y - 164
        if (dbx * dbx + dby * dby <= 12 * 12) return Input.Act.B
        if (x in 11..37 && y in 125..141) return Input.Act.UP
        if (x in 11..37 && y in 151..167) return Input.Act.DOWN
        if (x in 3..19 && y in 133..159) return Input.Act.LEFT
        if (x in 29..45 && y in 133..159) return Input.Act.RIGHT
        return null
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val active = mutableSetOf<Input.Act>()
        for (i in 0 until event.pointerCount) {
            val x = ((event.getX(i) - offX) / scale).toInt()
            val y = ((event.getY(i) - offY) / scale).toInt()
            val a = buttonAt(x, y)
            if (a != null) active.add(a)
        }
        for (act in Input.Act.values()) {
            if (active.contains(act)) input.press(act) else input.release(act)
        }
        return true
    }

    private fun keyToAct(keyCode: Int): Input.Act? = when (keyCode) {
        KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_W -> Input.Act.UP
        KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_S -> Input.Act.DOWN
        KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_A -> Input.Act.LEFT
        KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_D -> Input.Act.RIGHT
        KeyEvent.KEYCODE_Z, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_SPACE -> Input.Act.A
        KeyEvent.KEYCODE_X, KeyEvent.KEYCODE_BACK -> Input.Act.B
        else -> null
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val act = keyToAct(keyCode) ?: return super.onKeyDown(keyCode, event)
        if (event.repeatCount == 0) input.press(act)
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        val act = keyToAct(keyCode) ?: return super.onKeyUp(keyCode, event)
        input.release(act)
        return true
    }
}
