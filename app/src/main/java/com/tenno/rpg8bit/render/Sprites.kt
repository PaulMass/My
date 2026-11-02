package com.tenno.rpg8bit.render

import android.graphics.Canvas
import android.graphics.Paint

object Sprites {

    private val paint = Paint()

    fun classPalette(prim: Int, sec: Int, acento: Int): Map<Char, Int> = mapOf(
        'P' to prim, 'S' to sec, 'A' to acento, 'V' to 0xFF18E0FF.toInt(), 'D' to 0xFF263238.toInt()
    )

    val TENNO: Array<String> = arrayOf(
        "................",
        "......AAAA......",
        ".....APPPPA.....",
        "....APPPPPPA....",
        "....PPVVVVPP....",
        "....PPVVVVPP....",
        ".....PPPPPP.....",
        "....PPPPPPPP....",
        "...SPPPPPPPPS...",
        "...SPPDDDDPS....",
        "..SSPPDDPPSS....",
        "..SPDDPPDDPS....",
        "..SPDDPPDDPS....",
        ".....DD..DD.....",
        ".....DD..DD.....",
        "....DDD..DDD...."
    )

    private val GRINEER: Map<Char, Int> = mapOf(
        'G' to 0xFF7A8C3C.toInt(), 'g' to 0xFF5C6B2E.toInt(),
        'V' to 0xFFFF5252.toInt(), 'D' to 0xFF37474F.toInt(), 'B' to 0xFF263238.toInt()
    )

    val LANCER: Array<String> = arrayOf(
        "................",
        ".....GGGG.......",
        "....GGGGGG......",
        "....GGVVGG......",
        "....GGGGGG......",
        ".....GGGG.......",
        "...GGGGGGGG..B..",
        "..GGGGGGGGGG.BB.",
        "..GGDDGGDDGG.BB.",
        "..GGDDGGDDGG....",
        "..GGGGGGGGGG....",
        "...G.GGGG.G.....",
        "...G.GGGG.G.....",
        "..GGG....GGG....",
        "..GG......GG....",
        "..BB......BB...."
    )

    val BUTCHER: Array<String> = arrayOf(
        "................",
        "....BBBB........",
        "...BBBBBB....C..",
        "...BBVVBB...CC..",
        "...BBBBBB..CCC..",
        "....BBBB...CCC..",
        "..BBBBBBBB..CC..",
        ".BBBBBBBBBB.CC..",
        ".BBgBBBBBgBB....",
        ".BBBBBBBBBB.....",
        ".BBBBBBBBBB.....",
        "..BBBBBBBB......",
        "...BB..BB.......",
        "...BB..BB.......",
        "..BBB..BBB......",
        "................"
    )

    private val CORPUS: Map<Char, Int> = mapOf(
        'W' to 0xFFF5F5F5.toInt(), 'B' to 0xFF2196F3.toInt(),
        'V' to 0xFF0D47A1.toInt(), 'D' to 0xFF37474F.toInt(), 'Y' to 0xFFFFD600.toInt()
    )

    val CREWMAN: Array<String> = arrayOf(
        "................",
        ".....WWWW.......",
        "....WWWWWW......",
        "....WVVVVW......",
        "....WWWWWW......",
        ".....WWWW.......",
        "...BBBBBBBB.....",
        "..BWWBBBBWWB....",
        "..BWBBBBBBWB....",
        "..BWYBBBBYBW....",
        "..BBBBBBBBBB....",
        "...BB....BB.....",
        "...BB....BB.....",
        "..BBB....BBB....",
        "................",
        "................"
    )

    val MOA: Array<String> = arrayOf(
        "................",
        "..WW............",
        ".WWWW....DDD....",
        ".WVVWW..DDDDD...",
        "..WWWWWWDDDD....",
        "..BBWWWWWW......",
        "...BBBBBB.......",
        "..BB..BB........",
        ".BBB..BBB.......",
        ".BB....BB.......",
        "................",
        "................",
        "................",
        "................",
        "................",
        "................"
    )

    private val INFESTED: Map<Char, Int> = mapOf(
        'F' to 0xFFB0677A.toInt(), 'f' to 0xFF8A4B5C.toInt(),
        'V' to 0xFFFFEB3B.toInt(), 'D' to 0xFF5C2E3C.toInt()
    )

    val RUNNER: Array<String> = arrayOf(
        "................",
        ".....FF.........",
        "....FFFF........",
        "....FVVF........",
        "....FFFF........",
        ".....FF.........",
        "....fFFf........",
        "...FFFFFF.......",
        "..FF.FF.FF......",
        "..F...F...F.....",
        "..F...F...F.....",
        "...F..F..F......",
        "...FF....FF.....",
        "..FFF....FFF....",
        "................",
        "................"
    )

    val ANCIENT: Array<String> = arrayOf(
        "................",
        "....FFFFFFFF....",
        "...FFFFFFFFFF...",
        "..FFFVVFFVVFFF..",
        "..FFFFFFFFFFFF..",
        "..FF.FVVVVF.FF..",
        "...FFFFFFFFFF...",
        "..FF..FFFF..FF..",
        ".FFF..FFFF..FFF.",
        ".FF...FFFF...FF.",
        "..FF...FF...FF..",
        "..FFF..FF..FFF..",
        "...FF......FF...",
        "...FF......FF...",
        "................",
        "................"
    )

    private val JACKAL: Map<Char, Int> = mapOf(
        'M' to 0xFF90A4AE.toInt(), 'm' to 0xFF607D8B.toInt(),
        'K' to 0xFF37474F.toInt(), 'V' to 0xFFFF1744.toInt(), 'L' to 0xFF455A64.toInt()
    )

    val JACKAL_SPR: Array<String> = arrayOf(
        "........................",
        "......MMMM..............",
        ".....MMMMMM.............",
        "....MMKVVKMM............",
        "....MMMMMMMM............",
        "...MMMMMMMMMM...........",
        "...MMMMMMMMMMM..........",
        "..MMMMMMMMMMMM..........",
        "..MM.MMMMMM.MM..........",
        ".MMM.MMMMMM.MMM..LL.....",
        ".MMM.MMMMMM.MMM.LLLL....",
        "..MMMMMMMMMMMM.LLLL.....",
        "..MMMMMMMMMMMM..........",
        "...MM..MM..MM...........",
        "...MM..MM..MM...........",
        "..MMM..MM..MMM..........",
        "..MMM..MM..MMM..........",
        "..MM....MM....MM........",
        ".MMM....MM....MMM.......",
        "........................",
        "........................",
        "........................",
        "........................"
    )

    private val VAYHEK: Map<Char, Int> = mapOf(
        'A' to 0xFFD4AF37.toInt(), 'F' to 0xFFEDE7D8.toInt(),
        'V' to 0xFFB71C1C.toInt(), 'D' to 0xFF6D4C41.toInt()
    )

    val VAYHEK_SPR: Array<String> = arrayOf(
        "........................",
        "........AAAA............",
        ".......AAAAAA...........",
        "......AAAAAAAA..........",
        ".......FFFFFF...........",
        "......FFVVVVFF..........",
        "......FFFFFFFF..........",
        ".......FFFFFF...........",
        ".....FFFFFFFFFF.........",
        "....FFFFFFFFFFF.........",
        "...FFFFFFFFFFFFF........",
        "...FF.FFFFFFF.FF........",
        "..FFF.FFFFFFF.FFF.......",
        "..FF..FFFFFFF..FF.......",
        "..FF..FFFFFFF..FF.......",
        "..FF...FFFFF...FF.......",
        "...FF...FFF...FF........",
        "...FFF..FFF..FFF........",
        "....FF..FFF..FF.........",
        "....FF..FFF..FF.........",
        "....FFF.FFF.FFF.........",
        ".....FF.F.F.FF..........",
        "........................",
        "........................"
    )

    fun enemySprite(id: String): Pair<Array<String>, Map<Char, Int>> = when (id) {
        "grineer" -> LANCER to GRINEER
        "carniceiro" -> BUTCHER to GRINEER
        "corpus" -> CREWMAN to CORPUS
        "moa" -> MOA to CORPUS
        "infestado" -> RUNNER to INFESTED
        "anciao" -> ANCIENT to INFESTED
        "jackal" -> JACKAL_SPR to JACKAL
        "vayhek" -> VAYHEK_SPR to VAYHEK
        else -> LANCER to GRINEER
    }

    fun draw(
        c: Canvas, rows: Array<String>, pal: Map<Char, Int>,
        x: Int, y: Int, scale: Int = 1
    ) {
        for (ry in rows.indices) {
            val row = rows[ry]
            for (rx in row.indices) {
                val col = pal[row[rx]] ?: continue
                paint.color = col
                c.drawRect(
                    (x + rx * scale).toFloat(), (y + ry * scale).toFloat(),
                    (x + (rx + 1) * scale).toFloat(), (y + (ry + 1) * scale).toFloat(), paint
                )
            }
        }
    }

    fun lotus(c: Canvas, x: Int, y: Int, scale: Int, gold: Int, dark: Int) {
        paint.color = gold
        rect(c, x + 9 * scale, y, 6 * scale, 18 * scale)
        paint.color = dark
        rect(c, x, y + 16 * scale, 7 * scale, 13 * scale)
        rect(c, x + 17 * scale, y + 16 * scale, 7 * scale, 13 * scale)
        paint.color = gold
        rect(c, x + 6 * scale, y + 12 * scale, 4 * scale, 10 * scale)
        rect(c, x + 3 * scale, y + 24 * scale, 18 * scale, 5 * scale)
    }

    private fun rect(c: Canvas, x: Int, y: Int, w: Int, h: Int) {
        c.drawRect(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), paint)
    }
}
