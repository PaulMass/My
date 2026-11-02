package com.tenno.rpg8bit.core

class Input {
    enum class Act { UP, DOWN, LEFT, RIGHT, A, B }

    private val down = mutableSetOf<Act>()
    private val pressed = mutableSetOf<Act>()

    fun press(a: Act) {
        if (down.add(a)) pressed.add(a)
    }

    fun release(a: Act) {
        down.remove(a)
    }

    fun isDown(a: Act): Boolean = down.contains(a)

    fun wasPressed(a: Act): Boolean {
        if (pressed.contains(a)) {
            pressed.remove(a)
            return true
        }
        return false
    }

    fun clearFrame() {
        pressed.clear()
    }
}
