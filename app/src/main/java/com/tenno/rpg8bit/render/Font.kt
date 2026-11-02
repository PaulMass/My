package com.tenno.rpg8bit.render

import android.graphics.Canvas
import android.graphics.Paint

object Font {

    const val W = 3
    const val H = 8
    const val ADV = 4

    private val G: Map<Char, Array<String>> = mapOf(
        'A' to arrayOf("010", "101", "111", "101", "101"),
        'B' to arrayOf("110", "101", "110", "101", "110"),
        'C' to arrayOf("011", "100", "100", "100", "011"),
        'D' to arrayOf("110", "101", "101", "101", "110"),
        'E' to arrayOf("111", "100", "110", "100", "111"),
        'F' to arrayOf("111", "100", "110", "100", "100"),
        'G' to arrayOf("011", "100", "101", "101", "011"),
        'H' to arrayOf("101", "101", "111", "101", "101"),
        'I' to arrayOf("111", "010", "010", "010", "111"),
        'J' to arrayOf("011", "001", "001", "101", "010"),
        'K' to arrayOf("101", "101", "110", "101", "101"),
        'L' to arrayOf("100", "100", "100", "100", "111"),
        'M' to arrayOf("101", "111", "111", "111", "101"),
        'N' to arrayOf("101", "111", "111", "101", "101"),
        'O' to arrayOf("010", "101", "101", "101", "010"),
        'P' to arrayOf("110", "101", "110", "100", "100"),
        'Q' to arrayOf("010", "101", "101", "110", "011"),
        'R' to arrayOf("110", "101", "110", "101", "101"),
        'S' to arrayOf("011", "100", "010", "001", "110"),
        'T' to arrayOf("111", "010", "010", "010", "010"),
        'U' to arrayOf("101", "101", "101", "101", "111"),
        'V' to arrayOf("101", "101", "101", "101", "010"),
        'W' to arrayOf("101", "101", "111", "111", "101"),
        'X' to arrayOf("101", "101", "010", "101", "101"),
        'Y' to arrayOf("101", "101", "010", "010", "010"),
        'Z' to arrayOf("111", "001", "010", "100", "111"),
        '0' to arrayOf("111", "101", "101", "101", "111"),
        '1' to arrayOf("010", "110", "010", "010", "111"),
        '2' to arrayOf("110", "001", "010", "100", "111"),
        '3' to arrayOf("110", "001", "010", "001", "110"),
        '4' to arrayOf("101", "101", "111", "001", "001"),
        '5' to arrayOf("111", "100", "110", "001", "110"),
        '6' to arrayOf("011", "100", "110", "101", "011"),
        '7' to arrayOf("111", "001", "010", "010", "010"),
        '8' to arrayOf("010", "101", "010", "101", "010"),
        '9' to arrayOf("111", "101", "111", "001", "110"),
        ' ' to arrayOf("000", "000", "000", "000", "000"),
        '.' to arrayOf("000", "000", "000", "000", "010"),
        ',' to arrayOf("000", "000", "000", "010", "100"),
        '!' to arrayOf("010", "010", "010", "000", "010"),
        '?' to arrayOf("110", "001", "010", "000", "010"),
        ':' to arrayOf("000", "010", "000", "010", "000"),
        ';' to arrayOf("000", "010", "000", "010", "100"),
        '-' to arrayOf("000", "000", "111", "000", "000"),
        '+' to arrayOf("000", "010", "111", "010", "000"),
        '/' to arrayOf("001", "001", "010", "100", "100"),
        '(' to arrayOf("001", "010", "010", "010", "001"),
        ')' to arrayOf("100", "010", "010", "010", "100"),
        '%' to arrayOf("101", "001", "010", "100", "101"),
        '\'' to arrayOf("010", "010", "000", "000", "000"),
        '>' to arrayOf("100", "010", "001", "010", "100"),
        '<' to arrayOf("001", "010", "100", "010", "001"),
        '_' to arrayOf("000", "000", "000", "000", "111"),
        '=' to arrayOf("000", "111", "000", "111", "000"),
        '*' to arrayOf("000", "101", "010", "101", "000"),
        '♥' to arrayOf("010", "111", "111", "010", "000")
    )

    private val ACUTE = setOf('Á', 'É', 'Í', 'Ó', 'Ú')
    private val GRAVE = setOf('À')
    private val TILDE = setOf('Ã', 'Õ')
    private val CIRC = setOf('Â', 'Ê', 'Ô')
    private val CEDILLA = setOf('Ç')

    private val paint = Paint()

    fun width(text: String, scale: Int): Int =
        if (text.isEmpty()) 0 else text.length * ADV * scale - scale

    fun draw(c: Canvas, rawText: String, x: Int, y: Int, scale: Int, color: Int) {
        paint.color = color
        var cx = x
        for (ch0 in rawText) {
            val ch = ch0.uppercaseChar()
            var dy = 0
            when {
                ch in ACUTE -> { drawRow(c, "001", cx, y, scale, paint); drawRow(c, "010", cx, y + scale, scale, paint); dy = 2 }
                ch in GRAVE -> { drawRow(c, "100", cx, y, scale, paint); drawRow(c, "010", cx, y + scale, scale, paint); dy = 2 }
                ch in CIRC -> { drawRow(c, "010", cx, y, scale, paint); drawRow(c, "101", cx, y + scale, scale, paint); dy = 2 }
                ch in TILDE -> { drawRow(c, "110", cx, y, scale, paint); drawRow(c, "011", cx, y + scale, scale, paint); dy = 2 }
            }
            val base = G[ch] ?: G['?']!!
            for (row in 0 until 5) {
                drawRow(c, base[row], cx, y + (dy + row) * scale, scale, paint)
            }
            if (ch in CEDILLA) {
                drawRow(c, "010", cx, y + 7 * scale, scale, paint)
            }
            cx += ADV * scale
        }
    }

    fun drawCentered(c: Canvas, text: String, cx: Int, y: Int, scale: Int, color: Int) {
        draw(c, text, cx - width(text, scale) / 2, y, scale, color)
    }

    private fun drawRow(c: Canvas, row: String, x: Int, y: Int, scale: Int, p: Paint) {
        for (i in 0 until 3) {
            if (row[i] == '1') {
                c.drawRect(
                    (x + i * scale).toFloat(), y.toFloat(),
                    (x + (i + 1) * scale).toFloat(), (y + scale).toFloat(), p
                )
            }
        }
    }

    fun wrap(text: String, maxChars: Int): List<String> {
        val words = text.split(' ')
        val lines = mutableListOf<String>()
        var line = ""
        for (w in words) {
            if (line.isEmpty()) {
                line = w
            } else if (line.length + 1 + w.length <= maxChars) {
                line += " " + w
            } else {
                lines.add(line)
                line = w
            }
        }
        if (line.isNotEmpty()) lines.add(line)
        return lines
    }
}
