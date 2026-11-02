package com.tenno.rpg8bit

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import com.tenno.rpg8bit.core.GameView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(GameView(this))
    }
}
